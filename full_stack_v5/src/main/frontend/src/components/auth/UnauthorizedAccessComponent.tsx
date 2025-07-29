import { Alert, Button, Card, Col, Container, Row } from "react-bootstrap";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { FaExclamationTriangle, FaHome, FaArrowLeft } from "react-icons/fa";

export const UnauthorizedAccessComponent = () => {
    const location = useLocation();
    const navigate = useNavigate();

    const handleGoBack = () => {
        console.log(`Unauthorized access attempted from ${location.state?.from ?? location.pathname}`);
        navigate(-1);
    };

    return (
        <Container className="py-5">
            <Row className="justify-content-center">
                <Col md={8} lg={6}>
                    <Card className="border-0 shadow">
                        <Card.Header className="bg-danger text-white text-center py-3">
                            <FaExclamationTriangle size={30} className="me-2" />
                            <h4 className="d-inline-block mb-0">Unauthorized Access</h4>
                        </Card.Header>
                        <Card.Body className="p-4">
                            <Alert variant="danger">
                                <p className="mb-0">
                                    You don't have permission to access this resource. 
                                    This area requires higher privileges than your current user role provides.
                                </p>
                            </Alert>
                            
                            <p className="text-muted">
                                If you believe you should have access to this page, please contact your administrator
                                or try logging in with an account that has the required permissions.
                            </p>
                            
                            <div className="d-flex justify-content-between mt-4">
                                <Button 
                                    variant="outline-secondary" 
                                    onClick={handleGoBack}
                                    className="d-flex align-items-center"
                                >
                                    <FaArrowLeft className="me-2" /> Go Back
                                </Button>
                                
                                <Link to="/" className="btn btn-primary d-flex align-items-center">
                                    <FaHome className="me-2" /> Return Home
                                </Link>
                            </div>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </Container>
    );
}