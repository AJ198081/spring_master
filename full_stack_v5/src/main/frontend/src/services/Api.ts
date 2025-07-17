import axios from 'axios';

export const BASE_URL = 'http://localhost:10006/api/v1';

export const backendClient = axios.create({
    baseURL: BASE_URL
});