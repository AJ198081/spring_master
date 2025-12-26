import {useEffect} from "react";
import {CounterButton} from "./CounterButton.tsx";
import {useCounterStore} from "../../stores/CounterStore.ts";
import toast from "react-hot-toast";

export const Counter = () => {

    const {counterCycle, count, toastOn, increment, decrement, reset, setToastOn} = useCounterStore();

    useEffect(() => {
        const intervalId = setInterval(() => {
            increment();
        }, 1000);

        return () => clearInterval(intervalId);
    }, [])

    useEffect(() => {
        if (!toastOn) return;
        toast.success('Counter Cycle: ' + counterCycle, {
                id: 'counter-cycle',
                position: 'top-center',
                duration: 3000,
            }
        );
    }, [counterCycle, toastOn]);




    return <div className={"row text-white container col-12 text-center"}>

        <div className={"h3 p-4 my-5"}>
            Counter: {count}
        </div>

        <div className={""}>

            <CounterButton
                buttonText={"-1"}
                variant={"danger"}
                onClickHandler={decrement}
            />
            <CounterButton
                buttonText={"+1"}
                variant={"success"}
                onClickHandler={increment}
            />
            <div>
                <CounterButton
                    buttonText={"Reset"}
                    variant={"warning"}
                    onClickHandler={reset}
                />
            </div>
            <div>
                <CounterButton
                    buttonText={toastOn ? "Hide Count Cycle" : "Display Count Cycle"}
                    variant={"info"}
                    onClickHandler={setToastOn}
                    style={{
                        minWidth: "200px"
                    }}
                />
            </div>
        </div>
    </div>;
}