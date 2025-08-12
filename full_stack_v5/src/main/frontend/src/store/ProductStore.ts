import {create} from 'zustand';
import type {CartType} from "../types/CartType.ts";
import type {OrderType} from "../types/OrderType.ts";
import type {UserResponseDto} from "../types/User.ts";
import type {CustomerType} from "../types/CustomerType.ts";

export interface Product {
    id?: number,
    name: string,
    price: number,
    description: string,
    brand: string,
    inventory: number,
    categoryName: string,
    images: Image[],
    inWishList?: boolean
}

export interface Image {
    fileName: string,
    downloadUrl: string,
    contentType: string
}

// This is what you need to declare first up,
// An interface that will contain the state you want to manage, and the setter/update functions of the state

interface ProductStore {
    thisCustomer: CustomerType | null,
    setThisCustomer: (customer: CustomerType) => void;
    currentUser: UserResponseDto | null,
    setCurrentUser: (user: UserResponseDto | null) => void;
    thisCustomerId: number | null;
    setThisCustomerId: (customerId: number) => void;
    thisCustomerOrders: OrderType[];
    setThisCustomerOrders: (orders: OrderType[]) => void;
    allProducts: Product[],
    searchedProducts: Product[],
    addProduct: (product: Product) => void,
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
    productCategories: string[],
    setProductCategories: (categories: string[]) => void,
    cartForThisCustomer: CartType | null,
    setCartForThisCustomer: (cart: CartType | null) => void,
    ordersForThisCustomer: OrderType[] | null,
    setOrdersForThisCustomer: (orders: OrderType[] | null) => void,
    updateOrderForThisCustomer: (order: OrderType) => void,
}

function sortStringsAlphabetically(categories: string[]) {
    return categories.sort((a, b) => a.localeCompare(b));
}

export const useProductStore = create<ProductStore>((set, get) => ({
        allProducts: [] as Product[],
        setAllProducts: (products) => set({allProducts: products}),
        addProduct: (product) => set({allProducts: [product, ...get().allProducts]}),

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
        setProductBrands: (brands) => set({productBrands: sortStringsAlphabetically(brands)}),

        productCategories: [],
        setProductCategories: (categories) => set({productCategories: sortStringsAlphabetically(categories)}),

        cartForThisCustomer: null,
        setCartForThisCustomer: (cart) => set({cartForThisCustomer: cart}),

        ordersForThisCustomer: [],
        setOrdersForThisCustomer: (orders) => set({ordersForThisCustomer: orders}),

        updateOrderForThisCustomer: (order) => {
            set({ordersForThisCustomer: [...get().ordersForThisCustomer!, order]});
        },

        thisCustomerOrders: [],
        setThisCustomerOrders: (orders) => set({thisCustomerOrders: orders}),

        thisCustomerId: null,
        setThisCustomerId: (customerId) => set({thisCustomerId: customerId}),

        currentUser: null,
        setCurrentUser: (userResponse => set({currentUser: userResponse})),

        thisCustomer: null,
        setThisCustomer: (customer => set({thisCustomer: customer})),
    }
))
