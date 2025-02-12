import {Fragment, useEffect, useState} from "react";
import {useForm} from "react-hook-form";
import {Link, useNavigate} from "react-router-dom";
import {AxiosInstance} from "../services/api";
import {jwtDecode, JwtPayload} from "jwt-decode";
import {InputField} from "./InputField";
import {FcGoogle} from "react-icons/fc";
import {FaGithub} from "react-icons/fa";
import Divider from "@mui/material/Divider";
import {Button} from "./Button";
import toast from "react-hot-toast";
import {useApiContext} from "../hooks/ApiContextHook.ts";

export const Login = () => {
    // Step 1: Login method and Step 2: Verify 2FA
    const [step, setStep] = useState(1);
    const [jwtToken, setJwtToken] = useState("");
    const [loading, setLoading] = useState(false);
    // Access the token and setToken function using the useMyContext hook from the ContextProvider
    const {setToken, token} = useApiContext();
    const navigate = useNavigate();

    //react hook form initialization
    const {
        register,
        handleSubmit,
        reset,
        formState: {errors},
    } = useForm({
        defaultValues: {
            username: "",
            password: "",
            code: "",
        },
        mode: "onTouched",
    });

    const handleSuccessfulLogin = (token: string, decodedToken: JwtPayload) => {
        console.log("DecodedToken: ", decodedToken);
        const user = {
            username: decodedToken.sub,
            roles: decodedToken.roles ? decodedToken.roles.split(",") : [],
        };
        localStorage.setItem("JWT_TOKEN", token);
        localStorage.setItem("USER", JSON.stringify(user));

        //store the token on the context state  so that it can be shared any where in our application by context provider
        setToken(token);

        navigate("/notes");
    };

    //function for handle login with credentials
    const onLoginHandler = async (data) => {
        try {
            setLoading(true);
            const response = await AxiosInstance.post("/auth/public/login", data);

            //showing success message with react hot toast
            toast.success("Login Successful");

            //reset the input field by using reset() function provided by react hook form after submission
            reset();

            if (response.status === 200 && response.data.jwtToken) {
                setJwtToken(response.data.jwtToken);
                const decodedToken = jwtDecode(response.data.jwtToken);
                if (decodedToken.is2faEnabled) {
                    setStep(2); // Move to 2FA verification step
                } else {
                    handleSuccessfulLogin(response.data.jwtToken, decodedToken);
                }
            } else {
                toast.error(
                    "Login failed. Please check your credentials and try again."
                );
            }
        } catch (error) {
            if (error) {
                toast.error("Invalid credentials");
            }
        } finally {
            setLoading(false);
        }
    };

    //function for verify 2fa authentication
    const onVerify2FaHandler = async (data) => {
        const code = data.code;
        setLoading(true);

        try {
            const formData = new URLSearchParams();
            formData.append("code", code);
            formData.append("jwtToken", jwtToken);

            await AxiosInstance.post("/auth/public/verify-2fa-login", formData, {
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded",
                },
            });

            const decodedToken = jwtDecode(jwtToken);
            handleSuccessfulLogin(jwtToken, decodedToken);
        } catch (error) {
            console.error("2FA verification error", error);
            toast.error("Invalid 2FA code. Please try again.");
        } finally {
            setLoading(false);
        }
    };

    //if there is token  exist navigate  the user to the home page if he tried to access the login page
    useEffect(() => {
        if (token) navigate("/");
    }, [navigate, token]);

    //step1 will render the login form and step-2 will render the 2fa verification form
    return (
        <div className="min-h-[calc(100vh-74px)] flex justify-center items-center">
            {step === 1 ? (
                <Fragment>
                    <form
                        onSubmit={handleSubmit(onLoginHandler)}
                        className="sm:w-[450px] w-[360px]  shadow-custom py-8 sm:px-8 px-4"
                    >
                        <div>
                            <h1 className="font-montserrat text-center font-bold text-2xl">
                                Login Here
                            </h1>
                            <p className="text-slate-600 text-center">
                                Please Enter your username and password{" "}
                            </p>
                            <div className="flex items-center justify-between gap-1 py-5 ">
                                <Link
                                    to={`${import.meta.env.VITE_FULL_STACK_V2_BASE_URL}/oauth2/authorization/google`}
                                    className="flex gap-1 items-center justify-center flex-1 border p-2 shadow-sm shadow-slate-200 rounded-md hover:bg-slate-300 transition-all duration-300"
                                >
                  <span>
                    <FcGoogle className="text-2xl"/>
                  </span>
                                    <span className="font-semibold sm:text-customText text-xs">
                    Login with Google
                  </span>
                                </Link>
                                <Link
                                    to={`${import.meta.env.VITE_FULL_STACK_V2_BASE_URL}/oauth2/authorization/github`}
                                    className="flex gap-1 items-center justify-center flex-1 border p-2 shadow-sm shadow-slate-200 rounded-md hover:bg-slate-300 transition-all duration-300"
                                >
                  <span>
                    <FaGithub className="text-2xl"/>
                  </span>
                                    <span className="font-semibold sm:text-customText text-xs">
                    Login with Github
                  </span>
                                </Link>
                            </div>

                            <Divider className="font-semibold">OR</Divider>
                        </div>

                        <div className="flex flex-col gap-2">
                            <InputField
                                label="UserName"
                                required
                                id="username"
                                type="text"
                                message="*UserName is required"
                                placeholder="type your username"
                                register={register}
                                errors={errors}
                            />{" "}
                            <InputField
                                label="Password"
                                required
                                id="password"
                                type="password"
                                message="*Password is required"
                                placeholder="type your password"
                                register={register}
                                errors={errors}
                            />
                        </div>
                        <Button
                            disabled={loading}
                            onClickHandler={() => {console.log("Login clicked")}}
                            className="bg-amber-700 font-semibold text-white w-full py-2 hover:text-slate-400 transition-colors duration-100 rounded-sm my-3"
                            type="submit"
                        >
                            {loading ? <span>Loading...</span> : "LogIn"}
                        </Button>
                        <p className=" text-sm text-slate-700 ">
                            <Link
                                className=" underline hover:text-black"
                                to="/forgot-password"
                            >
                                Forgot Password?
                            </Link>
                        </p>

                        <p className="text-center text-sm text-slate-700 mt-6">
                            Don't have an account?{" "}
                            <Link
                                className="font-semibold underline hover:text-black"
                                to="/signup"
                            >
                                SignUp
                            </Link>
                        </p>
                    </form>
                </Fragment>
            ) : (
                <Fragment>
                    <form
                        onSubmit={handleSubmit(onVerify2FaHandler)}
                        className="sm:w-[450px] w-[360px]  shadow-custom py-8 sm:px-8 px-4"
                    >
                        <div>
                            <h1 className="font-montserrat text-center font-bold text-2xl">
                                Verify 2FA
                            </h1>
                            <p className="text-slate-600 text-center">
                                Enter the correct code to complete 2FA Authentication
                            </p>

                            <Divider className="font-semibold pb-4"></Divider>
                        </div>

                        <div className="flex flex-col gap-2 mt-4">
                            <InputField
                                label="Enter Code"
                                required
                                id="code"
                                type="text"
                                message="*Code is required"
                                placeholder="Enter your 2FA code"
                                register={register}
                                errors={errors}
                            />
                        </div>
                        <Button
                            disabled={loading}
                            className="bg-customRed font-semibold text-white w-full py-2 hover:text-slate-400 transition-colors duration-100 rounded-sm my-3"
                            type="button"
                        >
                            {loading ? <span>Loading...</span> : "Verify 2FA"}
                        </Button>
                    </form>
                </Fragment>
            )}
        </div>
    );
};

