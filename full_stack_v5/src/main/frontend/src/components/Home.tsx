import {Hero} from "./hero/Hero.tsx";
import {type Product, useProductStore} from "../store/ProductStore.ts";
import {Card} from "react-bootstrap";
import {Link} from "react-router-dom";
import {ProductImage} from "./product/ProductImage.tsx";
import {queryAllProducts} from "../services/ProductService.ts";
import {PaginatorComponent} from "./common/PaginatorComponent.tsx";
import {LoadSpinner} from "./common/LoadSpinner.tsx";
import {useQuery} from "@tanstack/react-query";
import {useEffect, useState} from "react";

export const Home = () => {

    const productStore = useProductStore();

    const searchedProducts = useProductStore(state => state.searchedProducts);
    const allProducts = useProductStore(state => state.allProducts);
    const setAllProducts = useProductStore(state => state.setAllProducts);

    const products = searchedProducts.length > 0 ? searchedProducts : allProducts;

    const [currentProducts, setCurrentProducts] = useState<Product[]>(products);

    const {isLoading, isError, data} = useQuery({
        queryKey: ["products"],
        queryFn: queryAllProducts,
        staleTime: 10000,
        refetchInterval: 50000,
    });

    useEffect(() => {
        if (data) {
            setAllProducts(data);
        }
    }, [data, setAllProducts]);

    useEffect(() => {
        const indexOfLastItemOnPage = productStore.currentPageNumber * productStore.productsPerPage;
        const indexOfFirstItemOnPage = indexOfLastItemOnPage - productStore.productsPerPage;
        setCurrentProducts(products.slice(indexOfFirstItemOnPage, indexOfLastItemOnPage));
    }, [productStore.currentPageNumber, productStore.productsPerPage, products]);

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
                    && <p
                        className={"d-flex flex-wrap justify-content-center p-5 text-danger h3"}
                        style={{marginTop: '160px'}}
                    >No products found</p>}
                {(!isError && products && products.length > 0) && renderProductCards()}
            </div>

            {isLoading && <LoadSpinner/>}

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
