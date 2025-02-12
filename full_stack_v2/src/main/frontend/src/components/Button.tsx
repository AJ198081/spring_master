import {MouseEventHandler, ReactNode} from "react";

export interface ButtonProps {
    disabled?: boolean;
    children: ReactNode;
    className: string;
    onClickHandler?: MouseEventHandler<HTMLButtonElement>;
    type?: "button" | "submit" | "reset";
}

export const Button = ({disabled = false, children, className, onClickHandler, type = 'button'}: ButtonProps) => {
    return (
        <button
            disabled={disabled}
            type={type}
            className={`${className}`}
            onClick={onClickHandler}
        >
            {children}
        </button>
    );
};
