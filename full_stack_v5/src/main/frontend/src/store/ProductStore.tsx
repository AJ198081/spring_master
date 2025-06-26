import {create} from 'zustand';

export interface Product {
    id: number,
    name: string,
    price: number,
    description: string,
    image: string,
}

// This is what you need to declare first up,
// An interface that will contain the state you want to manage, and the setter/update functions of the state
interface ProductStore {
    products: Product[], // Type of any state object should be declared
    setProducts: (products: Product[]) => void,
}

export const useProductStore = create<ProductStore>(set => ({
    products: [] as Product[],
    setProducts: (products) => set({products})

}))