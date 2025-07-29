import {type FormEvent, useState} from "react";
import {type LoginRequestDto, loginUser} from "../../services/LoginService.ts";
import {useLocation, useNavigate} from "react-router-dom";
import {toast} from "react-toastify";
import {Button, Card, Col, Container, Form, Row} from "react-bootstrap";
import {type Authentication, useAuthStore} from "../../store/AuthStore.ts";
import {isJwtValid, parseJwt} from "../../services/JwtUtil.ts";

export const LoginComponent = () => {

    const [credentials, setCredentials] = useState<LoginRequestDto>({
        username: '',
        password: ''
    });

    const setAuthentication = useAuthStore(state => state.setAuthState);

    const navigateTo = useNavigate();
    const {pathname, state} = useLocation();

    const handleLogin = (e: FormEvent<HTMLFormElement>) => {

        e.preventDefault();

        loginUser(credentials)
            .then(response => {

                const jwtClaims = parseJwt(response);

                if (jwtClaims) {
                    const authenticationObject: Authentication = {
                        isAuthenticated: isJwtValid(response),
                        token: response,
                        username: jwtClaims.sub,
                        roles: jwtClaims.roles
                    }

                    setAuthentication(authenticationObject);
                    
                    toast.success(`Welcome ${authenticationObject.username}!`);

                    navigateTo(state?.from || '/', {
                        replace: true,
                        state: {from: pathname}
                    });

                } else {
                    toast.error(`Error logging the user - ${response}`);
                }
            })
            .catch(error => {
                console.log(error);
                toast.error(`Error logging the user ${error.response?.data?.error} - ${error.response?.data?.message}`);
            });

    }

    return (
        <Container
            className="mt-5 w-50"
            fluid="md"
        >
            <Row>
                <Col>
                    <Card>
                        <Card.Header className={`text-bg-dark`}>Login</Card.Header>
                        <Card.Body className={`mt-3`}>
                            <Form
                                onSubmit={handleLogin}
                                onReset={() => setCredentials({username: '', password: ''})}
                            >
                                <Form.Group
                                    className="mb-3"
                                    controlId="username"
                                >
                                    <Form.Label>Username</Form.Label>
                                    <Form.Control
                                        type="text"
                                        name="username"
                                        value={credentials.username}
                                        onChange={e => setCredentials({...credentials, username: e.target.value})}
                                        placeholder="Enter username"
                                        required
                                        minLength={3}
                                        autoComplete="off"
                                        autoFocus
                                    />
                                    <Form.Control.Feedback type="invalid">
                                        Please provide a valid username (minimum 3 characters).
                                    </Form.Control.Feedback>
                                </Form.Group>

                                <Form.Group
                                    className="mb-3"
                                    controlId="password"
                                >
                                    <Form.Label>Password</Form.Label>
                                    <Form.Control
                                        type="password"
                                        name="password"
                                        value={credentials.password}
                                        onChange={e => setCredentials({...credentials, password: e.target.value})}
                                        placeholder="Enter password"
                                        autoComplete="off"
                                    />
                                    <Form.Control.Feedback type="invalid">
                                        Please provide a valid password.
                                    </Form.Control.Feedback>
                                </Form.Group>
                                <Form.Group className="my-5 d-flex justify-content-center gap-3">
                                    <Button
                                        variant="outline-secondary"
                                        className={'w-25'}
                                        type="reset"
                                    >Reset</Button>
                                    <Button
                                        variant="primary"
                                        className={'w-25'}
                                        type="submit"
                                    >Login</Button>
                                </Form.Group>

                                <Form.Group className="mt-5 gap-3 d-flex justify-content-around">
                                    <Form.Text className="text-muted">
                                        Don't have an account? <a href="/register">Register</a>
                                    </Form.Text>
                                    <Form.Text className="text-muted">
                                        Forgot password? <a href="/reset-password">Reset password</a>
                                    </Form.Text>
                                </Form.Group>
                            </Form>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </Container>
    )
}