import {Pagination} from "react-bootstrap";

export interface PaginatorProps {
    totalItems: number,
    itemsPerPage: number,
    currentPage: number,
    onPageChange: (page: number) => void,
}

export const Paginator = ({
                              totalItems,
                              itemsPerPage,
                              currentPage,
                              onPageChange,
                          }: PaginatorProps) => {

    const totalPages = Math.ceil(totalItems / itemsPerPage);

    return (
        <Pagination className={"d-flex justify-content-center me-5"}>
                       {Array.from({length: totalPages}, (_, i) => {
                           const pageNumber = i + 1;
                           return (
                               <Pagination.Item
                                key={pageNumber}
                                active={pageNumber === currentPage}
                                onClick={() => onPageChange(pageNumber)}
                               >
                                   {pageNumber}
                               </Pagination.Item>
                           )
                       })}
        </Pagination>
    );
}