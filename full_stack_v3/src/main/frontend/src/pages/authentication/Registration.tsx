import {ReactNode} from "react";
import {useNavigate} from "react-router-dom";
import {AxiosResponse} from "axios";
import {
    defaultUserRegistrationRequest,
    UserRegistrationRequest,
    UserRegistrationRequestSchemaValidations,
    UserRegistrationResponse
} from "../../domain/Types.ts";
import {useFormik} from "formik";
import {Tooltip} from "@mantine/core";
import {GoQuestion} from "react-icons/go";
import {AxiosInstance} from "../../service/api-client.ts";
import toast from "react-hot-toast";

export const Registration = (): ReactNode => {

    const navigateTo = useNavigate();

    const registerUser = (values: UserRegistrationRequest) => {
        setSubmitting(true);
        AxiosInstance.post('/api/v1/auth/register', values)
            .then((response: AxiosResponse<UserRegistrationResponse>) => {
                console.log(`Registration response: ${JSON.stringify(response.data)}`);
                const registrationData = response.data;
                toast.success(`Registration successful - User ID - ${registrationData.userId} assigned to ${registrationData.username}`, {
                    duration: 10000,
                });
            })
            .catch(error => {
                console.log(`Registration error: ${JSON.stringify(error)}`);
                toast.error(error.response.data.message);
            })
            .finally(() => {
                console.log('Registration finally');
                setSubmitting(false);
                navigateTo("/login");
            });
    };

    const {
        values,
        errors,
        touched,
        isSubmitting,
        setSubmitting,
        handleChange,
        handleSubmit,
        resetForm,
        handleBlur
    } = useFormik<UserRegistrationRequest>({
        initialValues: defaultUserRegistrationRequest,
        onSubmit: registerUser,
        validationSchema: UserRegistrationRequestSchemaValidations,
    });

    console.log(`isSubmitting: ${isSubmitting}`);

    return (
        <div className={'d-flex justify-content-center align-items-center mt-3'}>
            <div className={'container col-md-4 col-sm-8 col-xs-12'}>
                <form className={'needs-validation'} noValidate={false}
                      onSubmit={handleSubmit}
                      onReset={() => resetForm()}
                      onBlur={handleBlur}>

                    <div className="mb-3">
                        <label htmlFor="firstname" className="form-label">Firstname</label>
                        <input type="text"
                               id="firstname"
                               className={`form-control ${errors.firstname && touched.firstname ? 'is-invalid' : ''}`}
                               value={values.firstname}
                               onChange={handleChange}
                               onBlur={handleBlur}
                               placeholder="First name"/>
                        {<div className="invalid-feedback">
                            {errors.firstname}
                        </div>}
                    </div>

                    <div className="mb-3">
                        <label htmlFor="lastname" className="form-label">Lastname</label>
                        <input type="text"
                               id="lastname"
                               className={`form-control ${errors.lastname && touched.lastname ? 'is-invalid' : ''}`}
                               value={values.lastname}
                               onChange={handleChange}
                               onBlur={handleBlur}
                               placeholder="Last name"/>
                        {<div className="invalid-feedback">
                            {errors.lastname}
                        </div>}
                    </div>

                    <div className="mb-3">
                        <label htmlFor="email" className="form-label">Email</label>
                        <input
                            type={'email'}
                            id="email"
                            className={`form-control ${errors.email && touched.email ? 'is-invalid' : ''}`}
                            value={values.email}
                            onChange={handleChange}
                            onBlur={handleBlur}
                            placeholder="Your email address">
                        </input>
                        {<div className="invalid-feedback">
                            {errors.email}
                        </div>}
                    </div>

                    <div className="mb-3">
                        <label htmlFor="username" className="form-label">Username</label>
                        <input type="text"
                               id="username"
                               className={`form-control ${errors.username && touched.username ? 'is-invalid' : ''}`}
                               value={values.username}
                               onChange={handleChange}
                               onBlur={handleBlur}
                               autoComplete={'on'}
                               placeholder="Enter username"/>
                        {<div className="invalid-feedback">
                            {errors.username}
                        </div>}
                    </div>

                    <div className="mb-3">
                        <label htmlFor="password" className="form-label">Password</label>
                        <div className={'input-group'}>
                            <input type="password"
                                   id="password"
                                   name="password"
                                   className={`form-control ${errors.password && touched.password ? 'is-invalid' : ''}`}
                                   value={values.password}
                                   onChange={handleChange}
                                   onBlur={handleBlur}
                                   autoComplete={'on'}
                                   placeholder="Enter password"/>
                            <Tooltip
                                className={'input-group-text'}
                                label={'Must contain at least one uppercase, one lowercase, one number, one special character, and be at least eight characters long'}
                            >
                                <span>
                                    <GoQuestion/>
                                </span>
                            </Tooltip>
                        </div>
                        {
                            <div className="invalid-feedback d-block">
                                {errors.password}
                            </div>
                        }
                    </div>

                    <div className="mb-3">
                        <label htmlFor="confirmpassword" className="form-label">Confirm password</label>
                        <input type="password"
                               id="confirmpassword"
                               name="confirmpassword"
                               className={`form-control ${errors.confirmpassword && touched.confirmpassword ? 'is-invalid' : ''}`}
                               value={values.confirmpassword}
                               onChange={handleChange}
                               onBlur={handleBlur}
                               autoComplete={'on'}
                               placeholder="Confirm password"/>
                        {<div className="invalid-feedback">
                            {errors.confirmpassword}
                        </div>}
                    </div>

                    <button
                        type="reset"
                        name={"reset"}
                        className={`btn btn-outline-danger me-2 ${isSubmitting && 'disabled'}`}>
                        Reset
                    </button>
                    <button
                        type="submit"
                        name={"submit"}
                        className={`btn btn-outline-primary ${isSubmitting && 'disabled'}`}
                    >
                        Submit
                    </button>
                </form>
            </div>
        </div>
    );
};