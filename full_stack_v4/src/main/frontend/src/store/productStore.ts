import {create} from "zustand";
import {ProductType} from "@/types/ProductType.ts";

export type ProductStore = {
    products: ProductType[],
    setProducts: (products: ProductType[]) => void
}

export const useProductStore = create<ProductStore>((set) => ({
    products: [] as ProductType[],
    setProducts: (products: ProductType[]) => set({products})
}));