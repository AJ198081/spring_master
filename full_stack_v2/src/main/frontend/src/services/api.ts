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
        if (response.config.url.includes('/login') && response.status === 200) {
            localStorage.removeItem('JWT_TOKEN');
            localStorage.setItem('JWT_TOKEN', response.data.jwtToken);
        }
    }
    return response;
}, error => {
    return Promise.reject(error);
});

AxiosInstance.interceptors.request.use(config => {

    if (localStorage.getItem('JWT_TOKEN') !== null) {
        config.headers.Authorization = `Bearer ${localStorage.getItem('JWT_TOKEN')}`;
    }

    if (localStorage.getItem('CSRF_TOKEN') === null || localStorage.getItem('CSRF_TOKEN') === undefined) {
        const csrfUrl = `${import.meta.env.VITE_FULL_STACK_V2_BASE_URL}/api/csrf-token`;

        axios.get(csrfUrl)
            .then(response => {
                localStorage.setItem('CSRF_TOKEN', response.data.token);
            })
    }

    config.headers['X-XSRF-TOKEN'] = localStorage.getItem('CSRF_TOKEN');

    config.headers.Accept = "application/json";
    return config;
}, (error) => {
    return Promise.reject(error);
});