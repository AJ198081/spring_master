import {ReactNode, useContext, useState} from "react";
import {useNavigate} from "react-router-dom";
import {
    initialUserLoginRequest,
    UserLoginRequest,
    UserLoginRequestSchemaValidation,
    UserLoginResponse,
} from "../../domain/Types.ts";
import {AxiosInstance} from "../../service/api-client.ts";
import {AxiosError, AxiosResponse, CanceledError} from "axios";
import toast from "react-hot-toast";
import {useFormik} from "formik";
import {Tooltip} from "@mantine/core";
import {Button, Link, Paper, Stack, Typography} from "@mui/material";
import {GoQuestion} from "react-icons/go";
import {UserAuthenticationContext} from "../../contexts/user/UserAuthenticationContext.tsx";
import {FcGoogle} from "react-icons/fc";
import {ConfirmLoginCancelDialog} from "./dialogs/ConfirmLoginCancelDialog.tsx";
import {isProblemDetail} from "../../utils/Utils.ts";

export const Login = (): ReactNode => {

    const navigateTo = useNavigate();
    const [isCancelLoginDialogOpen, setIsCancelLoginDialogOpen] = useState<boolean>(false);
    const [abortController, setAbortController] = useState<AbortController | null>(null);
    const {setToken} = useContext(UserAuthenticationContext);

    const openDialog = () => setIsCancelLoginDialogOpen(true);
    const closeDialog = () => setIsCancelLoginDialogOpen(false);

    const loginUser = async (values: UserLoginRequest) => {

        const controller = new AbortController();
        setAbortController(controller);

        const loginResponsePromise = AxiosInstance.post('/api/v1/auth/login', values, {
            signal: controller.signal
        });

        void toast.promise(loginResponsePromise, {
            loading: (
                <div>
                    <div className={`d-flex justify-content-between`}>
                        Login request in progress...
                    </div>

                    <div>
                        <Button
                            variant={"contained"}
                            className={`mt-3`}
                            onClick={openDialog}
                            autoFocus={true}
                            color={"error"}
                        >
                            Cancel Login?
                        </Button>
                    </div>
                </div>)
        });

        // useFormik needs this await to properly set isSubmitting after login function completion, either 'await' or 'return' promise is required.
        await loginResponsePromise
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
                    let errorMessage;
                    if (error instanceof CanceledError) {
                        errorMessage = controller.signal.reason;
                    } else {
                        if (isProblemDetail(error)) {
                            errorMessage = <Stack
                                gap={2}
                                color={"black"}
                                sx={{
                                    minWidth: '300px'
                                }}
                            >
                                <div>
                                    {error.response!.data.detail + ` Find out more? `}
                                    <a
                                        href={error.response!.data.type}
                                        target={"_blank"}
                                    >
                                        <Typography
                                            color={"blue"}
                                            sx={{display: "inline"}}
                                        >
                                            here
                                        </Typography>
                                    </a>
                                </div>
                                <div className={"d-flex justify-content-center"}>
                                    <Link
                                        sx={{cursor: 'pointer'}}
                                        onClick={() => {
                                            toast.dismiss('login-error')
                                            navigateTo('/register', {
                                                state: {
                                                    from: '/login',
                                                    username: values.username,
                                                    password: values.password
                                                }
                                            });
                                        }}
                                    >
                                        Register new user?
                                    </Link>
                                </div>
                            </Stack>
                        } else {
                            errorMessage = error.message;
                        }
                    }

                    toast.error(errorMessage, {
                        id: 'login-error',
                        duration: 3000,
                        style: {
                            // background: 'lightgray',
                            color: '#fff'
                        }
                    });
                }
            });
    };

    const {
        values,
        errors,
        touched,
        handleChange,
        handleSubmit,
        isSubmitting,
        setSubmitting,
        resetForm,
        handleBlur
    } = useFormik({
        initialValues: initialUserLoginRequest,
        onSubmit: loginUser,
        validationSchema: UserLoginRequestSchemaValidation,
    });

    const handleLoginWithGoogle = () => {
        toast.error('Login with Google is not yet implemented', {
            id: 'login-with-google-not-yet-implemented',
            duration: 5000
        });
    };
    return (
        <>
            {ConfirmLoginCancelDialog(isCancelLoginDialogOpen, closeDialog, abortController, setSubmitting)}

            <Paper
                elevation={5}
                square={false}
                sx={
                    {
                        minWidth: 500,
                        minHeight: 320,
                        mx: 'auto',
                        padding: 3,
                        position: 'absolute',
                        top: 'max(20.33%, 60px)',
                        left: '50%',
                        transform: 'translate(-50%, 0)',
                        marginTop: '55px'
                    }
                }
            >
                <form
                    className={'needs-validation'}
                    noValidate={false}
                    onSubmit={handleSubmit}
                    onReset={() => resetForm()}
                    onBlur={handleBlur}
                >

                    <div className="mb-3">
                        <label
                            htmlFor="username"
                            className="form-label"
                        >
                            <Typography>
                                Username or email
                            </Typography>
                        </label>
                        <input
                            type="text"
                            id="username"
                            className={`form-control ${errors.username && touched.username ? 'is-invalid' : ''}`}
                            value={values.username}
                            onChange={handleChange}
                            onBlur={handleBlur}
                            autoComplete={'on'}
                            placeholder="Enter your username or email"
                        />
                        {touched.username && <div className="invalid-feedback">
                            {errors.username}
                        </div>}
                    </div>

                    <div className="mb-3">
                        <label
                            htmlFor="password"
                            className="form-label"
                        >Password</label>
                        <div className={'input-group'}>
                            <input
                                type="password"
                                id="password"
                                autoComplete={"current-password"}
                                className={`form-control ${errors.password && touched.password ? 'is-invalid' : ''}`}
                                value={values.password}
                                onChange={handleChange}
                                onBlur={handleBlur}
                                placeholder="Enter password"
                            />
                            <Tooltip
                                className={'input-group-text'}
                                label={'Must container at least one uppercase, one lowercase, one number, one special and min 8 characters'}
                            >
                                    <span>
                                    <GoQuestion/>
                                    </span>
                            </Tooltip>
                            {touched.password && <div className="invalid-feedback">
                                {errors.password}
                            </div>}
                        </div>
                    </div>
                    <div className={"container mt-4 mx-auto d-flex flex-column align-items-center"}>
                        <Stack
                            spacing={1}
                            direction={"row"}
                        >

                            <Button
                                variant={"contained"}
                                type="reset"
                                name={"reset"}
                                color={"warning"}
                                className={"me-2"}
                                disabled={isSubmitting}
                            >
                                Reset
                            </Button>

                            <Button
                                variant={"contained"}
                                type={"submit"}
                                color={"violet"}
                                name={"submit"}
                                disabled={isSubmitting}
                            >
                                Login
                            </Button>
                        </Stack>

                        <button
                            type="button"
                            className={`btn btn-outline-success d-flex mt-3 ${isSubmitting && 'disabled'}`}
                            onClick={handleLoginWithGoogle}
                        >
                            <FcGoogle
                                className="me-1"
                                style={{width: '20px', height: '20px'}}
                            />
                            Login with Google
                        </button>
                    </div>
                </form>
            </Paper>
        </>
    );
}