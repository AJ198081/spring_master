import {backendClient} from "./Api.ts";
import {AxiosError, type AxiosResponse} from "axios";
import type {OrderType} from "../types/OrderType.ts";

export const placeOrder = async (customerId: number) => {
    const response: AxiosResponse<OrderType> = await backendClient.post(
        `/orders/`,
        null, {
            params: {
                customerId: customerId
            }
        });

    if (response.status === 200) {
        return response.data;
    }

    const axiosError = new AxiosError("Error placing order");
    axiosError.status = response.status;
    axiosError.response = response;
    throw axiosError;
}

export const getOrdersForCustomer = async (customerId: number) => {
    const response: AxiosResponse<OrderType[]> = await backendClient.get(
        `/orders/${customerId}`);

    if (response.status === 200) {
        return response.data;
    }

    const axiosError = new AxiosError("Error fetching orders for customer");
    axiosError.status = response.status;
    axiosError.response = response;
    throw axiosError;
}