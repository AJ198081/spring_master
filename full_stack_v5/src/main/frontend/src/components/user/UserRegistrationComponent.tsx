import {type ChangeEvent, type FormEvent, useState} from "react";
import type {UserRegistrationDto} from "../../types/User.ts";
import {registerNewUser} from "../../services/UserService.ts";
import {useProductStore} from "../../store/ProductStore.tsx";
import {toast} from "react-toastify";
import {Button, Card, Col, Form, Row, Spinner} from "react-bootstrap";
import {Link} from "react-router-dom";

const initialState = {
    username: '',
    password: '',
    roles: new Set<string>()
};

export const UserRegistrationComponent = () => {
    const [userRegistration, setUserRegistration] = useState<UserRegistrationDto>(initialState);

    const [validated, setValidated] = useState(false);
    const setCurrentUser = useProductStore(state => state.setCurrentUser);
    const currentUser = useProductStore(state => state.currentUser);
    const [isLoading, setIsLoading] = useState(false);

    const handleInputChange = (e: ChangeEvent<HTMLInputElement>) => {
        const {name, value} = e.target;
        setUserRegistration(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleRoleChange = (e: ChangeEvent<HTMLInputElement>) => {
        const {checked, value} = e.target;

        const updatedRoles = userRegistration.roles;
        if (checked) {
            updatedRoles.add(value);
        } else {
            updatedRoles.delete(value);
        }

        setUserRegistration(prev => ({
            ...prev,
            roles: updatedRoles
        }));
    };

    const onSubmitHandler = (e: FormEvent<HTMLFormElement>) => {
        const form = e.currentTarget;
        e.preventDefault();

        if (!form.checkValidity()) {
            e.stopPropagation();
            setValidated(true);
            return;
        }

        setValidated(true);
        setIsLoading(true);

        registerNewUser(userRegistration)
            .then(response => {
                setCurrentUser(response);
                toast.success(`Congratulations!! You have successfully registered username ${response.username}!`);
                form.reset();
            })
            .catch(error => {
                toast.error(`Error registering user: ${error.response?.data?.detail ?? error.message}`);
            })
            .finally(() => {
                setIsLoading(false);
            });
    }

    const handleFormReset = () => {
        setUserRegistration(initialState);
    };

    return (
        <div className={"container mt-5"}>
            <Row className="justify-content-center">
                <Col
                    md={8}
                    lg={6}
                >
                    <Card className="shadow">
                        <Card.Header className="bg-primary text-white">
                            <h3 className="mb-0">Register a new User</h3>
                        </Card.Header>
                        <Card.Body>
                            <Form
                                noValidate
                                validated={validated}
                                onSubmit={onSubmitHandler}
                            >
                                <Form.Group
                                    className="mb-3"
                                    controlId="username"
                                >
                                    <Form.Label>Username</Form.Label>
                                    <Form.Control
                                        type="text"
                                        name="username"
                                        value={userRegistration.username}
                                        onChange={handleInputChange}
                                        placeholder="Enter username"
                                        required
                                        minLength={3}
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
                                        value={userRegistration.password}
                                        onChange={handleInputChange}
                                        placeholder="Enter password"
                                        required
                                        minLength={6}
                                    />
                                    <Form.Control.Feedback type="invalid">
                                        Please provide a valid password (minimum 6 characters).
                                    </Form.Control.Feedback>
                                </Form.Group>

                                <Form.Group className="mb-4">
                                    <Form.Label>Roles</Form.Label>
                                    <div>
                                        <Form.Check
                                            inline
                                            type="checkbox"
                                            id="role-user"
                                            label="User"
                                            value="USER"
                                            checked={userRegistration.roles.has('USER')}
                                            onChange={handleRoleChange}
                                        />
                                        <Form.Check
                                            inline
                                            type="checkbox"
                                            id="role-admin"
                                            label="Admin"
                                            value="ADMIN"
                                            checked={userRegistration.roles.has('ADMIN')}
                                            onChange={handleRoleChange}
                                        />
                                    </div>
                                </Form.Group>

                                <div className={'d-flex justify-content-between gap-2'}>
                                    {currentUser === null
                                        ? <>
                                            <Button
                                                variant="outline-secondary"
                                                type="reset"
                                                className="w-50"
                                                onClick={handleFormReset}
                                            >
                                                Reset
                                            </Button>
                                            <Button
                                                variant="success"
                                                type="submit"
                                                className="w-50"
                                                disabled={isLoading}
                                            >
                                                {isLoading ? (
                                                    <>
                                                        <Spinner
                                                            as="span"
                                                            animation="border"
                                                            size="sm"
                                                            role="output"
                                                            aria-hidden="true"
                                                            className="me-2"
                                                        />
                                                        Registering...
                                                    </>
                                                ) : (
                                                    'Register'
                                                )}
                                            </Button>
                                        </>
                                        : <>
                                            <Link
                                                to="/login"
                                                className="btn btn-outline-secondary w-50"
                                            >Go to Login Page</Link>
                                            <Link
                                                to="/add-customer"
                                                className="btn btn-success w-50"
                                            >
                                                Complete profile
                                            </Link>
                                        </>
                                    }
                                </div>
                            </Form>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </div>
    )
}
