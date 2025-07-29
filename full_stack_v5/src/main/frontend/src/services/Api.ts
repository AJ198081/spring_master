import axios from 'axios';
import {type Authentication, useAuthStore} from '../store/AuthStore';
import {isJwtValid, parseJwt} from "./JwtUtil.ts";

export const BASE_URL = 'http://localhost:10006/api/v1';

export const backendClient = axios.create({
    baseURL: BASE_URL
});

backendClient.interceptors.request.use(
    config => {
        const authState = useAuthStore.getState().authState;
        if (authState && authState.isAuthenticated && authState.token) {
            config.headers.Authorization = `Bearer ${authState.token}`;
        }
        config.withCredentials = true;
        return config;
    }
);

backendClient.interceptors.response.use(
    response => {

        if (response.headers.authorization) {
            console.log('Setting AuthState.');
            const authorizationHeader = response.headers.authorization;

            const jwtClaims = parseJwt(authorizationHeader);

            if (jwtClaims) {
                const authenticationObject: Authentication = {
                    isAuthenticated: isJwtValid(authorizationHeader),
                    token: authorizationHeader,
                    username: jwtClaims.sub,
                    roles: jwtClaims.roles
                }
                console.log(`Setting an authentication object from response header`);
                useAuthStore.getState().setAuthState(authenticationObject);
            }
        }
        return response
    },
    error => {
        if (error.response && error.response.status === 401) {
            console.log('Unauthorized resetting AuthState.');
            backendClient.defaults.headers.common['Authorization'] = '';
            useAuthStore.getState().setAuthState(null);
        }
        return Promise.reject(error);
    }
)