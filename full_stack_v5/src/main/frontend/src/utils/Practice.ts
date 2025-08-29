const BACKEND_SERVER = 'http://localhost:10006/api/v1/productso';
fetch(BACKEND_SERVER + '/all')
    .then(response => response.json())
    .then(data => fetch(BACKEND_SERVER + `/${data[0].id}`))
    .then(response => response.json())
    .then(data => console.log(data))
    .catch(error => console.error('Error:', error));

const loadFirstProduct = async () =>{
    try {
        const allProducts = await fetch(BACKEND_SERVER + '/all').then(response => response.json());
        const firstProductId = allProducts[0].id;
        const firstProduct = await fetch(BACKEND_SERVER + `/${firstProductId}`);
        const product = await firstProduct.json();
        console.log(product);
    } catch (error) {
        console.error('Error:', error);
    }
}

loadFirstProduct();


