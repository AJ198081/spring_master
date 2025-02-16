export interface ModalProps {
    triggerButtonClass?: string;
    triggerButtonLabel: string;
    modalHeader: string;
    modalBody: string;
    cancelButtonLabel: string;
    confirmButtonLabel: string;
    onConfirm: () => void;
    onCancel?: () => void;
}

export const Modal = ({
                          triggerButtonClass = '',
                          triggerButtonLabel = 'Delete',
                          modalHeader = 'Delete Expense',
                          modalBody = 'Are you sure you want to delete this expense?',
                          cancelButtonLabel = 'Cancel',
                          confirmButtonLabel = 'Delete',
                          onConfirm,
                      }: ModalProps) => {

    return <>
        <button className={`btn ${triggerButtonClass}`}
                data-bs-toggle="modal"
                data-bs-target="#staticBackdrop"
        >
            {triggerButtonLabel}
        </button>

        <div className="modal fade"
             id="staticBackdrop"
             data-bs-backdrop="static"
             data-bs-keyboard="false"
             tabIndex={-1}
             aria-labelledby="staticBackdropLabel"
             aria-hidden="true">
            <div className="modal-dialog">
                <div className="modal-content">
                    <div className="modal-header">
                        <h1 className="modal-title fs-5 text-danger" id="staticBackdropLabel">
                            {modalHeader}

                        </h1>
                        <button type="button"
                                className="btn-close"
                                data-bs-dismiss="modal"
                                aria-label="Close">
                        </button>
                    </div>
                    <div className="modal-body">
                        {modalBody}
                    </div>
                    <div className="modal-footer">
                        <button type="button" className="btn btn-secondary"
                                data-bs-dismiss="modal">{cancelButtonLabel}</button>
                        <button type="button"
                                className="btn btn-danger"
                                data-bs-dismiss="modal"
                                onClick={onConfirm}
                        >
                            {confirmButtonLabel}
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </>
}


/*
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

export default forwardRef<DialogRef, ModalProps>(Modal);*/
