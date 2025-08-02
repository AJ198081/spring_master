import {ProductImage} from "../product/ProductImage.tsx";
import type {CartItemType} from "../../types/CartType.ts";
import {CartItemUpdater} from "../product/CartItemUpdater.tsx";
import {deleteCartItem, updateCartItemQuantity} from "../../services/CartService.ts";
import {toast} from "react-toastify";
import {useState} from "react";
import {useProductStore} from "../../store/ProductStore.ts";
import {BsFloppy2Fill, BsPencilFill, BsTrash} from "react-icons/bs";

interface CartItemComponentProps {
    cartItem: CartItemType
    cartId: number;
}

export const CartItemComponent = ({cartItem, cartId}: CartItemComponentProps) => {
    const [editingItem, setEditingItem] = useState<boolean>(false);
    const [quantity, setQuantity] = useState<number>(cartItem.quantity);
    const setCartForThisCustomer = useProductStore(state => state.setCartForThisCustomer);

    const updateCartItem = () => {

        console.log(`updateCartItem ${cartId} ${cartItem.product.id} ${quantity}`);
        updateCartItemQuantity(cartId, cartItem.product.id, quantity)
            .then(updatedCart => {
                setCartForThisCustomer(updatedCart);
                toast.success(`Cart item Id ${cartItem.id} updated successfully`);
            })
            .catch(error => {
                toast.error(`Error updating cart item; issue is - ${error.response?.data?.detail}`);
            });
    }

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
        <tr key={cartItem.id}>
            <td
                className={"cart-image"}
            >
                <ProductImage imageDownloadUrl={cartItem.product?.images[0]?.downloadUrl}/>
            </td>
            <td>{cartItem.product?.name}</td>
            <td>{cartItem.product?.brand?.name}</td>
            <td>{cartItem.unitPrice?.toFixed(2)}</td>
            <td
                className={"text-center"}
                style={{width: '160px'}}
            >
                {editingItem
                    ? <CartItemUpdater
                        quantity={quantity || 0}
                        setQuantity={setQuantity}
                        maxQuantity={cartItem.product?.inventory}
                    />
                    : quantity}
            </td>
            <td>{cartItem.total?.toFixed(2)}</td>
            <td className={"text-center"}>
                <div className={`d-flex justify-content-around`}>
                    <button
                        className={`btn ${editingItem ? 'btn-success' : 'btn-primary'}`}
                        onClick={() => setEditingItem(prevState => !prevState)}
                    >
                        {editingItem ? <BsFloppy2Fill onClick={updateCartItem}/> : <BsPencilFill/>}
                    </button>
                    <button
                        className={"btn btn-danger"}
                        onClick={() => removeCartItem(cartId, cartItem.id)}
                    >
                        <BsTrash/>
                    </button>
                </div>
            </td>
        </tr>
    )
}