import {create} from "zustand";
import {AxiosResponse} from "axios";
import {ProductType} from "@/types/ProductType.ts";
import {AxiosInstance} from "@/api-clients/ProductClient.ts";

export type ProductStore = {
    products: ProductType[],
    setProducts: (products: ProductType[]) => void
    createProduct: (product: ProductType) => Promise<{ status: string, message: string }>
    fetchProducts: () => void
    updateProduct: (product: ProductType) => Promise<{ status: string, message: string }>
    deleteProduct: (id: number, name: string) => Promise<{ status: string, message: string }>
}

export const useProductStore = create<ProductStore>((set) => ({
    products: [] as ProductType[],
    setProducts: (products: ProductType[]) => set({products}),
    createProduct: async (newProduct) => {
        if (!(newProduct.name && newProduct.description && newProduct.price)) {
            return Promise.reject({status: "error", message: "Invalid product details"});
        }

        try {
            const productCreationResponse = await AxiosInstance.post("", newProduct, {
                headers: {
                    'Content-Type': 'application/json'
                }
            }) as AxiosResponse<ProductType>;

            if (productCreationResponse.status === 201) {
                console.log("Product created successfully");
                console.log(productCreationResponse.data);
                set(state => ({
                    // products: [...state.products, productCreationResponse.data]
                    products: state.products.concat(productCreationResponse.data)
                }))
                return Promise.resolve({status: "success", message: "Product created successfully"});
            }
            return Promise.reject({status: "Error", message: "Product creation failed"});
        } catch (e) {
            return Promise.reject({status: "Error creating product", message: (e as Error).message});
        }
    },
    fetchProducts: async () => {
        const productResponse = await AxiosInstance.get("/all") as AxiosResponse<ProductType[]>;
        if (productResponse.status === 200) {
            console.log("Products fetched successfully", productResponse.data);
            set({ products: productResponse.data });
        }
    },
    updateProduct: async (product: ProductType) => {
        const updateResponse = await AxiosInstance.patch(`/${product.id}`, product) as AxiosResponse<ProductType>;

        if (updateResponse.status === 200) {
            set(state => ({
                products: state.products.map(p => p.id === product.id ? product : p)
            }));
            return Promise.resolve({ status: "success", message: `${product.name} updated successfully`});
        }

        return Promise.reject({ status: "error", message: `Couldn't update ${product.name}`});
    },
    deleteProduct: async (id: number, name: string) => {
        const deleteResponse = await AxiosInstance.delete(`/${id}`) as AxiosResponse;
        if (deleteResponse.status === 204) {
            set(state => ({
                products: state.products.filter(product => product.id !== id)
            }))
            return Promise.resolve({ status: "success", message: `${name} deleted successfully`});
        }
        return Promise.reject({ status: "error", message: `Couldn't delete ${name}`});
    }
}));