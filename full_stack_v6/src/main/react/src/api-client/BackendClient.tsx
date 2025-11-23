import axios from "axios";

import type {operations, paths} from "../assets/schema";

export const backendClient = axios.create({baseURL: `${import.meta.env.VITE_API_BASE_URL}`});

export type BackendPaths = paths[keyof paths];

export type BackendPath = BackendPaths[keyof BackendPaths];

export type BackendOperation = BackendPath[keyof BackendPath];

export type BackendResponse<T> = T extends BackendOperation ? Awaited<ReturnType<T>> : never;


backendClient.interceptors.request.use(config => {
    config.headers.set(
        'Authorization', `Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJwaGlsIiwiYXV0aG9yaXRpZXMiOlsiUk9MRV9BRE1JTiJdLCJpYXQiOjE3NjM4ODEzNTcsImV4cCI6MTc2Mzk2Nzc1N30.OArMlNsYj9_41Hx-7Rjv3X_seHDKCSdMM0_tsl3iMAHtpdo5Izk3lhqQLyEnlKJcEkor1a_36Jvlrc2_Z9inXw`
);
    return config;
});


export const getAllProducts = () => backendClient
    .get<BackendResponse<operations["getAllProducts"]>>("/api/v1/products/all");


export default backendClient;