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

    const renderPaginationItems = () => {
        const items = [];

        if (totalPages <= 5) {
            for (let i = 1; i <= totalPages; i++) {
                items.push(
                    <Pagination.Item
                        key={i}
                        active={i === currentPage}
                        onClick={() => onPageChange(i)}
                    >
                        {i}
                    </Pagination.Item>
                );
            }
            return items;
        }

        items.push(
            <Pagination.Item
                key={1}
                active={1 === currentPage}
                onClick={() => onPageChange(1)}
            >
                1
            </Pagination.Item>
        );

        let startPage = Math.max(2, currentPage - 1);
        let endPage = Math.min(totalPages - 1, currentPage + 1);

        if (currentPage <= 3) {
            endPage = Math.min(4, totalPages - 1);
        } else if (currentPage >= totalPages - 2) {
            startPage = Math.max(2, totalPages - 3);
        }

        if (startPage > 2) {
            items.push(<Pagination.Ellipsis key="ellipsis1" disabled />);
        }

        // Add middle pages
        for (let i = startPage; i <= endPage; i++) {
            items.push(
                <Pagination.Item
                    key={i}
                    active={i === currentPage}
                    onClick={() => onPageChange(i)}
                >
                    {i}
                </Pagination.Item>
            );
        }

        // Add last ellipsis if needed
        if (endPage < totalPages - 1) {
            items.push(<Pagination.Ellipsis key="ellipsis2" disabled />);
        }

        // Always show last page
        items.push(
            <Pagination.Item
                key={totalPages}
                active={totalPages === currentPage}
                onClick={() => onPageChange(totalPages)}
            >
                {totalPages}
            </Pagination.Item>
        );

        return items;
    };

    return (
        <Pagination className={"d-flex justify-content-center me-5"}>
            {renderPaginationItems()}
        </Pagination>
    );
}
