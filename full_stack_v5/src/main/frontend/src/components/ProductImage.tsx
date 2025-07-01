import {useEffect, useState} from "react";

export interface ProductImageProps {
    imageDownloadUrl: string;
}

export const ProductImage = ({imageDownloadUrl}: ProductImageProps) => {

    const [imageUrl, setImageUrl] = useState<string | null>(null);

    useEffect(() => {

        fetch(`http://localhost:10006/api/v1/images/${imageDownloadUrl}`)
            .then(response => response.blob())
            .then(blob => {

                const fileReader = new FileReader();

                fileReader.readAsDataURL(blob);

                fileReader.onloadend = (() => {
                    setImageUrl(fileReader.result as string);
                });

            })
            .catch(error => {
                console.log(error);
            })
    }, [imageDownloadUrl]);

    if (!imageUrl) {
        return null;
    }

    return (
        <div>
            <img
                src={imageUrl}
                alt={"Main product"}
                style={{width: '100%', height: '200px', objectFit: 'contain'}}
            />
        </div>
    )
}
