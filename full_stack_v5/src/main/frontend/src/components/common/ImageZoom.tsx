import {useEffect, useState} from "react";
import {getImageById} from "../../services/ImageService.ts";

interface ImageZoomProps {
    downloadUrl: string;
}

export const ImageZoom = ({downloadUrl}: ImageZoomProps) => {

    const [productImage, setProductImage] = useState(null);
    const [isLoading, setIsLoading] = useState(false);

    useEffect(() => {

        setIsLoading(true);

        getImageById(downloadUrl)
            .then((image) => {
                if (image) {
                    setProductImage(image);
                }
            })
            .catch((error) => {
                    console.log(error);
                    setProductImage(null);
                }
            )
            .finally(
                () => setIsLoading(false)
            );

    }, [downloadUrl])

    return (<>
            {isLoading && <div>Loading...</div>}
            {!isLoading && productImage && <img src={productImage} alt={"Product Image"}/>}
            <div>

            </div>
        </>

    )
}