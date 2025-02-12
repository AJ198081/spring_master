import axios from "axios";

console.log(`API URL ${import.meta.env.VITE_FULL_STACK_V2_BASE_URL}`);

export const AxiosInstance = axios.create({
    baseURL: `${import.meta.env.VITE_FULL_STACK_V2_BASE_URL}/api`,
    headers: {
        "Content-Type": "application/json",
    },
    withCredentials: true,
});

AxiosInstance.interceptors.response.use(response => {

    if (response.config.url) {
        console.log(response.config.url);
        if (response.config.url.includes('login')) {
            localStorage.removeItem('JWT_TOKEN');
        }
    }

    if (response.status === 200) {
        localStorage.setItem('JWT_TOKEN', response.data.token);
    }

    return response;
}, error => {
    return Promise.reject(error);
});

AxiosInstance.interceptors.request.use(config => {

    if (localStorage.getItem('JWT_TOKEN') !== null) {
        console.log(localStorage.getItem('JWT_TOKEN'))
        config.headers.Authorization = `Bearer ${localStorage.getItem('JWT_TOKEN')}`;
    }

    if (localStorage.getItem('CSRF_TOKEN') !== null) {
        config.headers['X-CSRF-TOKEN'] = localStorage.getItem('CSRF_TOKEN');
    } else {
        axios.get(`${import.meta.env.VITE_FULLSTACK_V2_BASE_URL}/api/csrf-token`)
            .then(response => {
                localStorage.setItem('CSRF_TOKEN', response.data.token);
                config.headers['X-CSRF-TOKEN'] = response.data.token;
            })
    }

    console.log(config.headers['X-CSRF-TOKEN']);

    config.headers.Accept = "application/json";
    return config;
}, (error) => {
    return Promise.reject(error);
});