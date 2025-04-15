/**
 * Pagination Component using Material UI's TablePagination
 * 
 * This component implements pagination functionality using Material UI's TablePagination component.
 * It provides a clean, accessible interface for navigating through paginated data.
 * 
 * Material UI's TablePagination offers several advantages:
 * 1. Built-in accessibility features
 * 2. Consistent styling with other Material UI components
 * 3. Support for changing rows per page
 * 4. Responsive design that works well on different screen sizes
 * 5. Internationalization support
 * 
 * The component is wrapped in a Box container for layout control.
 */

import {Page} from "../domain/types/Page.ts";
import {Post} from "../domain/types/Post.ts";
import {Box, TablePagination} from "@mui/material"

/**
 * Props interface for the Pagination component
 * 
 * @property {function} handlePageChange - Callback function that receives the new page number when page is changed
 * @property {function} handleChangeRowsPerPage - Callback function that receives the new rows per page value
 * @property {Page<Post>} page - The current page object containing pagination metadata
 * @property {number} rowsPerPage - The current number of rows per page
 */
export interface PaginationProps {
    handlePageChange: (newPage: number) => void,
    handleChangeRowsPerPage: (rows: number) => void,
    page: Page<Post>,
    rowsPerPage: number
}

/**
 * Pagination component that renders Material UI's TablePagination
 * 
 * @param {PaginationProps} props - The props for the component
 * @returns {JSX.Element} The rendered Pagination component
 */
export function Pagination({page, handlePageChange, handleChangeRowsPerPage, rowsPerPage}: PaginationProps) {
    return (
        <Box sx={{
            width: '100vw', // Full viewport width
            height: 'auto'// Auto height
        }}>
            <TablePagination
                component={"div"}  // The component used for the root node
                count={page.totalElements}  // Total number of elements across all pages
                onPageChange={(_event, page) => handlePageChange(page)}  // Callback fired when the page is changed
                page={page.number}  // The current page number (0-based)
                rowsPerPage={rowsPerPage}  // Number of rows per page
                onRowsPerPageChange={(event) => handleChangeRowsPerPage(parseInt(event.target.value, 10))}  // Callback fired when rows per page is changed
                // labelRowsPerPage="Items per page:"  // Custom label for rows per page dropdown
                // labelDisplayedRows={({from, to, count}) => `${from}-${to} of ${count}`}  // Custom displayed rows label
                // rowsPerPageOptions={[5, 10, 25]}  // Options for rows per page dropdown
            />
        </Box>
    );
}
