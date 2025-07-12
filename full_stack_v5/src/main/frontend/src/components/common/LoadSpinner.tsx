import {Spinner} from "react-bootstrap";

interface LoadSpinnerProps {
    variant?: string;
}

export const LoadSpinner = ({variant = 'secondary'}: LoadSpinnerProps) => {
    return (
        <div className={"d-flex justify-content-center align-items-center"} style={{height: '400px'}}>
            < Spinner animation="border" variant={variant}/>
        </div>
    )
}