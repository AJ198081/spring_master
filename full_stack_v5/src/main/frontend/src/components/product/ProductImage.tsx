import {BASE_URL} from "../../services/Api.ts";

export interface ProductImageProps {
    imageDownloadUrl: string;
}

export const ProductImage = ({imageDownloadUrl}: ProductImageProps) => {

    if (!imageDownloadUrl) {
        return null;
    }

    return (
        <div>
            <img
                src={BASE_URL.concat('/images/').concat(imageDownloadUrl)}
                alt={"Main product"}
                style={{width: '100%', height: '200px', objectFit: 'contain'}}
            />
        </div>
    )
}
