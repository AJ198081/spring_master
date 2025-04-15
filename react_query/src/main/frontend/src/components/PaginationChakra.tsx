import {ButtonGroup, IconButton, Pagination} from "@chakra-ui/react";
import {HiChevronLeft, HiChevronRight} from "react-icons/hi";
import {Page} from "../domain/types/Page.ts";
import {Post} from "../domain/types/Post.ts";

export function PaginationChakra(page: Page<Post>, setCurrentPage: (value: (((prevState: number) => number) | number)) => void) {

    return (
        <Pagination.Root
            count={page.totalElements}
            pageSize={page.size}
            page={page.number}
            siblingCount={1}
            onPageChange={(e) => setCurrentPage(e.page)}
            colorPalette="teal"
        >
            <ButtonGroup variant="ghost" size="md" colorScheme="gray" >
                <Pagination.PrevTrigger asChild>
                    <IconButton>
                        <HiChevronLeft/>
                    </IconButton>
                </Pagination.PrevTrigger>

                <Pagination.Items
                    render={(page) => (
                        <IconButton variant={{base: "ghost", _selected: "outline"}}>
                            {page.value}
                        </IconButton>
                    )}
                />

                <Pagination.NextTrigger asChild>
                    <IconButton>
                        <HiChevronRight/>
                    </IconButton>
                </Pagination.NextTrigger>
            </ButtonGroup>
        </Pagination.Root>
    )
}