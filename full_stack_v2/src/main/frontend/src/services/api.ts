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

    console.log(`${config.method} ${config.url}`);

    if (localStorage.getItem('JWT_TOKEN') !== null) {
        console.log(localStorage.getItem('JWT_TOKEN'))
        config.headers.Authorization = `Bearer ${localStorage.getItem('JWT_TOKEN')}`;
    }

    if (localStorage.getItem('CSRF_TOKEN') !== null || localStorage.getItem('CSRF_TOKEN') === undefined) {
        config.headers['X-CSRF-TOKEN'] = localStorage.getItem('CSRF_TOKEN');
    } else {
        const csrfUrl = `${import.meta.env.VITE_FULL_STACK_V2_BASE_URL}/api/csrf-token`;
        console.log("CSRF URL: ", csrfUrl);
        axios.get(csrfUrl)
            .then(response => {
                console.log("CSRF TOKEN: ", response.data.token);
                console.log("CSRF TOKEN TYPE: ", typeof response.data.token);
                console.log("CSRF TOKEN LENGTH: ", response.data.token.length);
                console.log("Response: ", response.data);
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