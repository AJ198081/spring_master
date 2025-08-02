import {Hero} from "./hero/Hero.tsx";
import {useEffect, useState} from "react";
import {useProductStore} from "../store/ProductStore.ts";
import {Card} from "react-bootstrap";
import {Link} from "react-router-dom";
import {ProductImage} from "./product/ProductImage.tsx";
import {getDistinctProducts} from "../services/ProductService.ts";
import {toast} from "react-toastify";
import {PaginatorComponent} from "./common/PaginatorComponent.tsx";
import {LoadSpinner} from "./common/LoadSpinner.tsx";

export const Home = () => {

    const productStore = useProductStore();
    const [errorMessage, setErrorMessage] = useState<string>();
    const [isLoading, setIsLoading] = useState<boolean>(false);

    useEffect(() => {
        setIsLoading(true);
        getDistinctProducts()
            .then(products => {
                useProductStore.setState({
                    allProducts: products
                });
            })
            .catch(error => {
                setErrorMessage(error.message);
                useProductStore.setState({allProducts: []});
                toast.error(error.message);
            })
            .finally(() => setIsLoading(false));
    }, []);

    const products = useProductStore(state => state.searchedProducts);

    const indexOfLastItemOnPage = productStore.currentPageNumber * productStore.productsPerPage;
    const indexOfFirstItemOnPage = indexOfLastItemOnPage - productStore.productsPerPage;
    const currentProducts = products.slice(indexOfFirstItemOnPage, indexOfLastItemOnPage);

    function renderProductCards() {
        return currentProducts
            .map((product) => (
                <Card
                    className={"home-product-card pt-2"}
                    key={product.id}
                >
                    <Link to={`/products/${product.id}`}>
                        <div>
                            {product.images?.length > 0
                                && <ProductImage
                                    key={product.images[0].downloadUrl}
                                    imageDownloadUrl={product.images[0].downloadUrl}
                                />}
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
            ));
    }

    return (
        <>
            <Hero/>

            <div className={"d-flex flex-wrap justify-content-center p-5"}>
                {!isLoading && (products.length === 0)
                && <p className={"d-flex flex-wrap justify-content-center p-5 text-danger h3"} style={{marginTop: '160px'}}>No products found</p>}
                {(!errorMessage && products && products.length > 0) && renderProductCards()}
            </div>

            {isLoading && <LoadSpinner />}

            {!isLoading && products && products.length > 0 && <PaginatorComponent
                products={products}
                currentPageNumber={productStore.currentPageNumber}
                onPageNumberChange={productStore.onPageNumberChange}
                productsPerPage={productStore.productsPerPage}
                onProductsPerPageChange={productStore.onProductsPerPageChange}
            />}
        </>
    )
}
