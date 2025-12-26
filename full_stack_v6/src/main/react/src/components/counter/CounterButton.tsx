import Button, {type ButtonProps} from "react-bootstrap/Button";

interface CounterButtonProps extends ButtonProps {
    buttonText: string;
    onClickHandler: () => void;
}

export const CounterButton = ({buttonText, onClickHandler, ...props}: CounterButtonProps) => {

    return <Button
        style={{
            minWidth: "100px"
        }}
        className={"m-2"}
        onClick={onClickHandler}
        {...props}
    >
        {buttonText}
    </Button>

}