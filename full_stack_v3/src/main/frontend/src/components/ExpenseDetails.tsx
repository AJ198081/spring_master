import {useLocation, useNavigate} from "react-router-dom";
import {ExpenseResponse} from "../domain/Types.ts";
import {dateFormatter} from "../utils/Formatter.ts";
import {useState} from "react";
import {AxiosInstance} from "../service/api-client.ts";


export const ExpenseDetails = () => {

    const currentExpense = useLocation().state.expense as ExpenseResponse;
    const [editMode, setEditMode] = useState<boolean>(false);
    const navigateTo = useNavigate();


    const updateExpense = (currentExpense: ExpenseResponse) => {
        AxiosInstance.put(`/api/v1/expenses/${currentExpense.expenseId}`, currentExpense)
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
                            <td>{currentExpense.name}</td>
                        </tr>
                        <tr>
                            <th scope="col">Note</th>
                            <td>{currentExpense.note}</td>
                        </tr>
                        <tr>
                            <th scope="col">Amount</th>
                            <td>{currentExpense.amount}</td>
                        </tr>
                        <tr>
                            <th scope="col">Date</th>
                            <td>{currentExpense.date instanceof Date ? dateFormatter.format(currentExpense.date) : currentExpense.date}</td>
                        </tr>
                        <tr>
                            <th scope="col">Category</th>
                            <td>{currentExpense.category}</td>
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