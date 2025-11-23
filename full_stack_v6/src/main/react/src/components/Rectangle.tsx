import RectangleImage from "../assets/rectangle.svg";
import {useState} from "react";
import toast from "react-hot-toast";

interface RectangleProps {
    clicked?: boolean;
}

export const Rectangle = ({clicked}: RectangleProps) => {

    const [imageClickEnabled, setImageClickEnabled] = useState(true);

    const handleImageClick = () => {
        if (imageClickEnabled) {
            console.log('Image click called');

            setImageClickEnabled(false);
            toast.success('Product added to cart', {
                // id: 'product-added',
                position: 'top-center',
                duration: 5000,
            });
            setTimeout(() => {
                setImageClickEnabled(true);
            }, 5000);
        }
    }

    console.log(`Parent component clicked: ${clicked}`)

    return (
        <img
            src={RectangleImage}
            alt={"Rectangle image"}
            width={300}
            aria-disabled={true}
            onClick={() => {
                console.log('Image clicked!!');
                handleImageClick();
            }}
        />
    )
}