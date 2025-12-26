import {useEffect} from "react";
import {CounterButton} from "./CounterButton.tsx";
import {useCounterStore} from "../../stores/CounterStore.ts";

export const Counter = () => {

    const {count, increment, decrement, reset} = useCounterStore();

    useEffect(() => {
        const intervalId = setInterval(() => {
            increment();
        }, 1000);

        return () => clearInterval(intervalId);
    }, [])

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
        </div>
    </div>;
}