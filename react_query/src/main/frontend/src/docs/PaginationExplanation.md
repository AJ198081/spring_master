# Material UI Pagination Implementation

This document explains how pagination is implemented in the project using Material UI's TablePagination component, along with comparisons to other pagination implementations.

## Material UI TablePagination

### Overview

The `Pagination.tsx` component uses Material UI's `TablePagination` component to provide a clean, accessible interface for navigating through paginated data.

### Key Features

- **Built-in accessibility**: Follows WAI-ARIA guidelines for screen readers
- **Consistent styling**: Matches other Material UI components
- **Rows per page**: Allows users to change the number of items displayed per page
- **Responsive design**: Works well on different screen sizes
- **Internationalization**: Supports multiple languages through customizable labels

### Implementation Details

```tsx
<TablePagination
    component={"div"}  // The component used for the root node
    count={page.totalElements}  // Total number of elements across all pages
    onPageChange={(_event, page) => handlePageChange(page)}  // Callback fired when the page is changed
    page={page.number}  // The current page number (0-based)
    rowsPerPage={rowsPerPage}  // Number of rows per page
    onRowsPerPageChange={(event) => handleChangeRowsPerPage(parseInt(event.target.value, 10))}  // Callback fired when rows per page is changed
/>
```

### Data Flow

1. The `Posts` component maintains state for `currentPage` and `rowsPerPage`
2. When the user changes the page or rows per page, the corresponding handler is called
3. The handler updates the state in the parent component
4. React Query refetches the data with the new pagination parameters
5. The backend API returns paginated data according to the Spring Boot `Page` format
6. The UI updates to display the new page of data

## Comparison with Other Implementations

### Chakra UI Pagination (PaginationChakra.tsx)

Chakra UI's pagination implementation offers:

- A more visually distinct pagination with page numbers
- Previous/Next buttons with icons
- Customizable styling through Chakra UI's theme system
- Different component structure with `Pagination.Root`, `Pagination.Items`, etc.

```tsx
<Pagination.Root
    count={page.totalElements}
    pageSize={page.size}
    page={page.number}
    siblingCount={1}
    onPageChange={(e) => setCurrentPage(e.page)}
    colorPalette="teal"
>
    {/* Pagination components */}
</Pagination.Root>
```

### Bootstrap Pagination (PaginationBS.tsx)

Bootstrap's pagination implementation offers:

- Traditional pagination with numbered pages
- Previous/Next buttons with text
- Simple HTML structure with Bootstrap classes
- Manual handling of disabled states for first/last pages

```tsx
<nav aria-label="Page navigation example">
    <ul className="pagination justify-content-center">
        <li className="page-item">
            <a className={`page-link ${page?.first ? 'disabled' : ''}`}>Previous</a>
        </li>
        {/* Page numbers */}
        <li className="page-item">
            <a className={`page-link ${page?.last ? 'disabled' : ''}`}>Next</a>
        </li>
    </ul>
</nav>
```

## When to Use Each Implementation

- **Material UI TablePagination**: Best for data tables or when you need rows-per-page functionality
- **Chakra UI Pagination**: Good for applications using Chakra UI as their design system
- **Bootstrap Pagination**: Suitable for applications using Bootstrap or when you need a traditional numbered pagination

## Backend Integration

All pagination implementations work with the same backend API, which returns data in the Spring Boot `Page` format:

```typescript
export async function fetchPosts(pageNumber: number = 0, pageSize: number = 10) {
    const response = await AxiosInstance.get(`/posts?page=${pageNumber}&size=${pageSize}`);
    return response.data;
}
```

The `Page` interface includes metadata such as:
- `content`: The actual data items
- `number`: Current page number (0-based)
- `size`: Page size
- `totalElements`: Total number of items
- `totalPages`: Total number of pages
- `first`/`last`: Flags indicating if this is the first or last page

This consistent backend interface allows for easy swapping between different pagination UI implementations.
