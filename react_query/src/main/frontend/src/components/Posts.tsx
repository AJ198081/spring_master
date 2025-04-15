import {Fragment, useState, useEffect} from "react";
import {Box} from "@chakra-ui/react";
import {useQuery, useQueryClient} from "@tanstack/react-query";
import {fetchPosts} from "../api-client/postClient.ts";
import {ModalBS} from "./ModalBS.tsx";
import {Post} from "../domain/types/Post.ts";
import {Page} from "../domain/types/Page.ts";
import {Pagination} from "./Pagination.tsx";

export const Posts = () => {

    const [currentPage, setCurrentPage] = useState<number>(0);
    const [selectedPost, setSelectedPost] = useState<Post | null>(null);
    const [page, setPage] = useState<Page<Post>>();
    const [rowsPerPage, setRowsPerPage] = useState<number>(10)

    useEffect(() => {
        const pageToBeFetched = currentPage + 1;

        if (page && pageToBeFetched < page.totalPages) {

            queryClient.prefetchQuery({
                queryKey: ['posts', pageToBeFetched],
                queryFn: async () => {
                    return fetchPosts(pageToBeFetched)
                        .then(page => page.content);
                },
            }).catch(e => console.log(e));
        }
    }, [currentPage]);

    /*    const {data, isError, isLoading, error} = useQuery({
            queryKey: ['posts'],
            queryFn: async () => {
                return fetchAllPosts();
            },
            staleTime: 5 * 1000,
        });*/

    const {data, isError, isLoading, error} = useQuery({
        queryKey: ["posts", currentPage],
        queryFn: async () => {
            const pagePromise = fetchPosts(currentPage);

            return pagePromise.then(page => {
                setPage(page);
                return page.content
            });
        },
        staleTime: 5 * 1000,
    });

    const queryClient = useQueryClient();

    return (
        <>
            {isError &&
                <div>Unable to fetch data due to <em className={'italic'}>{error.message}</em>, please try later..
                </div>}
            {isLoading && <div>Loading...</div>}
            {page && <Pagination handlePageChange={setCurrentPage} rowsPerPage={rowsPerPage} handleChangeRowsPerPage={setRowsPerPage} page={page} />}
            {data && <>
                <div className={"h5"}>Rendering {data.length} posts</div>
                <Box as={"ul"}
                     listStyleType={"circle"}
                     className={"justify-start align-content-start w-auto"}
                >
                    {data?.map((post) => (
                        <Fragment key={post.id}>
                            <li className={'my-2 bg-body-tertiary'}
                                data-bs-toggle="modal"
                                data-bs-target="#staticBackdrop"
                                style={{cursor: 'pointer', width: "fit-content"}}
                                onClick={() => setSelectedPost(post)}
                            >
                                {post.title}
                            </li>
                        </Fragment>
                    ))}
                </Box>
                {selectedPost && <ModalBS post={selectedPost}/>}
            </>
            }
        </>
    );
}