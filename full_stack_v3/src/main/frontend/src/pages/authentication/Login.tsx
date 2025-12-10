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
import {Button, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle, Stack} from "@mui/material";
import {GoQuestion} from "react-icons/go";
import {UserAuthenticationContext} from "../../contexts/UserAuthenticationContext.tsx";
import {FcGoogle} from "react-icons/fc";


export const Login = (): ReactNode => {

    const navigateTo = useNavigate();
    const [dialogOpen, setDialogOpen] = useState<boolean>(false);
    const [abortController, setAbortController] = useState<AbortController | null>(null);
    const {setToken} = useContext(UserAuthenticationContext);

    const openDialog = () => setDialogOpen(true);
    const closeDialog = () => setDialogOpen(false);

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
                        <button
                            className={`btn btn-outline-danger m-2`}
                            onClick={openDialog}
                        >
                            Cancel Login?
                        </button>
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
                        errorMessage = error.message;
                    }

                    toast.error(errorMessage, {
                        duration: 5000,
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

    return (
        <>
            <Dialog
                open={dialogOpen}
                onClose={closeDialog}
                aria-labelledby="alert-dialog-title"
                aria-describedby="alert-dialog-description"
            >
                <DialogTitle id="alert-dialog-title">
                    {"Cancel Login Request?"}
                </DialogTitle>
                <DialogContent>
                    <DialogContentText id="alert-dialog-description">
                        Are you sure you want to cancel the ongoing login request?
                    </DialogContentText>
                </DialogContent>
                <DialogActions>
                    <Button
                        variant={"outlined"}
                        color={"info"}
                        onClick={closeDialog}
                    >No</Button>
                    <Button
                        variant={"contained"}
                        color={"error"}
                        onClick={() => {
                            if (abortController) {
                                abortController.abort('Login attempt canceled by the user');
                                setSubmitting(false);
                            }
                            closeDialog();
                        }}
                        autoFocus
                    >
                        Yes, Cancel
                    </Button>
                </DialogActions>
            </Dialog>

            <div className={'d-flex justify-content-center align-items-center mt-3'}>
                <div className={'container col-md-4 col-sm-8 col-xs-12'}>
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
                            >Username or email</label>
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
                            </div>
                            {touched.password && <div className="invalid-feedback">
                                {errors.password}
                            </div>}
                        </div>

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
                                color={"info"}
                                name={"submit"}
                                disabled={isSubmitting}
                            >
                                Login
                            </Button>

                        </Stack>


                        <button
                            type="button"
                            className={`btn btn-outline-success d-flex mt-2 ${isSubmitting && 'disabled'}`}
                        >
                            <FcGoogle
                                className="me-1"
                                style={{width: '20px', height: '20px'}}
                            />
                            Login with Google
                        </button>
                    </form>
                </div>
            </div>
        </>
    );
}