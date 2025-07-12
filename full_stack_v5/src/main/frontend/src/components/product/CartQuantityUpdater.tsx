import {BsDash, BsPlus} from "react-icons/bs";
import {MdRestore} from "react-icons/md";

interface QuantityUpdaterProps {
    maxQuantity: number;
    quantity: number;
    setQuantity: (event: number) => void;
    onDecrease: () => void;
    onIncrease: () => void;
}

export const CartQuantityUpdater = ({maxQuantity, quantity, setQuantity, onDecrease, onIncrease}: QuantityUpdaterProps) => {

    return (
        <section style={{width: "250px"}}>
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
                <button
                    onClick={() => setQuantity(0)}
                    className={'btn btn-outline-secondary btn-info'}
                >{" "}<MdRestore/></button>
            </div>
        </section>
    );
}