import {backendClient} from "./Api.ts";
import type {CartType} from "../types/CartType.ts";
import {AxiosError, type AxiosResponse} from "axios";
import type {CustomerType} from "../types/CustomerType.ts";
import {useAuthStore} from "../store/AuthStore.ts";


export interface AddCartItem {
    customerId?: number;
    productId: number;
    quantity: number;
}

export const addProductToCartItems: (addCartItem: AddCartItem) => Promise<CartType> = async (addCartItem: AddCartItem) => {
    console.log(`Customer ID in request ${addCartItem.customerId}`);

    const addToCartResponse: AxiosResponse<CartType> = await backendClient.post("/cartItems/", null, {
        params: addCartItem
    });

    if (addToCartResponse.status === 200) {
        return addToCartResponse.data;
    }

    const axiosError = new AxiosError("Error adding to cart");
    axiosError.status = addToCartResponse.status;
    axiosError.response = addToCartResponse;
    throw axiosError;
}

export const deleteCartItem = async (cartId: number, cartItemId: number) => {
    const response: AxiosResponse<CartType> = await backendClient.delete(`/carts/${cartId}/cartItem/${cartItemId}`);
    if (response.status === 200) {
        return response.data;
    }
    const axiosError = new AxiosError("Error adding to cart");
    axiosError.status = response.status;
    axiosError.response = response;
    throw axiosError;
}

export const updateCartItemQuantity = async (cartId: number, productId: number, quantity: number) => {
    const response: AxiosResponse<CartType> = await backendClient.patch(`/cartItems/`, null, {
        params: {
            cartId: cartId,
            productId: productId,
            quantity: quantity
        }
    });

    if (response.status === 200) {
        return response.data;
    }

    const axiosError = new AxiosError("Error updating to cart item quantity");
    axiosError.status = response.status;
    axiosError.response = response;
    throw axiosError;
}

export const getCustomer = async () => {
    const currentUser = useAuthStore.getState().authState;
    try {
        const response: AxiosResponse<CustomerType> = await backendClient.get(`/customers/username/${currentUser?.username}`);
        if (response.status === 200) {
            return response.data;
        } else {
            return null;
        }
    } catch (e) {
        console.log(`Error thrown whilst fetching customer details ${e}`);
        if (e instanceof AxiosError) {
            console.log(`Error thrown whilst fetching customer details, exception is ${e.message}`);
        }
        throw e;
    }
}

export const getCustomerCart = async (customerId: number) => {
    const response: AxiosResponse<CartType> = await backendClient.get(`/customers/${customerId}`);
    if (response.status === 200) {
        return response.data;
    }
    const axiosError = new AxiosError("Error fetching customer cart");
    axiosError.status = response.status;
    axiosError.response = response;
    throw axiosError;
}