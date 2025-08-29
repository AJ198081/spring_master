import axios from "axios";

const BACKEND_SERVER = 'http://localhost:10006/api/v1/products';

const axiosInstance = axios.create({
    baseURL: BACKEND_SERVER,
    timeout: 1000,
    headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/xml',
    },
    withCredentials: true,
});

axiosInstance.get('/all')
    .then(response => response.data)
    .then(data => axiosInstance.get(`/${data[0].id}`))
    .then(response => response.data)
    .then(data => console.log(data))
    .catch(error => console.error('Error:', error));



