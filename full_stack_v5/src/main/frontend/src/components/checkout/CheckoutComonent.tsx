import {useNavigate, useParams} from "react-router-dom";
import {useProductStore} from "../../store/ProductStore.ts";
import {CardElement, useElements, useStripe} from "@stripe/react-stripe-js";
import {useState} from "react";
import {createPaymentIntent} from "../../services/PaymentService.tsx";
import {useCustomerStore} from "../../store/CustomerStore.ts";
import {toast} from "react-toastify";

export const CheckoutComponent = () => {
    const navigate = useNavigate();
    const {customerId} = useParams();
    const customerCart = useProductStore(state => state.cartForThisCustomer);
    const customer = useCustomerStore(state => state.customer);

    const stripe = useStripe();
    const elements = useElements();
    const [isLoading, setIsLoading] = useState<boolean>(false);
    const [error, setError] = useState<string | null>(null);

    const handlePaymentAndOrder = async (event: { preventDefault: () => void; }) => {
        event.preventDefault();
        setIsLoading(true);
        setError(null);

        const cardElement = elements?.getElement(CardElement);

        if (!stripe || !customerId || !customerCart || !elements || !customer || !cardElement) {
            console.log('stripe or customer id or customer dart or card elements null');
            setError("Missing required information for checkout");
            setIsLoading(false);
            return;
        }

        try {
            const clientSecret = await createPaymentIntent({
                amount: customerCart.total,
                customerId: customerCart.id,
                currency: "AUD"
            });

            const paymentIntentResult = await stripe.confirmCardPayment(
                clientSecret,
                {
                    payment_method: {
                        card: cardElement,
                        billing_details: {
                            name: customer.firstName.concat(" ", customer.lastName),
                            email: customer.email,
                            address: {
                                line1: customer.billingAddress.addressLine1,
                                line2: customer.billingAddress.addressLine2,
                                city: customer.billingAddress.city,
                                state: customer.billingAddress.state,
                                country: customer.billingAddress.country,
                                postal_code: customer.billingAddress.postalCode
                            }
                        }
                    }
                }
            )

            if (paymentIntentResult.error) {
                toast.error(`Error processing payment: ${paymentIntentResult.error.message}`);
                setError(paymentIntentResult.error.message || "Payment failed");
                navigate('/checkout/failure');
            } else {
                if (paymentIntentResult.paymentIntent.status === 'succeeded') {
                    toast.success(`Payment successful!`);
                    navigate('/checkout/success');
                }
            }

        } catch (e) {
            console.log(e);
            setError("An unexpected error occurred");
            navigate('/checkout/failure');
        } finally {
            setIsLoading(false);
        }
    };

    const CARD_ELEMENT_OPTIONS = {
        style: {
            base: {
                color: '#32325d',
                fontFamily: '"Helvetica Neue", Helvetica, sans-serif',
                fontSmoothing: 'antialiased',
                fontSize: '16px',
                '::placeholder': {
                    color: '#aab7c4'
                }
            },
            invalid: {
                color: '#fa755a',
                iconColor: '#fa755a'
            }
        }
    };

    return (
        <div className="container mt-5">
            <div className="row justify-content-center">
                <div className="col-md-8">
                    <div className="card">
                        <div className="card-header bg-primary text-white">
                            <h3 className="mb-0">Checkout</h3>
                        </div>
                        <div className="card-body">
                            {customerCart && (
                                <div className="mb-4">
                                    <h4>Order Summary</h4>
                                    <div className="table-responsive">
                                        <table className="table table-bordered">
                                            <thead>
                                            <tr>
                                                <th>Product</th>
                                                <th>Quantity</th>
                                                <th>Price</th>
                                                <th>Total</th>
                                            </tr>
                                            </thead>
                                            <tbody>
                                            {customerCart.cartItems.map(item => (
                                                <tr key={item.id}>
                                                    <td>{item.product.name}</td>
                                                    <td>{item.quantity}</td>
                                                    <td>${item.unitPrice.toFixed(2)}</td>
                                                    <td>${item.total.toFixed(2)}</td>
                                                </tr>
                                            ))}
                                            <tr className="table-active">
                                                <td colSpan={3} className="text-end fw-bold">Total:</td>
                                                <td className="fw-bold">${customerCart.total.toFixed(2)}</td>
                                            </tr>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                            )}

                            <form onSubmit={handlePaymentAndOrder}>
                                <div className="mb-4">
                                    <h4>Payment Information</h4>
                                    <div className="card p-3 mb-3">
                                        <CardElement options={CARD_ELEMENT_OPTIONS} />
                                    </div>
                                    {error && (
                                        <div className="alert alert-danger mt-2">{error}</div>
                                    )}
                                </div>

                                <div className="d-grid gap-2">
                                    <button 
                                        type="submit" 
                                        className="btn btn-primary" 
                                        disabled={isLoading || !stripe}
                                    >
                                        {isLoading ? 'Processing...' : 'Pay Now'}
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}