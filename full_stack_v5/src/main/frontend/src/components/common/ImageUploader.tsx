import {type ChangeEvent, type Dispatch, type FormEvent, type SetStateAction, useState} from "react";
import {nanoid} from "nanoid";
import {uploadImagesForProduct} from "../../services/ImageService.ts";
import {toast} from "react-toastify";

interface ImageUploaderProps {
    productId: number;
    setActiveStep: Dispatch<SetStateAction<number>>;
}

export interface ImageRequestType {
    id: string;
    name: string;
    file: File
}

export function mapFilesToImageRequests(files: File[]): ImageRequestType[] {
    return files.map(file => ({
        id: nanoid(),
        name: file.name,
        file
    }));
}

export const ImageUploader = ({productId, setActiveStep}: ImageUploaderProps) => {

    const [images, setImages] = useState<ImageRequestType[]>([]);

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
            uploadImagesForProduct(productId, images.map(image => image.file))
                .then(response => {
                    console.log(`typeof response ${typeof response}`);
                    console.log(`response ${response}`);
                    setActiveStep(prevState => prevState + 1);
                    toast.success("Image uploaded successfully.");
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
        setActiveStep(prevState => prevState - 1);
    };

    return (
        <div>
            <form onSubmit={handleImageUpload}>
                <div className={'container'}>
                    <h5 className={'mb-5'}>Upload product image(s)</h5>
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
                        Upload image(s)
                    </button>
                    <button 
                        type={'button'} 
                        onClick={handleImageStepReset} 
                        className="btn btn-outline-secondary my-5 ms-2"
                    >
                        Back to product page
                    </button>
                </div>
            </form>
        </div>
    )
};
