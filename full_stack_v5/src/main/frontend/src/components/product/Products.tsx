import {ProductCard} from "./ProductCard.tsx";
import {SearchBar} from "../search/SearchBar.tsx";
import {useState, useEffect} from "react";
import {type Product, useProductStore} from "../../store/ProductStore.tsx";
import {PaginatorComponent} from "../common/PaginatorComponent.tsx";
import {Sidebar} from "../common/SideBar.tsx";
import {useLocation, useParams} from "react-router-dom";
import {getProductsById} from "../../services/ProductService.ts";

export const Products = () => {

    const [currentPage, setCurrentPage] = useState(1);
    const [productsPerPage, setProductsPerPage] = useState(10);
    const filteredOrAllProducts = useProductStore(state => state.productsToShow());
    const setFilteredProducts = useProductStore(state => state.setFilteredProducts);

    const {id} = useParams();

    const location = useLocation();
    const queryParams = new URLSearchParams(location.search);

    const productId = queryParams.get('search') ?? id ?? "";

    useEffect(() => {
        getProductsById(productId)
            .then((productsForCurrentPage: Product[]) => {
                if (productsForCurrentPage !== null) {
                    setFilteredProducts(productsForCurrentPage);
                } else {
                    console.log("No products found");
                }
            });
    }, [productId, setFilteredProducts])

    const indexOfLastItemOnPage = currentPage * productsPerPage;
    const indexOfFirstItemOnPage = indexOfLastItemOnPage - productsPerPage;

    const productsOnCurrentPage = filteredOrAllProducts
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
                        products={filteredOrAllProducts}
                        currentPageNumber={currentPage}
                        onPageNumberChange={setCurrentPage}
                    />
                </section>

            </div>
        </>
    )
}