import {backendClient} from "./Api.ts";
import {toast} from "react-toastify";


export interface AddCartItem {
    customerId?: number;
    productId: number;
    quantity: number;
}

export const addProductToCartItems = async (addCartItem: AddCartItem)=>  {
console.log(`Customer ID in request ${addCartItem.customerId}`);

    if (!addCartItem.customerId) {
        await getFirstCustomer()
            .then(customer => {
                addCartItem.customerId = customer.id;
            })
            .catch(e => toast.error(e.message));
    }

    try {
        const addToCartResponse = await backendClient.post("/cartItems/", null, {
            params: addCartItem
        });
        if (addToCartResponse.status === 200) {
            return addToCartResponse.data;
        }
        return null;
    } catch (e) {
        console.log(e);
        throw e;
    }
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