import {backendClient} from './Api.ts';
import type {Product} from "../store/ProductStore.tsx";

export const getDistinctProducts = async () => {

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

export const getProductsById = async (id: number | null) : Product[] => {
    try {
        console.log(`Fetching products by id ${id}`);
        const response = await backendClient.get(`/products/${id}`);
        if (response.status === 200) {
            return response.data;
        }
        return null;
    } catch (e) {
        console.log(`Error thrown whilst fetching products by id, exception is ${e.message}`);
        throw e;
    }

};