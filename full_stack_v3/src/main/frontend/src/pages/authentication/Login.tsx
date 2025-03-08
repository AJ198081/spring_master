import {ReactNode, useContext} from "react";
import {useNavigate} from "react-router-dom";
import {
    initialUserLoginRequest,
    UserLoginRequest,
    UserLoginRequestSchemaValidation,
    UserLoginResponse,
} from "../../domain/Types.ts";
import {AxiosInstance} from "../../service/api-client.ts";
import {AxiosError, AxiosResponse} from "axios";
import toast from "react-hot-toast";
import {useFormik} from "formik";
import {Tooltip} from "@mantine/core";
import {GoQuestion} from "react-icons/go";
import {UserAuthenticationContext} from "../../contexts/UserAuthenticationContext.tsx";
import { FcGoogle } from "react-icons/fc";


export const Login = (): ReactNode => {
    const navigateTo = useNavigate();
    const {setToken} = useContext(UserAuthenticationContext)

    const loginUser = async (values: UserLoginRequest) => {

        AxiosInstance.post('/api/v1/auth/login', values)
            .then((response: AxiosResponse<UserLoginResponse>) => {
                const loginResponse = response.data;
                if (response.status === 200) {
                    setToken(loginResponse.token);
                    toast.success('Login successful');
                    navigateTo("/");
                }
            })
            .catch(error => {
                if (error instanceof AxiosError) {
                    toast.error((error as AxiosError).message, {
                        duration: 5000,
                    });
                }
            });
    };

    const {values, errors, touched, handleChange, handleSubmit, resetForm, handleBlur} = useFormik({
        initialValues: initialUserLoginRequest,
        onSubmit: loginUser,
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
                        <label htmlFor="username" className="form-label">Username or email</label>
                        <input type="text"
                               id="username"
                               className={`form-control ${errors.username && touched.username ? 'is-invalid' : ''}`}
                               value={values.username}
                               onChange={handleChange}
                               onBlur={handleBlur}
                               autoComplete={'on'}
                               placeholder="Enter your username or email"/>
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

                    <button type="button" className="btn btn-outline-success d-flex mt-2">
                        <FcGoogle className="me-1" style={{width: '20px', height: '20px'}} />
                        Login with Google
                    </button>
                </form>
            </div>
        </div>
    );
}