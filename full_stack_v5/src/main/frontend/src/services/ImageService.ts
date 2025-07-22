import {backendClient} from "./Api.ts";
import {AxiosError} from "axios";


export const getImageById = async (downloadUrl: string) => {
    const response = await backendClient.get(`images/${downloadUrl}`);

    if (response.status === 200) {
        return response.data;
    }

    const axiosError = new AxiosError(`Error fetching image by id ${downloadUrl}`);
    axiosError.status = response.status;
    axiosError.response = response;
    throw axiosError;
}

export const uploadImagesForProduct = async (productId: number, images: File[], replaceAll: boolean = false) => {

    console.log(`Uploading images for product ${productId}`);

    const formData = new FormData();
    if (images.length === 0) {
        return;
    }

    images.forEach(image => {
        formData.append('files', image);
    });
    formData.append('productId', productId.toString());
    formData.append('replaceAll', replaceAll.toString());

    const response = await backendClient.post(
        `/images/product/${productId}`,
        formData,
        {
            params: {
                replaceAll: replaceAll,
            }
        });

    if (response.status === 200) {
        return response.data;
    }

    const axiosError = new AxiosError(`Error uploading images for product ${productId}`);
    axiosError.status = response.status;
    axiosError.response = response;
    throw axiosError;
}
