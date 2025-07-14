import {BsDash, BsPlus} from "react-icons/bs";
import {toast} from "react-toastify";

interface CartItemUpdaterProps {
    quantity: number;
    setQuantity: (value: number | ((prevState: number) => number)) => void;
    maxQuantity: number;
}

export const CartItemUpdater = ({
                                    quantity,
                                    setQuantity,
                                    maxQuantity,
                                }: CartItemUpdaterProps) => {

    const onDecrease = () => {
        setQuantity((prevState: number) => prevState === 0 ? 0 : prevState - 1)
    };

    const onIncrease = () => {
        setQuantity((prevState: number) => {
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
};
