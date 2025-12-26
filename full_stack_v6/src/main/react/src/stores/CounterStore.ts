import {create} from "zustand/react";

interface CounterStore {
    count: number;
    counterCycle: number;
    toastOn: boolean;
    increment: () => void;
    decrement: () => void;
    reset: () => void;
    setToastOn: () => void;
}

export const useCounterStore = create<CounterStore>((set) => {

    return {
        count: 0,
        counterCycle: 0,
        increment: () => set(counterStore => {
            const newCount = counterStore.count + 1;
            return {
                count: newCount,
                counterCycle: counterStore.count === 0
                    ? 0
                    : newCount % 10 === 0
                        ? counterStore.counterCycle + 1
                        : counterStore.counterCycle
            };
        }),
        decrement: () => set(counterStore => {
            const newCount = counterStore.count - 1;
            return {
                count: newCount,
                counterCycle: newCount % 10 === 0
                    ? counterStore.counterCycle + 1
                    : counterStore.counterCycle
            };
        }),
        reset: () => set({
            count: 0
        }),
        toastOn: true,
        setToastOn: () => set(counterStore => ({toastOn: !counterStore.toastOn}))
    }
});