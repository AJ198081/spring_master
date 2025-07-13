import {create} from 'zustand';
import type {CartType} from "../types/CartType.ts";

export interface Product {
    id: number,
    name: string,
    price: number,
    description: string,
    brand: string,
    inventory: number,
    categoryName: string,
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
    searchedProducts: Product[],
    setSearchedProducts: (products: Product[]) => void,
    setAllProducts: (products: Product[]) => void,
    productsToShow: () => Product[],
    filteredProducts: Product[],
    setFilteredProducts: (brands: string[]) => void,
    currentPageNumber: number,
    onPageNumberChange: (pageNumber: number) => void,
    productsPerPage: number,
    onProductsPerPageChange: (productsOnPage: number) => void,
    productBrands: string[],
    setProductBrands: (brands: string[]) => void,
    cartForThisCustomer: CartType | null,
    setCartForThisCustomer: (cart: CartType | null) => void,
}

export const useProductStore = create<ProductStore>((set, get) => ({
    allProducts: [] as Product[],
    setAllProducts: (products) => set({allProducts: products}),

    searchedProducts: [] as Product[],
    setSearchedProducts: (products) => set({searchedProducts: products}),

    filteredProducts: [] as Product[],

    setFilteredProducts: (brands: string[]) => {
        if (brands.length === 0) {
            set({filteredProducts: []});
        }
        return set({
            filteredProducts: get().searchedProducts
                .filter((product) => brands.includes(product.brand))
        });
    },

    productsToShow: () => {
        const {filteredProducts, searchedProducts, allProducts} = get();

        if (filteredProducts.length > 0) {
            return filteredProducts;
        }

        if (searchedProducts.length > 0) {
            return searchedProducts;
        }

        return allProducts;
    },

    currentPageNumber: 1,
    onPageNumberChange: (newPageNumber) => {
        set({currentPageNumber: newPageNumber});
    },

    productsPerPage: 5,
    onProductsPerPageChange: (productsOnPage) => {
        set({productsPerPage: productsOnPage});
    },

    productBrands: [],
    setProductBrands: (brands) => set({productBrands: brands}),

    cartForThisCustomer: null,
    setCartForThisCustomer: (cart) => set({cartForThisCustomer: cart}),
}))
