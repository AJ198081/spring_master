import {backendClient} from "./Api.ts";
import {toast} from "react-toastify";
import type {CartType} from "../types/CartType.ts";
import {AxiosError, type AxiosResponse} from "axios";


export interface AddCartItem {
    customerId?: number;
    productId: number;
    quantity: number;
}

export const addProductToCartItems: (addCartItem: AddCartItem) => Promise<CartType> = async (addCartItem: AddCartItem) => {
    console.log(`Customer ID in request ${addCartItem.customerId}`);

    if (!addCartItem.customerId) {
        await getFirstCustomer()
            .then(customer => {
                addCartItem.customerId = customer.id;
            })
            .catch(e => toast.error(e.message));
    }

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

const getFirstCustomer = async () => {
    try {
        const response = await backendClient.get("/customers/all");
        if (response.status === 200) {
            return response.data[0];
        }
        return null;
    } catch (e) {
        if (e instanceof Error) {
            console.log(`Error thrown whilst fetching first customer ${e.message}`);
        }
        throw e;
    }
}