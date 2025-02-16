import {createPortal} from "react-dom";
import {ForwardedRef, forwardRef, ReactNode, useImperativeHandle, useRef} from "react";

interface ModalProps {
    title: string,
    body: string,
    positiveConfirmation?: string,
    negativeConfirmation?: string,
    className?: string,
    onConfirm?: () => void,
    onCancel?: () => void,
    children?: ReactNode,
}

export interface DialogRef {
    openModal: () => void,
    closeModal: () => void
}

const Modal = ({
                          title,
                          body,
                          positiveConfirmation,
                          negativeConfirmation,
                          onConfirm,
                          onCancel,
                          children
                      }: ModalProps,
                      ref: ForwardedRef<DialogRef>
): ReactNode => {

    const dialogRef = useRef<HTMLDialogElement | null>(null);

    useImperativeHandle(
        ref,
        () => ({
            openModal: () => {
                dialogRef.current?.showModal();
            },
            closeModal: () => {
                dialogRef.current?.close();
            }
        }),
    );

    return createPortal(
        <dialog className="modal" ref={dialogRef} tabIndex={-1}>
            <div className="modal-dialog">
                <div className="modal-content">
                    <div className="modal-header">
                        <h5 className="modal-title">{title}</h5>
                    </div>
                    <div className="modal-body">
                        <p>{body}</p>
                    </div>
                    <div className="modal-footer">
                        <button type="button" className="btn btn-secondary"
                                onClick={onCancel}>{negativeConfirmation}</button>
                        <button type="button"
                                className="btn btn-primary"
                                onClick={onConfirm}>{positiveConfirmation}</button>
                    </div>
                </div>
            </div>
            {children}
        </dialog>,
        document.getElementById('modal')!
    );
};

export default forwardRef<DialogRef, ModalProps>(Modal);