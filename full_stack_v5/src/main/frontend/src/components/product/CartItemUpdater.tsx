import {useState} from "react";
import {BsDash, BsPlus} from "react-icons/bs";
import {toast} from "react-toastify";

interface CartItemUpdaterProps {
    initialQuantity: number;
    maxQuantity: number;
}

export default function CartItemUpdater({initialQuantity, maxQuantity}: Readonly<CartItemUpdaterProps>) {
    const [quantity, setQuantity] = useState<number>(initialQuantity);

    console.log(maxQuantity, quantity);

    const onDecrease = () => {
        setQuantity(prevState => {
            return prevState === 0 ? 0 : prevState - 1
        })
    };

    const onIncrease = () => {
        setQuantity(prevState => {
            if (maxQuantity === 0) {
                // alert('Out of stock');
                toast.error('Out of stock');
                return prevState;
            }
            if (prevState === maxQuantity) {
                // alert('Max quantity reached');
                toast.error('Max quantity reached')
                return prevState;
            }
            return prevState === maxQuantity ? prevState : prevState + 1;
        })
    };
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
