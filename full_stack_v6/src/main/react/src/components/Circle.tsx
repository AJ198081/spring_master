import CircleImage from "../assets/circle.svg";
import { Rectangle } from "./Rectangle";


interface CircleProps {
    onClick: () => void;
    clicked?: boolean;
}

export const Circle = ({onClick, clicked}: CircleProps) => {

    return (
        <>
            <img
                src={CircleImage}
                alt={"Circle image"}
                width={200}
                onClick={onClick}
            />
            <Rectangle clicked={clicked} />
        </>

    )
}
