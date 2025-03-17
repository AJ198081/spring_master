import {create} from "zustand";
import {AxiosResponse} from "axios";
import {ProductType} from "@/types/ProductType.ts";
import {AxiosInstance} from "@/api-clients/ProductClient.ts";

export type ProductStore = {
    products: ProductType[],
    setProducts: (products: ProductType[]) => void
    createProduct: (product: ProductType) => Promise<{ status: string, message: string }>
    fetchProducts: () => void
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
    }
}));