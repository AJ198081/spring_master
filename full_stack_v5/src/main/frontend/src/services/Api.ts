import axios from 'axios';
import {type Authentication, authDefaultValues, useAuthStore} from '../store/AuthStore';
import {isJwtValid, parseJwt} from "./JwtUtil.ts";
import {QueryClient} from "@tanstack/react-query";

const isDevelopment = import.meta.env.MODE === 'development';
export const BASE_URL = isDevelopment ? 'http://localhost:10006/api/v1' : '/api/v1';

export const queryClient = new QueryClient();

export const backendClient = axios.create({
    baseURL: BASE_URL
});

backendClient.interceptors.request.use(
    config => {
        const authState = useAuthStore.getState().authState;
        if (authState && authState.isAuthenticated && authState.token) {
            config.headers.Authorization = `Bearer ${authState.token}`;
            config.withCredentials = true;
        }
        return config;
    }
);

backendClient.interceptors.response.use(
    response => {
        if (response.headers.authorization) {
            const authorizationHeader = response.headers.authorization;
            const jwtClaims = parseJwt(authorizationHeader);
            if (jwtClaims) {
                const authenticationObject: Authentication = {
                    isAuthenticated: isJwtValid(authorizationHeader),
                    token: authorizationHeader,
                    username: jwtClaims.sub,
                    roles: jwtClaims.roles
                }
                useAuthStore.getState().setAuthState(authenticationObject);
            }
        }
        return response
    },

    error => {
        if (error.response && error.response.status === 401) {
            console.log('Unauthorized intercepted in AxiosResponse interceptor; resetting AuthState.');
            backendClient.defaults.headers.common['Authorization'] = '';
            useAuthStore.getState().setAuthState(authDefaultValues);
        }
        return error;
    }
)