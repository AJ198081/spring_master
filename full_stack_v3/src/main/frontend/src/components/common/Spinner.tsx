export interface SpinnerProps {
    textColor?: string;
}

export const Spinner = ({textColor = 'text-danger'}: SpinnerProps) => {

    return (
        <div className={'d-flex justify-content-center align-items-center'} style={{height: '100vh'}}>
            <div className={`spinner-border ${textColor}`}
                 style={{width: '5rem', height: '5rem'}}
                 role="status">
                <span className="visually-hidden">Loading...</span>
            </div>
        </div>
    );
}