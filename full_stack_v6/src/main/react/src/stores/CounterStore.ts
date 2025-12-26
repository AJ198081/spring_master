import {create} from "zustand/react";

interface CounterStore {
    count: number
    increment: () => void
    decrement: () => void
    reset: () => void
}

export const useCounterStore = create<CounterStore>((set) => {

    return {
        count: 0,
        increment: () => set(counterStore => ({count: counterStore.count + 1})),
        decrement: () => set(counterStore => ({count: counterStore.count - 1})),
        reset: () => set({count: 0})
    }
});