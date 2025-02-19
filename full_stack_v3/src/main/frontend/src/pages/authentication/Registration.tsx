import {ReactNode} from "react";
import {useNavigate} from "react-router-dom";
import {
    initialUserRegistrationRequest,
    UserRegistrationRequest,
    UserRegistrationRequestSchemaValidations
} from "../../domain/Types.ts";
import {useFormik} from "formik";
import {Tooltip} from "@mantine/core";
import {GoQuestion} from "react-icons/go";

export const Registration = (): ReactNode => {

    const navigateTo = useNavigate();


    const registerUser = async (values: UserRegistrationRequest) => {
        console.log(values);
        navigateTo("/login");
    };

    const {values, errors, touched, handleChange, handleSubmit, resetForm, handleBlur} = useFormik({
        initialValues: initialUserRegistrationRequest,
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
                        {touched.firstname && <div className="invalid-feedback">
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
                        {touched.lastname && <div className="invalid-feedback">
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
                        {touched.email && <div className="invalid-feedback">
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
                               placeholder="Enter username"/>
                        {touched.username && <div className="invalid-feedback">
                            {errors.username}
                        </div>}
                    </div>

                    <div className="mb-3">
                        <label htmlFor="password" className="form-label">Password</label>

                        <div className={'input-group'}>
                            <input type="password"
                                   id="password"
                                   autoComplete={"current-password"}
                                   className={`form-control ${errors.password && touched.password ? 'is-invalid' : ''}`}
                                   value={values.password}
                                   onChange={handleChange}
                                   onBlur={handleBlur}
                                   placeholder="Enter password"/>

                            <Tooltip
                                className={'input-group-text'}
                                label={'Must container at least one uppercase, one lowercase, one number, one special and min 8 characters'}
                            >
                                <span>
                                <GoQuestion/>
                                </span>
                            </Tooltip>
                        </div>

                        {touched.password && <div className="invalid-feedback">
                            {errors.password}
                        </div>}
                    </div>

                    <div className="mb-3">
                        <label htmlFor="cpassword" className="form-label">Confirm password</label>
                        <input type="password"
                               id="cpassword"
                               autoComplete={"current-password"}
                               className={`form-control ${errors.confirmpassword && touched.confirmpassword ? 'is-invalid' : ''}`}
                               value={values.confirmpassword}
                               onChange={handleChange}
                               onBlur={handleBlur}
                               placeholder="Confirm password"/>
                        {touched.confirmpassword && <div className="invalid-feedback">
                            {errors.confirmpassword}
                        </div>}
                    </div>

                    <button type="reset" name={"reset"} className="btn btn-outline-danger me-2">Reset</button>
                    <button type="submit" name={"submit"} className="btn btn-outline-primary">Submit</button>
                </form>
            </div>
        </div>
    );
};