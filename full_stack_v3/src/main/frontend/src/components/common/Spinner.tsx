export interface SpinnerProps {
    textColor?: string;
    height?: string;
    spinnerSize?: string;
}

export const Spinner = ({textColor = 'text-danger', height = '100vh', spinnerSize = '5rem'}: SpinnerProps) => {

    return (
        <div className={'d-flex justify-content-center align-items-center'} style={{height}}>
            <div className={`spinner-border ${textColor}`}
                 style={{width: `${spinnerSize}`, height: `${spinnerSize}`}}
                 role="orderStatus">
                <span className="visually-hidden">Loading...</span>
            </div>
        </div>
    );
}