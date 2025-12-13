import {ReactNode} from "react";
import {useLocation, useNavigate} from "react-router-dom";
import {AxiosError, AxiosResponse} from "axios";
import {
    defaultUserRegistrationRequest,
    ProblemDetail,
    UserRegistrationRequest,
    UserRegistrationRequestSchemaValidations,
    UserRegistrationResponse
} from "../../domain/Types.ts";
import {useFormik} from "formik";
import {Tooltip} from "@mantine/core";
import {GoQuestion} from "react-icons/go";
import toast from "react-hot-toast";
import {registerNewUser} from "../../service/userClient.ts";

export const Registration = (): ReactNode => {

    const navigateTo = useNavigate();
    const location = useLocation();

    const registerUser = (values: UserRegistrationRequest) => {

        const abortController = new AbortController();

        const registrationResponsePromise = registerNewUser(values, abortController);

        void toast.promise(
            registrationResponsePromise,
            {
                loading: (
                    <div>
                        <div className={`d-flex justify-content-between`}>
                            Registration in progress...
                        </div>

                        <div>
                            <button
                                className={`btn btn-outline-danger m-2`}
                                onClick={() => {
                                    abortController.abort('Registration canceled by the user');
                                    setSubmitting(false);
                                }}
                            >
                                Cancel Registration?
                            </button>
                        </div>
                    </div>)
            }
        );

        registrationResponsePromise
            .then((response: AxiosResponse<UserRegistrationResponse>) => {
                const registrationData = response.data;
                toast.success(`Registration successful - User ID - ${registrationData.userId} assigned to ${registrationData.username}`, {
                    duration: 10000,
                });
            })
            .catch(error => {
                console.log('Registration error', error);
                if (error instanceof AxiosError) {
                    toast.error(((error as AxiosError).response?.data as ProblemDetail).detail || 'Registration failed', {
                        duration: 5000,
                    });
                } else {
                    toast.error(error.response.data.message);
                }
            })
            .finally(() => {
                navigateTo("/login");
            });
    };

    const calculateInitialRegistrationValues = () => {
        return location.state?.from === '/login'
            ? {
                username: location.state.username,
                password: location.state.password
            } as UserRegistrationRequest
            : defaultUserRegistrationRequest;
    }

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
        initialValues: calculateInitialRegistrationValues(),
        onSubmit: registerUser,
        validationSchema: UserRegistrationRequestSchemaValidations,
    });

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