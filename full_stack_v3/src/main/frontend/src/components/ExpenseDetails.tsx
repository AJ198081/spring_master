import {useLocation, useNavigate} from "react-router-dom";
import {ExpenseRequest, ExpenseResponse} from "../domain/Types.ts";
import {dateFormatter} from "../utils/Formatter.ts";
import {ChangeEvent, ChangeEventHandler, useState} from "react";
import {AxiosInstance} from "../service/api-client.ts";
import dayjs from "dayjs";


export const ExpenseDetails = () => {

    const currentExpense = useLocation().state.expense as ExpenseResponse;
    const [editMode, setEditMode] = useState<boolean>(false);
    const navigateTo = useNavigate();
    const [updatedExpense, setUpdatedExpense] = useState<ExpenseRequest>(currentExpense);


    const updateExpenseState: ChangeEventHandler<HTMLInputElement | HTMLTextAreaElement> = (event: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) =>  {
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
                    <button className={`btn btn-secondary ${editMode ? 'd-none' : ''}`}
                            onClick={() => setEditMode(true)}>
                        Edit
                    </button>
                    <button className={`btn btn-primary ${editMode ? '' : 'd-none'}`}
                            onClick={() => {
                                updateExpense(currentExpense);
                            }}>
                        Save
                    </button>
                    <button className={`btn btn-danger ms-2 ${editMode ? '' : 'd-none'}`}
                            onClick={() => setEditMode(false)}>
                        Cancel
                    </button>
                </div>
            </div>
        </div>
    );
}