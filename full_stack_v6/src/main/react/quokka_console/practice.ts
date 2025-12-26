import axios from "axios";
console.log('Hello, Quokka Console!');

fetch(`https://jsonplaceholder.typicode.com/posts/1`)
.then(response => response.json())
.then(data => console.log(data))
.catch(error => console.error('Error fetching products:', error));

axios.get(`https://jsonplaceholder.typicode.com/posts/12`)
.then(response => console.log(response.data))
.catch(error => console.error('Error fetching product:', error));
