import {useEffect, useState} from 'react';
import {Link, useLocation, useNavigate} from 'react-router-dom';
import {Button, Card, Col, Container, Row} from 'react-bootstrap';
import {FaArrowLeft, FaExclamationTriangle, FaHome} from 'react-icons/fa';
import dayjs from "dayjs";
import localizedFormat from "dayjs/plugin/localizedFormat";

export interface ErrorInfo {
    title: string;
    message: string;
    status?: number;
    details?: string;
    timestamp: string;
}

export const ErrorPage = () => {
    const location = useLocation();
    const navigate = useNavigate();
    const [error, setError] = useState<ErrorInfo>({
        title: 'An Error Occurred',
        message: 'Something went wrong while processing your request.',
        status: 500,
        details: 'No additional details are available',
        timestamp: new Date().toLocaleString()
    });

    useEffect(() => {

        if (location.state?.error) {
            setError({
                ...error,
                ...location.state.error,
                timestamp: new Date().toLocaleString()
            });
        }

        // Log the error for debugging purposes
        console.error('Error occurred:', error);
    }, [error, location.state]);

    dayjs.extend(localizedFormat);

    const goBack = () => {
        navigate(-1);
    };

    return (
        <Container className="mt-5">
            <Row className="justify-content-center">
                <Col md={8}>
                    <Card className="shadow">
                        <Card.Header className="bg-danger text-white">
                            <h3 className="mb-0 d-flex align-items-center">
                                <FaExclamationTriangle className="me-2"/>
                                {error.title} {error.status && `(${error.status})`}
                            </h3>
                        </Card.Header>
                        <Card.Body className="py-5">
                            <div className="text-center mb-4">
                                <FaExclamationTriangle
                                    size={70}
                                    className="text-danger mb-3"
                                />
                                <h4>{error.message}</h4>
                                {error.details && error.details !== 'No additional details available' && (
                                    <p className="mt-3 text-muted">{error.details}</p>
                                )}
                                <p className="text-muted mt-3">
                                    <small>Timestamp: {dayjs(error.timestamp).format('LL LTS')}</small>
                                </p>
                            </div>

                            <div className="mt-5 d-flex justify-content-center gap-3">
                                <Button
                                    variant="outline-secondary"
                                    onClick={goBack}
                                >
                                    <FaArrowLeft/>
                                    Go Back
                                </Button>
                                <Link
                                    to="/"
                                    className="btn btn-primary"
                                >
                                    <FaHome/>
                                    Return to Home
                                </Link>
                            </div>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </Container>
    );
};