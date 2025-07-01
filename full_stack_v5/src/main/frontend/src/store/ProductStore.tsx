import {create} from 'zustand';

export interface Product {
    id: number,
    name: string,
    price: number,
    description: string,
    inventory: number,
    images: Image[],
}

export interface Image {
    fileName: string,
    downloadUrl: string,
    contentType: string
}
// This is what you need to declare first up,
// An interface that will contain the state you want to manage, and the setter/update functions of the state

interface ProductStore {
    allProducts: Product[],
    filteredProducts: Product[],
    setAllProducts: (products: Product[]) => void,
    setFilteredProducts: (filteredProducts: Product[]) => void
}

export const useProductStore = create<ProductStore>(set => ({
    allProducts: [] as Product[],
    filteredProducts: [] as Product[],
    setAllProducts: (products) => set({allProducts: products}),
    setFilteredProducts: (filteredProducts) => set({filteredProducts})
}))
