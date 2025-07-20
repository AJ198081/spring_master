import {backendClient} from './Api.ts';
import type {Product} from "../store/ProductStore.tsx";
import {AxiosError, type AxiosResponse} from "axios";
import type {BrandType} from "../types/CartType.ts";

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

export const addNewProduct = async (product: Product) => {
    const response: AxiosResponse<Product> = await backendClient.post('/products/', product);

    if (response.status === 201) {
        return response.data;
    }
    const axiosError = new AxiosError(`Error thrown whilst adding new product ${product.name}`);
    axiosError.status = response.status;
    axiosError.response = response;
    throw axiosError;
}

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

export const getAvailableCategories = async () => {
    const response = await backendClient.get('/products/distinctCategories');

    if (response.status === 200) {
        return response.data as string[];
    }
    const axiosError = new AxiosError(`Error thrown whilst fetching distinct categories`);
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

export const addNewBrand = async (brandName: string) => {
    const response: AxiosResponse<BrandType> = await backendClient.post('/brands/', null, {
        params: {
            brandName: brandName
        }
    });

    if (response.status === 200) {
        return response.data;
    }
    const axiosError = new AxiosError(`Error thrown whilst adding new brand ${brandName}`);
    axiosError.status = response.status;
    axiosError.response = response;
    throw axiosError;
}

export const addNewCategory = async (categoryName: string) => {
    const response: AxiosResponse<BrandType> = await backendClient.post('/categories/', null, {
        params: {
            categoryName: categoryName
        }
    });

    if (response.status === 200) {
        return response.data;
    }
    const axiosError = new AxiosError(`Error thrown whilst adding new category ${categoryName}`);
    axiosError.status = response.status;
    axiosError.response = response;
    throw axiosError;
}
