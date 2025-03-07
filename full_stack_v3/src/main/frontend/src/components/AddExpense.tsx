import {useFormik} from "formik";
import dayjs from "dayjs";
import {AxiosInstance} from "../service/api-client.ts";
import {categoryOptions, dateFormat, ExpenseRequest, expenseSchemaValidations} from "../domain/Types.ts";
import toast from "react-hot-toast";
import {AxiosError} from "axios";
import {useNavigate} from "react-router-dom";
import {CategorySelect} from "./CategoryDropdown.tsx";

export const AddExpense = () => {

    const navigateTo = useNavigate();

    const createExpense = async (values: ExpenseRequest) => {
        const expenseRequest: ExpenseRequest = {
            name: values.name,
            note: values.note,
            category: values.category,
            amount: values.amount,
            date: dayjs(values.date).format(dateFormat),
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
    };

    const {values, errors, touched, handleChange, handleSubmit, resetForm, handleBlur} = useFormik({
        initialValues: {
            name: '',
            note: 'Default notes',
            category: '',
            amount: 0,
            date: dayjs().utc(true).format(dateFormat),
        },
        onSubmit: createExpense,
        validationSchema: expenseSchemaValidations,
    });

    return (
        <div className={'d-flex justify-content-center align-items-center mt-3'}>
            <div className={'container col-md-4 col-sm-8 col-xs-12'}>
                <form className={'needs-validation'} noValidate={false}
                      onSubmit={(e) => handleSubmit(e)}
                      onReset={() => resetForm()}
                      onBlur={handleBlur}>
                    <div className="mb-3">
                        <label htmlFor="name" className="form-label">Name</label>
                        <input type="text"
                               id="name"
                               className={`form-control ${errors.name && touched.name ? 'is-invalid' : ''}`}
                               value={values.name}
                               onChange={handleChange}
                               onBlur={handleBlur}
                               placeholder="Expense name"/>
                        {touched.name && <div className="invalid-feedback">
                            {errors.name}
                        </div>}
                    </div>
                    <div className="mb-3">
                        <label htmlFor="note" className="form-label">Note</label>
                        <textarea
                            className={`form-control ${errors.note && touched.note ? 'is-invalid' : ''}`}
                            id="note"
                            value={values.note}
                            onChange={handleChange}
                            onBlur={handleBlur}
                            rows={3}
                            placeholder="Desciption of the expense">
                        </textarea>
                        {touched.note && <div className="invalid-feedback">
                            {errors.note}
                        </div>}
                    </div>
                    <div className="mb-3">
                        <label htmlFor="amount" className="form-label">Amount</label>
                        <input type="number"
                               id="amount"
                               className={`form-control ${errors.amount && touched.amount ? 'is-invalid' : ''}`}
                               value={values.amount}
                               onChange={handleChange}
                               onBlur={handleBlur}
                               placeholder="Expense amount"/>
                        {touched.amount && <div className="invalid-feedback">
                            {errors.amount}
                        </div>}
                    </div>

                    <CategorySelect
                        valueList={categoryOptions}
                        label={'Expense category'}
                        selectLabel={'Select category'}
                        value={values.category}
                        onChange={handleChange}
                        onBlur={handleBlur}
                        error={errors.category}
                        touched={touched.category}
                    />

                    <div className="mb-3">
                        <label htmlFor="date" className="form-label">Date</label>
                        <input type="date"
                               id="date"
                               className={`form-control ${errors.date && touched.date ? 'is-invalid' : ''}`}
                               value={values.date}
                               onChange={handleChange}
                               onBlur={handleBlur}
                               />
                        {touched.date && <div className="invalid-feedback">
                            {errors.date}
                        </div>}
                    </div>
                    <button type="reset" name={"reset"} className="btn btn-outline-danger me-2">Reset</button>
                    <button type="submit" name={"submit"} className="btn btn-outline-primary">Submit</button>
                </form>
            </div>
        </div>
    )
}