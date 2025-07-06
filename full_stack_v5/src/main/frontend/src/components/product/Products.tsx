import {ProductCard} from "./ProductCard.tsx";
import {SearchBar} from "../search/SearchBar.tsx";
import {useState} from "react";
import {useProductStore} from "../../store/ProductStore.tsx";
import {PaginatorComponent} from "../common/PaginatorComponent.tsx";

export const Products = () => {

    const [currentPage, setCurrentPage] = useState(1);
    const [productsPerPage, setProductsPerPage] = useState(10);

    const indexOfLastItemOnPage = currentPage * productsPerPage;
    const indexOfFirstItemOnPage = indexOfLastItemOnPage - productsPerPage;
    const filteredOrAllProducts = useProductStore(state => state.productsToShow());
    const productsOnCurrentPage = filteredOrAllProducts
        .filter(value => value !== null)
        .slice(indexOfFirstItemOnPage, indexOfLastItemOnPage);
    console.log(productsOnCurrentPage);

    return (
        <>
            <div className={"d-flex justify-content-center"}>
                <div className={"col-md-9 mt-2"}>
                    <div className={"search-bar input-group"}>
                        <SearchBar/>
                    </div>
                </div>
            </div>

            <div className={"d-flex"}>
                <aside
                    className={"sidebar"}
                    style={{width: '250px', padding: '1rem'}}
                >
                    Sidebar coming here...
                </aside>

                <section style={{flex: 1}}>
                    <ProductCard productsToDisplay={productsOnCurrentPage}/>
                    <PaginatorComponent
                        productsPerPage={productsPerPage}
                        onProductsPerPageChange={setProductsPerPage}
                        products={filteredOrAllProducts}
                        currentPageNumber={currentPage}
                        onPageNumberChange={setCurrentPage}
                    />
                </section>

            </div>
        </>
    )
}