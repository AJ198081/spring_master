import {ReactNode} from "react";
import {Post} from "../domain/types/Post.ts";
import {UseMutationResult, useQuery} from "@tanstack/react-query";
import {fetchPostById} from "../api-client/postClient.ts";
import toast from "react-hot-toast";
import 'bootstrap/dist/js/bootstrap.esm.js';

export interface ModalBSProps {
    post: Post,
    deleteMutation:  UseMutationResult<void, Error, number, unknown>,
}

export function ModalBS({post, deleteMutation}: ModalBSProps): ReactNode {

    const {data} = useQuery({
        queryKey: ['post', post.id],
        queryFn: () => fetchPostById(post.id),
        staleTime: 5 * 1000,
    });

    const updatePostHandler = () => {

    };

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

                    <hr/>

                    <div className="d-flex justify-content-end gap-2 m-2">
                        <button type="button" className="btn btn-primary" onClick={updatePostHandler}>Update</button>

                        <button type="button" className="btn btn-danger" onClick={() => {

                            const deletePromise = deleteMutation.mutateAsync(post.id);



                            toast.promise(deletePromise, {
                                loading: 'Loading',
                                success: `Deleted - ${post.title}`,
                                error: 'Error whilst deleting'
                            });

                            const modalCloseButton = document.getElementById('close-modal');
                            if (modalCloseButton) {
                                modalCloseButton.click();
                            }


                        /*    deletePromise.then(() => {
                                // Close the modal if the promise resolves successfully
                                const modalElement = document.getElementById('staticBackdrop') as HTMLElement;
                                if (modalElement) {
                                    const modal = Modal.getOrCreateInstance(modalElement); // Ensures the modal instance exists
                                    modal?.hide();
                                    console.log(modalElement.classList)
                                    modalElement.classList.remove('fade'); // Ensures attribute is set to dismiss the modal
                                }
                            });*/
                            
                        }}>
                            Delete
                        </button>
                    </div>
                    <div className="modal-footer justify-content-between">
                        {data && <div className={"fw-lighter"}>
                            Posted by: {data.user.username}
                        </div>}
                        <button id={"close-modal"} type="button" className="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                    </div>
                </div>
            </div>
        </div>
    )
}