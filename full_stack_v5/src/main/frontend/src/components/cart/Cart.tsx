import {useProductStore} from "../../store/ProductStore.tsx";
import {toast, ToastContainer} from "react-toastify";
import {CartItemComponent} from "./CartItemComponent.tsx";
import {placeOrder} from "../../services/OrderService.ts";
import {Link} from "react-router-dom";

export const Cart = () => {

    const cartForThisCustomer = useProductStore(state => state.cartForThisCustomer);
    const setCartForThisCustomer = useProductStore(state => state.setCartForThisCustomer);
    const updateCustomerOrders = useProductStore(state => state.updateOrderForThisCustomer);

    const handlePlaceOrder = () => {
        if (cartForThisCustomer?.cartItems?.length === 0) {
            toast.error('Your cart is empty, place some items in cart to place order');
        }

        placeOrder(cartForThisCustomer!.customer.id)
            .then(placedOrder => {
                toast.success(`Order placed successfully. Order Id - ${placedOrder.id}`);
                updateCustomerOrders(placedOrder);
                setCartForThisCustomer(null);
            })
            .catch(error => {
                toast.error(`Error placing order; issue is - ${error.response?.data?.detail}`);
            })
    }

    return (
        <div className={"container"}>
            <ToastContainer
                position={"bottom-right"}
                autoClose={5000}
                newestOnTop={false}
                closeOnClick
                rtl={false}
                pauseOnFocusLoss
                pauseOnHover
            />
            <table className={"table table-striped caption-top align-middle text-center"}>
                <caption className={"h3 text-primary my-4"}>My Cart</caption>
                <thead>
                <tr>
                    <th style={{width: '150px'}}>Product Image</th>
                    <th>Product name</th>
                    <th>Brand</th>
                    <th>Unit Price</th>
                    <th>Quantity</th>
                    <th>Total</th>
                    <th>Action</th>
                </tr>
                </thead>
                <tbody className={"justify-content-between align-items-center"}>
                {
                    cartForThisCustomer?.cartItems?.length === 0
                        ? <tr>
                            <td
                                colSpan={7}
                                className={"text-center text-danger h3"}
                            >
                                <h3>Your cart is empty</h3>
                            </td>
                        </tr>
                        : cartForThisCustomer?.cartItems?.map(item => (
                            <CartItemComponent
                                key={item.id}
                                cartItem={item}
                                cartId={cartForThisCustomer.id}
                            />
                        ))}
                </tbody>
                <tfoot>
                <tr className={"h4"}>
                    <td
                        colSpan={6}
                        className={"text-end"}
                    >Total
                    </td>
                    <td className={"text-center"}>$ {(cartForThisCustomer?.total ?? 0).toFixed(2)}</td>
                </tr>
                </tfoot>
            </table>
            <div
                className={`d-flex justify-content-end gap-3 me-2 my-5`}
            >
                <Link
                    className={`btn btn-outline-info ${cartForThisCustomer?.cartItems?.length === 0 ? 'disabled' : ''}`}
                    to={"/products"}
                >Continue shopping</Link>
                <Link
                    to={"/"}
                    className={` btn btn-success ${cartForThisCustomer?.cartItems?.length || 'disabled'}`}
                    onClick={handlePlaceOrder}
                >Checkout</Link>
            </div>
        </div>
    );
}
