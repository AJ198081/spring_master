import {Hero} from "./hero/Hero.tsx";
import {useState} from "react";
import type {Product} from "../store/ProductStore.tsx";
import {Paginator} from "./common/Paginator.tsx";
import {Card} from "react-bootstrap";
import {Link} from "react-router-dom";

const products: Product[] = [
    {
        id: 1,
        name: "Laptop Pro",
        price: 1299.99,
        description: "High-performance laptop for professionals",
        image: "/images/laptop.jpg"
    },
    {
        id: 2,
        name: "Smartphone X",
        price: 799.99,
        description: "Latest smartphone with advanced features",
        image: "/images/phone.jpg"
    },
    {
        id: 3,
        name: "Wireless Headphones",
        price: 199.99,
        description: "Premium noise-canceling headphones",
        image: "/images/headphones.jpg"
    },
    {
        id: 4,
        name: "Smart Watch",
        price: 299.99,
        description: "Fitness and health tracking smartwatch",
        image: "/images/watch.jpg"
    },
    {
        id: 5,
        name: "Gaming Console",
        price: 499.99,
        description: "Next-gen gaming console",
        image: "/images/console.jpg"
    },
    {
        id: 6,
        name: "Tablet Pro",
        price: 649.99,
        description: "Versatile tablet for work and entertainment",
        image: "/images/tablet.jpg"
    },
    {
        id: 7,
        name: "Wireless Speaker",
        price: 129.99,
        description: "Portable bluetooth speaker",
        image: "/images/speaker.jpg"
    },
    {
        id: 8,
        name: "Digital Camera",
        price: 799.99,
        description: "Professional DSLR camera",
        image: "/images/camera.jpg"
    },
    {
        id: 9,
        name: "Smart TV",
        price: 899.99,
        description: "4K Smart TV with HDR",
        image: "/images/tv.jpg"
    },
    {
        id: 10,
        name: "Wireless Mouse",
        price: 59.99,
        description: "Ergonomic wireless mouse",
        image: "/images/mouse.jpg"
    }
];


export const Home = () => {

    const [itemsPerPage] = useState(1);
    const [currentPage, setCurrentPage] = useState<number>(1);

    const paginate = (pageNumber: number) => {
        return setCurrentPage(pageNumber);

    }
    const indexOfLastItemOnPage = currentPage * itemsPerPage;

    const indexOfFirstItemOnPage = indexOfLastItemOnPage - itemsPerPage;

    const currentProducts = products.slice(indexOfFirstItemOnPage, indexOfLastItemOnPage);

    console.log(currentProducts);

    return (
        <>
            <Hero/>
            <Card className={"home-product-card"}>
                <Link to={"/#"}>{products[0].id}</Link>
                <Card.Body>
                    <p className={"product-description"}>{products[0].name}</p>
                    <h4 className={"price"}>${products[0].price}</h4>
                    <p className={"text-success"}>In stock</p>
                    <Link to={"/#"} className={"shop-now-button"}>
                        {" "}
                        Shop Now
                    </Link>
                </Card.Body>

            </Card>
            <Paginator
                itemsPerPage={itemsPerPage}
                currentPage={currentPage}
                onPageChange={paginate}
                totalItems={products.length}
            />
        </>
    )
}