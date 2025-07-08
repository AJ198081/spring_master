import {create} from 'zustand';

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
    filteredProducts: Product[],
    setAllProducts: (products: Product[]) => void,
    setFilteredProducts: (filteredProducts: Product[]) => void,
    productsToShow: () => Product[],
    filteredProductsByBrand: (brands: string[]) => Product[],
    currentPageNumber: number,
    onPageNumberChange: (pageNumber: number) => void,
    productsPerPage: number,
    onProductsPerPageChange: (productsOnPage: number) => void,
    productBrands: string[],
    setProductBrands: (brands: string[]) => void,
}

export const useProductStore = create<ProductStore>((set, get) => ({
    allProducts: [] as Product[],
    setAllProducts: (products) => set({allProducts: products}),

    filteredProducts: [] as Product[],
    setFilteredProducts: (products) => set({filteredProducts: products}),

    productsToShow: () => {
        const currentlyFilteredProducts = get().filteredProducts;
        const allProductsList = get().allProducts;

        return currentlyFilteredProducts && currentlyFilteredProducts.length > 0
            ? currentlyFilteredProducts
            : allProductsList;
    },

    filteredProductsByBrand: (selectedBrands) => {
        if (!selectedBrands || selectedBrands.length === 0) {
            return get().allProducts;
        }
        return get().filteredProducts
            .filter((product) => selectedBrands.includes(product.brand));
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
}))
