import axios, {AxiosError, AxiosResponse} from "axios";
import dayjs from "dayjs";

export const AxiosInstance = axios.create({
    baseURL: `${import.meta.env.VITE_API_BASE_URL_V3}`,
    timeout: 10 * 60 * 1000,
    headers: {
        "Content-Type": "application/json",
        "Accept": "application/json"
    }
});

AxiosInstance.interceptors.request.use(
    (config) => {
        config.withCredentials = true;
        return config;
    },

    (error) => {
        return Promise.reject(error);
    }
)

AxiosInstance.interceptors.response.use(
    (response: AxiosResponse) => {
        if (response.config?.url?.includes('login') && response.status === 200) {
            console.log(`Successfully logged in ${dayjs().format('DD/MM/YYYY HH:mm:ss')}`);
            // AxiosInstance.defaults.headers.common['Authorization'] = `Bearer ${response.data.token}`;
            // Won't be able to change the header whenever 'token' value changes
        } else {
            // AxiosInstance.defaults.headers.common['Authorization'] = null;
        }
        return response;
    },

    (error: AxiosError) => {
        if (error.response) {
            switch (error.response.status) {
                case 400:
                    console.log(` 400 whilst accessing ${error.config?.url}`);
                    break;
                case 401:
                    console.log(` 401 whilst accessing ${error.config?.url}`);
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
    }
);
