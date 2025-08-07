import {backendClient} from "./Api.ts";
import type {CustomerType} from "../types/CustomerType.ts";
import {AxiosError, type AxiosResponse} from "axios";

export const addCustomer = async (customerDto: CustomerType) => {

    const customerResponse = await backendClient.post('/customers/', customerDto);

    if (customerResponse.status === 201) {
        return customerResponse.data as CustomerType;
    }

    const axiosError = new AxiosError("Error adding customer");
    axiosError.status = customerResponse.status;
    axiosError.response = customerResponse;
    throw axiosError;
}

export const getCustomer = async (username: string) => {
    const customerResponse = await backendClient.get(`/customers/username/${username}`);
    if (customerResponse.status === 200) {
        return customerResponse.data as CustomerType;
    }
    const axiosError = new AxiosError("Error fetching customer");
    axiosError.status = customerResponse.status;
    axiosError.response = customerResponse;
    throw axiosError;
}

export const getCustomerById = async (id: number) => {
    const customerResponse: AxiosResponse<CustomerType> = await backendClient.get(`/customers/${id}`);
    if (customerResponse.status === 200) {
        return customerResponse.data;
    }
    const axiosError = new AxiosError("Error fetching customer");
    axiosError.status = customerResponse.status;
    axiosError.response = customerResponse;
    throw axiosError;
}

export const updateCustomer = async (id: number, customerDto: CustomerType) => {
    const customerResponse = await backendClient.patch(`/customers/${id}`, customerDto);
    if (customerResponse.status === 200) {
        return customerResponse.data as CustomerType;
    }
    const axiosError = new AxiosError("Error updating customer");
    axiosError.status = customerResponse.status;
    axiosError.response = customerResponse;
    throw axiosError;
}