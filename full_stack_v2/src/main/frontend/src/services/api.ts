import axios from "axios";

console.log(`API URL ${process.env.VITE_FULL_STACK_V2_BASE_URL}`);

export const AxiosInstance = axios.create({
    baseURL: `http://localhost:${process.env.VITE_FULL_STACK_V2_BASE_URL}/api`,
    headers: {
        "Content-Type": "application/json",
    },
    withCredentials: true,
});

AxiosInstance.interceptors.response.use(response => {

    if (response.config.url) {
        console.log(response.config.url);
        if (response.config.url.includes('login')) {
            localStorage.removeItem('token');
        }
    } 

    if (response.status === 200) {
        localStorage.setItem('token', response.data.token);
    }

    return response;
}, error => {
    return Promise.reject(error);
});

AxiosInstance.interceptors.request.use(config => {

    if (localStorage.getItem('token') !== null) {
        config.headers.Authorization = `Bearer ${localStorage.getItem('token')}`;
    }

    config.headers.Accept = "application/json";
    return config;
}, (error) => {
    return Promise.reject(error);
})