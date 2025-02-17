import {useFormik} from "formik";
import dayjs from "dayjs";
import {date, number, object, string} from "yup";
import {AxiosInstance} from "../service/api-client.ts";
import {ExpenseRequest} from "../domain/Types.ts";
import toast from "react-hot-toast";
import {AxiosError} from "axios";
import {useNavigate} from "react-router-dom";

export const AddExpense = () => {

    const navigateTo = useNavigate();
    const expenseValidationSchema = object({
            name: string().min(2, 'Expense name can not be less than 2 character').required('Expense name is required'),
            note: string().nullable(),
            category: string(),
            amount: number().required('Expense amount is required'),
            date: date().default(() => dayjs().toDate()).required('Expense Date is required')
        })
    ;
    const {values, errors, handleChange, handleSubmit, resetForm, handleBlur} = useFormik({
        initialValues: {
            name: '',
            note: '',
            category: '',
            amount: 0,
            date: ''
        },
        onSubmit: async (values) => {
            const expenseRequest: ExpenseRequest = {
                name: values.name,
                note: values.note,
                category: values.category,
                amount: values.amount.toFixed(2),
                date: dayjs(values.date).format('DD/MM/YYYY'),
            };

            try {
                const expenseRequestPromise = AxiosInstance.post('/api/v1/expenses', expenseRequest);

                await toast.promise(expenseRequestPromise, {
                    loading: 'Adding expense...',
                    success: 'Expense added successfully',
                })

                await expenseRequestPromise;
                resetForm();
                navigateTo("/");
            } catch (error) {
                if (error instanceof AxiosError) {
                    const errorData = (error as AxiosError).response?.data;
                    if (typeof errorData === 'object' && errorData && 'detail' in errorData) {
                        console.log('Error', errorData.detail);
                        toast.error((errorData as { detail: string }).detail);
                    }
                    if (error.response?.status === 500) {
                        toast.error(error.response.data.message);
                    }
                }
            }
        },
        validationSchema: expenseValidationSchema,
    });

    return (
        <div className={'d-flex justify-content-center align-items-center mt-3'}>
            <div className={'container col-md-4 col-sm-8 col-xs-12'}>
                <form className={'needs-validation'} noValidate={true}
                      onSubmit={(e) => handleSubmit(e)}
                      onReset={() => resetForm()}
                      onBlur={handleBlur}>
                    <div className="mb-3">
                        <label htmlFor="name" className="form-label">Name</label>
                        <input type="text"
                               id="name"
                               className="form-control"
                               value={values.name}
                               onChange={handleChange}
                               placeholder="Expense name"/>
                        <div className="invalid-feedback">
                            {errors.name}
                        </div>
                    </div>
                    <div className="mb-3">
                        <label htmlFor="note" className="form-label">Note</label>
                        <textarea
                            className="form-control"
                            id="note"
                            value={values.note}
                            onChange={handleChange}
                            rows={3}
                            placeholder="Desciption of the expense">
                        </textarea>
                        <div className="invalid-feedback">
                            {errors.note}
                        </div>
                    </div>
                    <div className="mb-3">
                        <label htmlFor="amount" className="form-label">Amount</label>
                        <input type="number"
                               id="amount"
                               className="form-control"
                               value={values.amount}
                               onChange={handleChange}
                               placeholder="Expense amount"/>
                        <div className="invalid-feedback">
                            {errors.note}
                        </div>
                    </div>
                    <div className="mb-3">
                        <label htmlFor="category" className="form-label">Category</label>
                        <input type="text"
                               id="category"
                               className="form-control"
                               value={values.category}
                               onChange={handleChange}
                               placeholder="Expense category"/>
                        <div className="invalid-feedback">
                            {errors.category}
                        </div>
                    </div>

                    <div className="mb-3">
                        <label htmlFor="date" className="form-label">Date</label>
                        <input type="date"
                               id="date"
                               className="form-control"
                               value={values.date}
                               onChange={handleChange}
                               placeholder={dayjs().format('DD/MM/YYYY')}/>
                        <div className="invalid-feedback">
                            {errors.date}
                        </div>
                    </div>
                    <button type="reset" name={"reset"} className="btn btn-outline-danger me-2">Reset</button>
                    <button type="submit" name={"submit"} className="btn btn-outline-primary">Submit</button>
                </form>
            </div>
        </div>
    )
}