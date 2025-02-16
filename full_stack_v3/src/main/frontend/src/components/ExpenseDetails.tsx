import {useLocation, useNavigate} from "react-router-dom";
import {ExpenseRequest, ExpenseResponse} from "../domain/Types.ts";
import {dateFormatter} from "../utils/Formatter.ts";
import {ChangeEvent, ChangeEventHandler, useRef, useState} from "react";
import {AxiosInstance} from "../service/api-client.ts";
import dayjs from "dayjs";
import {DialogRef} from "./common/Modal.tsx";


export const ExpenseDetails = () => {

    const currentExpense = useLocation().state.expense as ExpenseResponse;
    const [editMode, setEditMode] = useState<boolean>(false);
    const navigateTo = useNavigate();
    const [updatedExpense, setUpdatedExpense] = useState<ExpenseRequest>(currentExpense);
    const dialogRef = useRef<DialogRef>(null);


    const updateExpenseState: ChangeEventHandler<HTMLInputElement | HTMLTextAreaElement> = (event: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
        setUpdatedExpense(prevState => {
            return {
                ...prevState,
                [event.target.name]: event.target.value
            }
        });
    }


    const updateExpense = (currentExpense: ExpenseResponse) => {
        AxiosInstance.put(`/api/v1/expenses/${currentExpense.expenseId}`, updatedExpense)
            .then(response => {
                console.log(response.data);
            })
            .catch(error => console.log(error))
            .finally(() => navigateTo("/"));
    };

    const deleteExpense = (expenseId: string) => {
        AxiosInstance.delete(`/api/v1/expenses/${expenseId}`)
            .then(response => {
                console.log(response.data);
            })
            .catch(error => console.log(error))
            .finally(() => navigateTo("/"));
    };


    return (
        <>
            <div className={'container mt-5 w-50'}>
                <div className="card">
                    <div className="card-header bg-dark-subtle text-dark d-flex justify-content-between">
                        <span>{currentExpense.name}</span>
                        <button className={'btn btn-close btn-danger'}
                                data-bs-theme={'dark'}
                                onClick={() => navigateTo("/")}
                        ></button>
                    </div>
                    <div className="card-body">
                        <table className="table">
                            <tbody>
                            <tr>
                                <th scope="row">Name</th>
                                <td>
                                    {editMode
                                        ? <input type="text" className="form-control"
                                                 name={'name'}
                                                 onChange={updateExpenseState}
                                                 defaultValue={currentExpense.name}/>
                                        : currentExpense.name}
                                </td>
                            </tr>
                            <tr>
                                <th scope="col">Note</th>
                                <td>
                                    {editMode
                                        ? <textarea className="form-control" rows={3}
                                                    name={'note'}
                                                    onChange={updateExpenseState}
                                                    defaultValue={currentExpense.note}/>
                                        : currentExpense.note}
                                </td>
                            </tr>
                            <tr>
                                <th scope="col">Amount</th>
                                <td>
                                    {editMode
                                        ? <input type="number" className="form-control"
                                                 name={'amount'}
                                                 onChange={updateExpenseState}
                                                 defaultValue={currentExpense.amount}/>
                                        : currentExpense.amount}
                                </td>
                            </tr>
                            <tr>
                                <th scope="col">Date</th>
                                <td>{
                                    editMode
                                        ? <input type={'date'} className="form-control"
                                                 name={'date'}
                                                 onChange={updateExpenseState}
                                                 defaultValue={dayjs(currentExpense.date).format('YYYY-MM-DD')}
                                        />
                                        : currentExpense.date instanceof Date ? dateFormatter.format(currentExpense.date) : currentExpense.date}</td>
                            </tr>
                            <tr>
                                <th scope="col">Category</th>
                                <td>
                                    {editMode
                                        ? <input type="text" className="form-control"
                                                 name={'category'}
                                                 onChange={updateExpenseState}
                                                 defaultValue={currentExpense.category}/>
                                        : currentExpense.category}
                                </td>
                            </tr>
                            </tbody>
                        </table>
                        <button className={`btn btn-outline-secondary ${editMode ? 'd-none' : ''}`}
                                onClick={() => setEditMode(true)}>
                            Edit
                        </button>
                        <button className={`btn btn-outline-danger ms-2 ${editMode ? 'd-none' : ''}`}
                                data-bs-toggle="modal"
                                data-bs-target="#staticBackdrop"
                                onClick={() => {
                                    dialogRef.current?.openModal();
                                }}>
                            Delete
                        </button>
                        <button className={`btn btn-outline-primary ${editMode ? '' : 'd-none'}`}
                                onClick={() => {
                                    updateExpense(currentExpense);
                                }}>
                            Save
                        </button>
                        <button className={`btn btn-outline-danger ms-2 ${editMode ? '' : 'd-none'}`}
                                onClick={() => setEditMode(false)}>
                            Cancel
                        </button>
                    </div>
                </div>
            </div>

      {/*      <Modal
                ref={dialogRef}
                title="Delete Confirmation"
                body="Are you sure you want to delete this expense?"
                negativeConfirmation="Cancel"
                positiveConfirmation="Delete"
                onConfirm={() => {
                    deleteExpense(currentExpense.expenseId)
                    dialogRef.current?.closeModal();
                }}
                onCancel={() => {
                    dialogRef.current?.closeModal();
                }}
            />*/}

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
                                Delete Confirmation

                            </h1>
                            <button type="button" className="btn-close" data-bs-dismiss="modal"
                                    aria-label="Close"></button>
                        </div>
                        <div className="modal-body">
                            Are you sure you want to delete this expense?

                        </div>
                        <div className="modal-footer">
                            <button type="button" className="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                            <button type="button"
                                    className="btn btn-danger"
                                    data-bs-dismiss="modal"
                                    onClick={() => {deleteExpense(currentExpense.expenseId)}}
                            >
                                Delete
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </>
    );
}