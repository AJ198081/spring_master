import {backendClient} from './Api.ts';
import type {Product} from "../store/ProductStore.tsx";
import {AxiosError} from "axios";

export const getDistinctProducts = async (): Promise<Product[]> => {

    try {
        const response = await backendClient.get('/products/distinctByName');
        if (response.status === 200) {
            return response.data;
        }
        return [];
    } catch (e) {
        console.log(`Error thrown whilst fetching distinct products ${e}`);
        throw e;
    }
}

export const getProducts = async (uri: string): Promise<Product[]> => {
    try {
        console.log(`Fetching similar products by ${uri}`);
        const response = await backendClient.get(`/products/${uri}`);
        if (response.status === 200) {
            return response.data;
        } else {
            return [];
        }
    } catch (e) {
        if (e instanceof AxiosError) {
            console.log(`Error thrown whilst fetching similar products by id, exception is ${e.message}`);
        }
        throw e;
    }
};

export const getAvailableBrands = async (): Promise<string[]> => {
    const response = await backendClient.get('/products/distinctBrands');

    if (response.status === 200) {
        return response.data;
    }

    const axiosError = new AxiosError(`Error thrown whilst fetching distinct brands`);
    axiosError.status = response.status;
    axiosError.response = response;
    throw axiosError;
}

export const getProductById = async (id: number): Promise<Product> => {
    console.log(`Fetching product by id ${id}`);

    const response = await backendClient.get(`/products/${id}`);
    if (response.status === 200) {
        return response.data;
    }

    throw new AxiosError(`Product with id ${id} not found, status code ${response.status}`);
}

