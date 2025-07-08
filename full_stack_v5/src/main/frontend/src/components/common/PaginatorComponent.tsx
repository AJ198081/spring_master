import type {Product} from "../../store/ProductStore.tsx";
import {Paginator} from "./Paginator.tsx";

interface PaginatorComponentProps {
    productsPerPage: number,
    onProductsPerPageChange: (e: number) => void,
    products: Product[],
    currentPageNumber: number,
    onPageNumberChange: (value: number) => void
}

export const PaginatorComponent = ({
                                       products,
                                       currentPageNumber,
                                       onPageNumberChange,
                                       productsPerPage,
                                       onProductsPerPageChange
                                   }: PaginatorComponentProps) => {

    return (<div className={"pagination d-flex justify-content-center mt-5 mb-5"}>
            <div className="me-1">
                <select
                    className="form-select"
                    value={productsPerPage}
                    onChange={e => {
                        onProductsPerPageChange(Number(e.target.value));
                        if (products.length > 0) {
                            onPageNumberChange(1);
                        }
                        }
                    }
                >
                    <option value={5}>5 per page</option>
                    <option value={10}>10 per page</option>
                    <option value={50}>50 per page</option>
                    <option value={100}>100 per page</option>
                </select>
            </div>
            {products && products.length > 0 && <Paginator
                itemsPerPage={productsPerPage}
                currentPage={currentPageNumber}
                onPageChange={onPageNumberChange}
                totalItems={products.length}
            />}
        </div>
    )
}