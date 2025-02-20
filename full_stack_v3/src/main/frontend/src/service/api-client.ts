import axios, {AxiosError, AxiosResponse, InternalAxiosRequestConfig} from "axios";

export const AxiosInstance = axios.create({
    baseURL: `${import.meta.env.VITE_API_BASE_URL_V3}`,
    timeout: 10 * 60 * 1000,
    headers: {
        "Content-Type": "application/json",
        "Accept": "application/json"
    }
});

AxiosInstance.interceptors.request.use(
    (config: InternalAxiosRequestConfig) => {
        return config;
    },
    (error: AxiosError) => {
        console.log(`Error whilst accessing ${error.config?.url} and message is ${error.message}`);
        return Promise.reject(error);
    }
);

AxiosInstance.interceptors.response.use(
    (response: AxiosResponse) => {

        return response;
    },

    (error: AxiosError) => {
        if (error.response) {
            switch (error.response.status) {
                case 400:
                case 401:
                    window.location.href = "/login";
                    break;
                case 403:
                    console.log(` 403 whilst accessing ${error.config?.url}`);
                    break;
                case 404:
                    console.log(` 404 whilst accessing ${error.config?.url}`);
                    break;
                case 500:
                    console.log(` 500 whilst accessing ${error.config?.url}`);
            }
        } else if (error.request) {
            console.log(`No response received when accessing ${error.request}`);
        } else {
            console.log(`Error whilst ${error.config?.url} and message is ${error.message}`);
        }

        return Promise.reject(error);
    });