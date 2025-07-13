import {useState} from "react";
import {BsDash, BsPlus} from "react-icons/bs";
import {toast} from "react-toastify";
import {updateCartItemQuantity} from "../../services/CartService.ts";
import {useProductStore} from "../../store/ProductStore.tsx";

interface CartItemUpdaterProps {
    cartId: number;
    productId: number;
    initialQuantity: number;
    maxQuantity: number;
}

export default function CartItemUpdater({cartId, productId, initialQuantity, maxQuantity}: Readonly<CartItemUpdaterProps>) {
    const [quantity, setQuantity] = useState<number>(initialQuantity);
    const updateCartForThisCustomer = useProductStore(state => state.setCartForThisCustomer);

    console.log(maxQuantity, quantity);

    const onDecrease = () => {
        setQuantity(prevState => {
            return prevState === 0 ? 0 : prevState - 1
        })
    };

    const onIncrease = () => {
        setQuantity(prevState => {
            if (maxQuantity === 0) {
                toast.error('Out of stock');
                return prevState;
            }
            if (prevState === maxQuantity) {
                toast.error('Max quantity reached')
                return prevState;
            }
            return prevState === maxQuantity ? prevState : prevState + 1;
        })
    };

    const updateCartQuantity = ({quantity}: { quantity: number;}) => {
        updateCartItemQuantity(cartId, productId, quantity)
            .then(updatedCart => {
                updateCartForThisCustomer(updatedCart)
            })
            .catch(error => {
                toast.error(`Error updating cart item; issue is - ${error.response?.data?.detail}`);
            })
    }

    return (
        <section style={{width: "140px"}}>
            <div className={"input-group"}>
                <button
                    onClick={onDecrease}
                    className={'btn btn-outline-secondary'}
                >{" "}<BsDash/></button>
                <input
                    type={"number"}
                    name={"quantity"}
                    className={"form-control text-center"}
                    min={0}
                    max={maxQuantity}
                    value={quantity}
                    disabled={maxQuantity === 0 || quantity === maxQuantity}
                    onChange={e => {
                        if (Number(e.target.value) <= maxQuantity) {
                            setQuantity(Number(e.target.value));
                        }
                    }}
                />
                <button
                    onClick={onIncrease}
                    className={'btn btn-outline-secondary'}
                >{" "}<BsPlus/></button>
            </div>
        </section>
    )
}
