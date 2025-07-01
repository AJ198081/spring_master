import axios from 'axios';

export const backendClient = axios.create({
    baseURL: 'http://localhost:10006/api/v1'
});