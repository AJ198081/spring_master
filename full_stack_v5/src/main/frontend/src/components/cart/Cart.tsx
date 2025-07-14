import {useProductStore} from "../../store/ProductStore.tsx";
import {ToastContainer} from "react-toastify";
import {CartItemComponent} from "./CartItemComponent.tsx";

export const Cart = () => {

    const cartForThisCustomer = useProductStore(state => state.cartForThisCustomer);

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
            <table className={"table table-striped table-bordered caption-top align-middle"}>
                <caption className={"h3 text-primary m-4"}>My Cart</caption>
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
                    <td className={"text-center"}>$ {cartForThisCustomer?.total.toFixed(2)}</td>
                </tr>
                </tfoot>
            </table>
        </div>
    );
}
