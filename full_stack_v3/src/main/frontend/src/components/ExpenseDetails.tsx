import {useLocation, useNavigate} from "react-router-dom";
import {categoryOptions, dateFormat, ExpenseRequest, ExpenseResponse, ProblemDetail} from "../domain/Types.ts";
import {currencyFormatter} from "../utils/Formatter.ts";
import {ChangeEvent, ChangeEventHandler, useState} from "react";
import {AxiosInstance} from "../service/api-client.ts";
import dayjs from "dayjs";
import {Modal} from "./common/Modal.tsx";
import toast from "react-hot-toast";


export const ExpenseDetails = () => {

    const currentExpense = useLocation().state.expense as ExpenseResponse;
    const [updatedExpense, setUpdatedExpense] = useState<ExpenseRequest>(currentExpense);
    const [editMode, setEditMode] = useState<boolean>(false);
    const navigateTo = useNavigate();

    const updateExpenseState: ChangeEventHandler<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement> = (event: ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
        setUpdatedExpense(prevState => {
            return {
                ...prevState,
                [event.target.name]: event.target.value
            }
        });
    }

    const updateExpense = async (currentExpense: ExpenseResponse) => {
        try {

            updatedExpense.date = dayjs(updatedExpense.date).format(dateFormat);

            const updatePromise = AxiosInstance.put(`/api/v1/expenses/${currentExpense.expenseId}`, updatedExpense);

            await toast.promise(updatePromise, {
                loading: 'Updating expense...',
                success: 'Expense updated successfully',
                error: 'Error updating expense'
            });

            await updatePromise;
        } catch (error) {
            if (error && typeof error === 'object' && 'response' in error && error.response) {
                console.log("Ist")
                const problemDetail = error.response as ProblemDetail;
                toast.error(problemDetail.detail || 'Error deleting expense');
            } else {
                toast.error('Error deleting expense');
            }
        } finally {
            navigateTo("/");
        }
    };

    const deleteExpense = async (expense: ExpenseResponse) => {
        try {
            const deletePromise = AxiosInstance.delete(`/api/v1/expenses/${expense.expenseId}`);
            await toast.promise(deletePromise, {
                loading: `Deleting expense ${expense.name} of ${currencyFormatter.format(Number(expense.amount))} from ${expense.date}...`,
                success: 'Expense deleted successfully',
                error: 'Error deleting expense'
            });
            await deletePromise;
        } catch (error) {
            console.log(error);
        } finally {
            navigateTo("/");
        }
    };

    return (
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
                                             defaultValue={dayjs(currentExpense.date).format(dateFormat)}
                                    />
                                    : currentExpense.date
                            }</td>
                        </tr>
                        <tr>
                            <th scope="col">Category</th>
                            <td>
                                {editMode
                                    ? <select className="form-control"
                                              name={'category'}
                                              onChange={updateExpenseState}
                                              defaultValue={currentExpense.category}>
                                        <option value={currentExpense.category}>{currentExpense.category}</option>
                                        {categoryOptions
                                            .filter(category => category !== currentExpense.category)
                                            .map(category => <option key={category}
                                                                     value={category}>{category}</option>)
                                        }
                                    </select>
                                    : currentExpense.category}
                            </td>
                        </tr>
                        </tbody>
                    </table>
                    <button className={`btn btn-outline-secondary ${editMode ? 'd-none' : ''}`}
                            onClick={() => setEditMode(true)}>
                        Edit
                    </button>
                    <Modal
                        triggerButtonClass={`btn-outline-danger ms-2 ${editMode ? 'd-none' : ''}`}
                        triggerButtonLabel={'Delete'}
                        modalHeader={'Delete Confirmation'}
                        modalBody={'Are you sure you want to delete this expense?'}
                        cancelButtonLabel={'Cancel'}
                        confirmButtonLabel={'Delete'}
                        onConfirm={() => deleteExpense(currentExpense)}
                    />
                    <button className={`btn btn-outline-primary ${editMode ? '' : 'd-none'}`}
                            onClick={() => updateExpense(currentExpense)}>
                        Save
                    </button>
                    <button className={`btn btn-outline-danger ms-2 ${editMode ? '' : 'd-none'}`}
                            onClick={() => setEditMode(false)}>
                        Cancel
                    </button>
                </div>
            </div>
        </div>
    );
}