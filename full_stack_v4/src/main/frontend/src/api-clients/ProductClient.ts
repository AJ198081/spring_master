import axios from "axios";

export const AxiosInstance = axios.create({
    baseURL: `${import.meta.env.VITE_PRODUCT_SERVICE_URL}/products`,
    timeout: 10 * 60 * 1000,
    headers:{
        "Content-Type": "application/json",
        "Accept": "application/json"
    }
});

