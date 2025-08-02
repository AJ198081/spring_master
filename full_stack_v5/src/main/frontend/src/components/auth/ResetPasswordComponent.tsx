import {type FormEvent, useState} from "react";
import {resetPassword} from "../../services/UserService.ts";
import {toast} from "react-toastify";
import {useNavigate} from "react-router-dom";
import {Button, Card, Col, Container, Form, Row} from "react-bootstrap";

const MIN_USERNAME_LENGTH = 3;
const MIN_PASSWORD_LENGTH = 8;
const MIN_EMAIL_LENGTH = 5;

const errorCondition = {
    username: MIN_USERNAME_LENGTH,
    email: MIN_EMAIL_LENGTH,
    newPassword: MIN_PASSWORD_LENGTH
};

interface ErrorMessage {
    username: string | null,
    email: string | null,
    newPassword: string | null,
}

const errorMessage = {
    username: `Minimum username length is ${MIN_USERNAME_LENGTH}`,
    email: `Please enter a valid email address`,
    newPassword: `Minimum password length is ${MIN_PASSWORD_LENGTH}`
};

export const ResetPasswordComponent = () => {
    const [resetData, setResetData] = useState({
        username: '',
        email: '',
        newPassword: ''
    });

    const [inputError, setInputError] = useState<ErrorMessage>({
        username: null,
        email: null,
        newPassword: null
    });

    const navigateFunction = useNavigate();

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    const handleBlur = (field: 'username' | 'email' | 'newPassword') => {
        if (field === 'email' && emailRegex.test(resetData.email)) {
            setInputError(prevState => ({
                ...prevState,
                [field]: errorMessage[field]
            }));
        } else {
            setInputError(prevState => ({
                ...prevState,
                [field]: resetData[field].length < errorCondition[field] ? errorMessage[field] : null
            }));
        }
    };

    const handleSubmit = (e: FormEvent<HTMLFormElement>) => {
        e.preventDefault();

        resetPassword(resetData.username, resetData.email, resetData.newPassword)
            .then(response => {
                console.log(`Reset Password response ${response}`);
                toast.success(`Password reset successfully for user ${resetData.username}`);
                navigateFunction('/login', {replace: true});
            })
            .catch(error => {
                toast.error(`Error resetting password for user ${resetData.username}, error status ${error.response?.data?.status}`);
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
                        <Card.Header className={`text-bg-danger`}>Reset Password</Card.Header>
                        <Card.Body className={`mt-3`}>
                            <Form
                                onSubmit={handleSubmit}
                                onReset={() => setResetData({username: '', email: '', newPassword: ''})}
                            >
                                <Form.Group
                                    className="mb-3"
                                    controlId="username"
                                >
                                    <Form.Label>Username</Form.Label>
                                    <Form.Control
                                        type="text"
                                        name="username"
                                        value={resetData.username}
                                        onChange={e => setResetData({...resetData, username: e.target.value})}
                                        onBlur={() => handleBlur('username')}
                                        placeholder="Enter username"
                                        required
                                        minLength={MIN_USERNAME_LENGTH}
                                        autoComplete="off"
                                        autoFocus
                                        isInvalid={inputError.username !== null}
                                    />
                                    <Form.Control.Feedback type="invalid">
                                        {inputError.username}
                                    </Form.Control.Feedback>
                                </Form.Group>

                                <Form.Group
                                    className="mb-3"
                                    controlId="email"
                                >
                                    <Form.Label>Email</Form.Label>
                                    <Form.Control
                                        type="email"
                                        name="email"
                                        value={resetData.email}
                                        onChange={e => setResetData({...resetData, email: e.target.value})}
                                        onBlur={() => handleBlur('email')}
                                        placeholder="Enter email"
                                        required
                                        autoComplete="off"
                                        isInvalid={inputError.email !== null}
                                    />
                                    <Form.Control.Feedback type="invalid">
                                        {inputError.email}
                                    </Form.Control.Feedback>
                                </Form.Group>

                                <Form.Group
                                    className="mb-3"
                                    controlId="newPassword"
                                >
                                    <Form.Label>New Password</Form.Label>
                                    <Form.Control
                                        type="password"
                                        name="newPassword"
                                        value={resetData.newPassword}
                                        onChange={e => setResetData({...resetData, newPassword: e.target.value})}
                                        onBlur={() => handleBlur('newPassword')}
                                        placeholder="Enter new password"
                                        required
                                        minLength={MIN_PASSWORD_LENGTH}
                                        autoComplete="off"
                                        isInvalid={inputError.newPassword !== null}
                                    />
                                    <Form.Control.Feedback type="invalid">
                                        {inputError.newPassword}
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
                                    >Submit</Button>
                                </Form.Group>
                            </Form>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </Container>
    )
}