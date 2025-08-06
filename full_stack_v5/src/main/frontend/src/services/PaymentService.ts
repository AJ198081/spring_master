import {backendClient} from "./Api.ts";
import {AxiosError, type AxiosResponse} from "axios";

export interface PaymentRequest {
    amount: number;
    currency: string;
    customerId: number;
}

export const createPaymentIntent = async (paymentRequest: PaymentRequest) => {
    const paymentIntentResponse: AxiosResponse<string> = await backendClient.post('/payments/createPaymentIntent', paymentRequest);

    if (paymentIntentResponse.status === 200) {
        return paymentIntentResponse.data;
    }

    const axiosError = new AxiosError("Error creating payment intent");
    axiosError.status = paymentIntentResponse.status;
    axiosError.response = paymentIntentResponse;
    throw axiosError;
}