import RectangleImage from "../assets/rectangle.svg";
import {useState} from "react";
import toast from "react-hot-toast";
import {Typography} from "@mui/material";

export interface RectangleProps {
    clicked?: boolean;
}

export const Rectangle = ({clicked}: RectangleProps) => {

    const [imageClickEnabled, setImageClickEnabled] = useState(true);

    const handleImageClick = () => {
        if (imageClickEnabled) {

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

    return (
        <>
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
            <Typography>`Clicked {clicked ? "True" : "False"}`</Typography>
        </>
    );
}