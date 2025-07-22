import {Link, useNavigate, useParams} from "react-router-dom";
import {type ChangeEvent, type FormEvent, useState} from "react";
import {uploadImagesForProduct} from "../../services/ImageService.ts";
import {toast} from "react-toastify";
import {type ImageRequestType, mapFilesToImageRequests} from "../common/ImageUploader.tsx";
import {Modal, Button} from "react-bootstrap";

export const ImageUpdater = () => {
    const {productId} = useParams();

    const [images, setImages] = useState<ImageRequestType[]>([]);
    const [showModal, setShowModal] = useState<boolean>(true);
    const [replaceAll, setReplaceAll] = useState<boolean>(false);
    const navigateTo = useNavigate();

    const handleImageChange = (e: ChangeEvent<HTMLInputElement>) => {

        const files = e.target.files && Array.from(e.target.files);

        if (files && files?.length > 0) {
            setImages(prevState => [...prevState, ...mapFilesToImageRequests(files)])
        } else {
            setImages([]);
        }
    }

    const handleImageUpload = (e: FormEvent<HTMLFormElement>) => {
        e.preventDefault();

        if (images.length > 0) {
            uploadImagesForProduct(Number(productId), images.map(image => image.file), replaceAll)
                .then(response => {
                    console.log(`typeof response ${typeof response}`);
                    console.log(`response ${response}`);
                    const action = replaceAll ? "replaced" : "added";
                    toast.success(`Images ${action} successfully.`);
                    navigateTo(`/products/${productId}/details`);
                })
                .catch(e => {
                        console.log(`${e}`)
                        toast.error(e);
                    }
                );
        }
    }

    const handleImageStepReset = () => {
        setImages([]);
    };

    const handleModalClose = (deleteAllPreviousImages: boolean) => {
        setReplaceAll(deleteAllPreviousImages);
        setShowModal(false);
    };

    return (
        <div>
            <Modal show={showModal} onHide={() => handleModalClose(false)} backdrop="static" keyboard={false} centered>
                <Modal.Header>
                    <Modal.Title>Image Upload Options</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <p>Would you like to add additional images or delete all previous images and replace them with new ones?</p>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="primary" onClick={() => handleModalClose(false)}>
                        Add Additional Images
                    </Button>
                    <Button variant="danger" onClick={() => handleModalClose(true)}>
                        Delete All and Replace
                    </Button>
                </Modal.Footer>
            </Modal>

            <form onSubmit={handleImageUpload}>
                <div className={'container'}>
                    <h5 className={'my-5'}>
                        {replaceAll ? 'Replace all product images' : 'Upload additional product image(s)'}
                    </h5>
                    <div className="d-flex align-items-center, mb-2 input-group">
                        <input
                            type="file"
                            multiple
                            accept="image/*"
                            onChange={handleImageChange}
                            className="me-2 form-control"
                        />
                    </div>
                    <button
                        type="submit"
                        className="btn btn-primary my-5"
                    >
                        {replaceAll ? 'Replace images' : 'Upload additional image(s)'}
                    </button>
                    <Link
                        to={'/products/' + productId + '/details'}
                        onClick={handleImageStepReset}
                        className="btn btn-outline-secondary my-5 ms-2"
                    >
                        Back to product page
                    </Link>
                </div>
            </form>
        </div>
    )
}
