import {ReactNode} from "react";
import {Post} from "../domain/types/Post.ts";
import {useQuery} from "@tanstack/react-query";
import {fetchPostById} from "../api-client/postClient.ts";

export interface ModalBSProps {
    post: Post,
}

export function ModalBS({post}: ModalBSProps): ReactNode {

    const {data} = useQuery({
        queryKey: ['post', post.id],
        queryFn: () => fetchPostById(post.id),
        staleTime: 5 * 1000,
    });

    return (
        <div className="modal fade"
             id="staticBackdrop"
             data-bs-backdrop="static"
             data-bs-keyboard="true"
             tabIndex={-1}
             aria-labelledby="staticBackdropLabel"
             aria-hidden="true"
        >
            <div className="modal-dialog">
                <div className="modal-content">
                    {data
                        ? <>
                            <div className="modal-header">
                                <h1 className="modal-title fs-5" id="staticBackdropLabel">{data.title}</h1>
                                <button type="button" className="btn-close" data-bs-dismiss="modal"
                                        aria-label="Close"></button>
                            </div>
                            <div className="modal-body">
                                {data.body}
                            </div>
                        </>
                        : <div>Fetching.....</div>
                    }
                    <div className="modal-footer justify-content-between">
                        {data && <div className={"fw-lighter"}>
                            Posted by: {data.user.username}
                        </div>}
                        <button type="button" className="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                    </div>
                </div>
            </div>
        </div>
    )
}