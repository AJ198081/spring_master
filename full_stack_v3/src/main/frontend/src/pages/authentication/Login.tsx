import {ReactNode, useContext} from "react";
import {useNavigate} from "react-router-dom";
import {
    CustomJwtPayload,
    initialUserLoginRequest, UserLoginRequest,
    UserLoginRequestSchemaValidation, UserLoginResponse,
} from "../../domain/Types.ts";
import {AxiosInstance} from "../../service/api-client.ts";
import toast from "react-hot-toast";
import {useFormik} from "formik";
import {Tooltip} from "@mantine/core";
import {GoQuestion} from "react-icons/go";
import {UserAuthenticationContext} from "../../contexts/UserAuthenticationContext.tsx";
import {jwtDecode} from "jwt-decode";

export const Login = (): ReactNode => {
    const navigateTo = useNavigate();
    const {setIsAuthenticated, setToken} = useContext(UserAuthenticationContext)

    const registerUser = async (values: UserLoginRequest) => {
        AxiosInstance.post('/api/v1/auth/login', values)
            .then(response => {
                console.log(JSON.stringify(response));
                let registrationData = response.data as UserLoginResponse;
                if (response.status === 200) {
                    localStorage.setItem('token', registrationData.token);
                    setToken(jwtDecode<CustomJwtPayload>(registrationData.token));
                    setIsAuthenticated(true);
                    navigateTo("/");
                } else if (response.status === 404 || response.status === 400){
                    console.log(`Errors ${response.data.detail}`);
                    toast.error('Unable to login. Please check your credentials and try again.', {
                        duration: 10000,
                    });
                    setErrors({ username: response.data.detail});
                    setToken(null);
                    resetForm();
                }
            })
            .catch(error => {
                toast.error(error.response.data.message);
                setIsAuthenticated(false);
            });
    };

    const {values, errors, setErrors, touched, handleChange, handleSubmit, resetForm, handleBlur} = useFormik({
        initialValues: initialUserLoginRequest,
        onSubmit: registerUser,
        validationSchema: UserLoginRequestSchemaValidation,
    });

    return (
        <div className={'d-flex justify-content-center align-items-center mt-3'}>
            <div className={'container col-md-4 col-sm-8 col-xs-12'}>
                <form className={'needs-validation'} noValidate={false}
                      onSubmit={handleSubmit}
                      onReset={() => resetForm()}
                      onBlur={handleBlur}>

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

                    <button type="reset" name={"reset"} className="btn btn-outline-danger me-2">Reset</button>
                    <button type="submit" name={"submit"} className="btn btn-outline-primary">Login</button>
                </form>
            </div>
        </div>
    );
}