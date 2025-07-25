import {backendClient} from "./Api.ts";
import type {CustomerType} from "../types/CustomerType.ts";
import {AxiosError} from "axios";

export const addCustomer = async (customerDto: CustomerType) => {

    const customerResponse = await backendClient.post('/customers/', customerDto);

    if (customerResponse.status === 201) {
        return customerResponse.data;
    }

    const axiosError = new AxiosError("Error adding customer");
    axiosError.status = customerResponse.status;
    axiosError.response = customerResponse;
    throw axiosError;
}