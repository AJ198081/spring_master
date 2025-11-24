import axios from "axios";

import type {operations, paths} from "../assets/schema";

export const backendClient = axios.create({baseURL: `${import.meta.env.VITE_API_BASE_URL}`});

export type BackendPaths = paths[keyof paths];

export type BackendPath = BackendPaths[keyof BackendPaths];

export type BackendOperation = BackendPath[keyof BackendPath];

export type BackendResponse<T> = T extends BackendOperation ? Awaited<ReturnType<T>> : never;


backendClient.interceptors.request.use(config => {
    config.headers.set(
        'Authorization', `Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJwaGlsIiwiYXV0aG9yaXRpZXMiOlsiUk9MRV9BRE1JTiJdLCJpYXQiOjE3NjM5NjkzMDMsImV4cCI6MTc2NDA1NTcwM30.Y414VmCEcfjG1tA9ldBuK83DY4QpryYbhdd7YkIsEfP-IhJY4jdIs7g8F-OGnII4a8jcoyTJOG7-l4yKmYcSrg`
);
    return config;
});


export const getAllProducts = () => backendClient
    .get<BackendResponse<operations["getAllProducts"]>>("/api/v1/products/all");


export default backendClient;