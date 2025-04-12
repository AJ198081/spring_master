import {useState} from "react";
import {Box} from "@chakra-ui/react";
import {Post} from "../domain/types/Post.ts";
import {useQuery} from "@tanstack/react-query";
import {fetchAllPosts} from "../api-client/postClient.ts";

export const Posts = () => {

    const [currentPage, _setCurrentPage] = useState<number>(0);
    const [_selectedPost, setSelectedPost] = useState<Post | null>(null);

    const {data} = useQuery({
        queryKey: ['posts', currentPage],
        queryFn: async () => {
            return fetchAllPosts();
        },
    });

    return (
        <>
            <Box as={"ul"} listStyleType={"circle"}>
                {data?.map((post) => (
                    <li key={post.id}
                        className={'my-2'}
                        onClick={() => setSelectedPost(post)}>
                        {post.title}
                    </li>
                ))}
            </Box>
        </>
    )
}