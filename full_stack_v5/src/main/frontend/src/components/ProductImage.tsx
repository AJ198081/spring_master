import {useEffect, useState} from "react";

export interface ProductImageProps {
    productId: number;
}

export const ProductImage = ({productId}: ProductImageProps) => {

    const [imageUrl, setImageUrl] = useState<string | null>(null);

    useEffect(() => {

        fetch(`http://localhost:8080/api/products/${productId}/image`)
            .then(response => response.blob())
            .then(blob => {

                const fileReader = new FileReader();
                fileReader.onloadend = (() => {
                    setImageUrl(fileReader.result as string);
                });
                fileReader.readAsDataURL(blob);
            })
            .catch(error => {
                console.log(error);
            })
    }, [productId]);

    if (!imageUrl) {
        return null;
    }

    return (
        <div>
            <img src={imageUrl} alt={"product image"} />
        </div>
    )
}
