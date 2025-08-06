import { Link } from 'react-router-dom';
import { FaCheckCircle, FaShoppingBag } from 'react-icons/fa';

export const CheckoutSuccess = () => {
    return (
        <div className="container mt-5">
            <div className="row justify-content-center">
                <div className="col-md-8">
                    <div className="card text-center">
                        <div className="card-header bg-success text-white">
                            <h3 className="mb-0">Payment Successful!</h3>
                        </div>
                        <div className="card-body py-5">
                            <div className="mb-4">
                                <FaCheckCircle size={80} className="text-success mb-3" />
                                <h4>Thank you for your purchase</h4>
                                <p className="lead">
                                    Your payment has been processed successfully and your order is now being prepared.
                                </p>
                                <p>
                                    You will receive a confirmation email shortly with your order details.
                                </p>
                            </div>
                            
                            <div className="mt-5">
                                <Link to="/my-orders" className="btn btn-primary btn-lg">
                                    <FaShoppingBag className="me-2" />
                                    View My Orders
                                </Link>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};