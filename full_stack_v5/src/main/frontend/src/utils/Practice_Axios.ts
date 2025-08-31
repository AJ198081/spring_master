import axios from "axios";

const BACKEND_SERVER = 'http://localhost:10006/api/v1/products';

const config = {
    baseURL: BACKEND_SERVER,
    timeout: 5000,
    headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
    },
    withCredentials: true,
};

const axiosInstance = axios.create(config);

axiosInstance.get('/all')
    .then(response => response.data)
    .then(data => axiosInstance.get(`/${data[0].id}`))
    .then(response => response.data)
    // .then(data => console.log(data))
    .catch(error => console.error('Error:', error));


const loadFirstProduct = async () => {
    try {
        const allProducts = await axiosInstance.get('/all')
            .then(response => response.data);

        const firstProduct = await axiosInstance.get(`/${(allProducts[0].id)}`);

        return await firstProduct.data;
    } catch (error) {
        console.error('Error:', error);
    }
}

loadFirstProduct()
    .then(data => console.log(data));

