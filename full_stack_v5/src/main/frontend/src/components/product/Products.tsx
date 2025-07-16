import {ProductCard} from "./ProductCard.tsx";
import {SearchBar} from "../search/SearchBar.tsx";
import {useEffect, useState} from "react";
import {type Product, useProductStore} from "../../store/ProductStore.tsx";
import {PaginatorComponent} from "../common/PaginatorComponent.tsx";
import {Sidebar} from "../common/SideBar.tsx";
import {useParams} from "react-router-dom";
import {getProducts} from "../../services/ProductService.ts";

export const Products = () => {

    const [currentPage, setCurrentPage] = useState(1);
    const [productsPerPage, setProductsPerPage] = useState(10);
    const filteredProducts = useProductStore(state => state.productsToShow());
    const setSearchedProducts = useProductStore(state => state.setSearchedProducts);

    const {id} = useParams();

    const productId = Number(id) ? `similar/${id}` : "all";

    useEffect(() => {
        getProducts(productId)
            .then((productsForCurrentPage: Product[]) => {
                if (productsForCurrentPage !== null) {
                    setSearchedProducts(productsForCurrentPage);
                } else {
                    console.log("No products found");
                }
            });
    }, [productId, setSearchedProducts])


    const indexOfLastItemOnPage = currentPage * productsPerPage;
    const indexOfFirstItemOnPage = indexOfLastItemOnPage - productsPerPage;

    const productsOnCurrentPage = filteredProducts
        .filter(value => value !== null)
        .slice(indexOfFirstItemOnPage, indexOfLastItemOnPage);

    return (
        <>
            <div className={"d-flex justify-content-center"}>
                <div className={"col-md-9 mt-2"}>
                    <div className={"search-bar input-group"}>
                        <SearchBar />
                    </div>
                </div>
            </div>

            <div className={"d-flex"}>
                <aside
                    className={"sidebar"}
                    style={{width: '250px', padding: '1rem'}}
                >
                    <Sidebar/>
                </aside>

                <section style={{flex: 1}}>
                    <ProductCard productsToDisplay={productsOnCurrentPage}/>
                    <PaginatorComponent
                        productsPerPage={productsPerPage}
                        onProductsPerPageChange={setProductsPerPage}
                        products={filteredProducts}
                        currentPageNumber={currentPage}
                        onPageNumberChange={setCurrentPage}
                    />
                </section>

            </div>
        </>
    )
}