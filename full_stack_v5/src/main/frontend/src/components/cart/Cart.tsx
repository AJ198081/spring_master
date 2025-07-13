import {useProductStore} from "../../store/ProductStore.tsx";
import {ProductImage} from "../ProductImage.tsx";
import {deleteCartItem} from "../../services/CartService.ts";
import {toast, ToastContainer} from "react-toastify";
import CartItemUpdater from "../product/CartItemUpdater.tsx";

export const Cart = () => {

    const cartForThisCustomer = useProductStore(state => state.cartForThisCustomer);
    const setCartForThisCustomer = useProductStore(state => state.setCartForThisCustomer);

    const removeCartItem = (cartId: number, cartItemId: number) => {
        console.log(`removeCartItem ${cartId} ${cartItemId}`);
        deleteCartItem(cartId, cartItemId)
            .then(updatedCart => {
                setCartForThisCustomer(updatedCart);
                toast.success(`Cart item Id ${cartItemId} removed successfully`);
            })
            .catch(error => {
                toast.error(`Error removing cart item; issue is - ${error.response?.data?.detail}`);
            });
    };
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
                {cartForThisCustomer?.cartItems.map(item => (
                    <tr key={item.id}>
                        <td
                            className={"cart-image"}
                            style={{width: '100px'}}
                        >
                            <ProductImage imageDownloadUrl={item.product?.images[0]?.downloadUrl}/>
                        </td>
                        <td>{item.product?.name}</td>
                        <td>{item.product?.brand}</td>
                        <td>{item.unitPrice?.toFixed(2)}</td>
                        {/*<td>{item.quantity}</td>*/}
                        <td
                            className={"text-center"}
                            style={{width: '20px'}}
                        >
                            <CartItemUpdater
                                cartId={item.id}
                                productId={item.product.id}
                                initialQuantity={item.quantity || 0}
                                maxQuantity={item.product?.inventory}
                            />
                        </td>
                        <td>{item.total?.toFixed(2)}</td>
                        <td className={"text-center"}>
                            <div className={`d-flex justify-content-around`}>
                                <button
                                    className={"btn btn-primary"}
                                >
                                    Edit
                                </button>
                                <button
                                    className={"btn btn-danger"}
                                    onClick={() => removeCartItem(cartForThisCustomer?.id, item.id)}
                                >
                                    Remove
                                </button>
                            </div>
                        </td>
                    </tr>
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