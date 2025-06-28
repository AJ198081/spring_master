import {Hero} from "./hero/Hero.tsx";
import {useState} from "react";
import {type Product, useProductStore} from "../store/ProductStore.tsx";
import {Paginator} from "./common/Paginator.tsx";
import {Card} from "react-bootstrap";
import {Link} from "react-router-dom";
import {ProductImage} from "./ProductImage.tsx";

export const Home = () => {

    const [itemsPerPage] = useState(1);
    const [currentPage, setCurrentPage] = useState<number>(1);

    const allAvailableProducts = useProductStore(state => state.allProducts);
    const filteredProducts = useProductStore(state => state.filteredProducts);

    const paginate = (pageNumber: number) => {
        return setCurrentPage(pageNumber);

    }
    const products = filteredProducts.length > 0 ? filteredProducts : allAvailableProducts;

    // const indexOfLastItemOnPage = currentPage * itemsPerPage;
    // const indexOfFirstItemOnPage = indexOfLastItemOnPage - itemsPerPage;
    // const currentProducts = products.slice(indexOfFirstItemOnPage, indexOfLastItemOnPage);

    return (
        <>
            <Hero/>
            <div className={"d-flex flex-wrap justify-content-center p-5"}>
                {

                    products.map((product) => (
                        <Card
                            className={"home-product-card"}
                            key={product.id}
                        >
                            <Link to={"/#"}>
                                <div>
                                    {product.images.length > 0
                                        && <ProductImage imageDownloadUrl={product.images[0].downloadUrl}/>}
                                </div>
                            </Link>
                            <Card.Body>
                                <p className={"product-description"}>
                                    {product.name
                                        .concat(' - ')
                                        .concat(product.description)}
                                </p>
                                <h4 className={"price"}>${products[0].price}</h4>
                                <p className={"text-success"}>{product.inventory} in stock</p>
                                <Link
                                    to={`/products/${product.id}`}
                                    className={"shop-now-button"}
                                >
                                    {" "}
                                    Shop Now
                                </Link>
                            </Card.Body>

                        </Card>
                    ))
                }
            </div>

            <Paginator
                itemsPerPage={itemsPerPage}
                currentPage={currentPage}
                onPageChange={paginate}
                totalItems={products.length}
            />
        </>
    )
}