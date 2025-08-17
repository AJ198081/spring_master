import {type ChangeEvent, type FormEvent, useState} from "react";
import type {CustomerType} from "../../types/CustomerType.ts";
import {addCustomer} from "../../services/CustomerService.ts";
import {useProductStore} from "../../store/ProductStore.ts";
import {toast} from "react-toastify";
import {Button, Card, Col, Form, Row, Spinner} from "react-bootstrap";
import {useLocation, useNavigate} from "react-router-dom";
import {useAuthStore} from "../../store/AuthStore.ts";

const initialCustomerState: CustomerType = {
    firstName: "",
    lastName: "",
    email: "",
    phone: "",
    billingAddress: {
        addressLine1: "",
        addressLine2: "",
        city: "",
        state: "NSW",
        postalCode: "",
        country: "Australia"
    },
    shippingAddress: {
        addressLine1: "",
        addressLine2: "",
        city: "",
        state: "NSW",
        postalCode: "",
        country: "Australia"
    }
};

export const CreateCustomerComponent = () => {
    const [customer, setCustomer] = useState<CustomerType>(initialCustomerState);
    const [isSubmitting, setIsSubmitting] = useState<boolean>(false);
    const [validated, setValidated] = useState(false);
    const [shippingSameAsBilling, setShippingSameAsBilling] = useState<boolean>(true);
    const setThisCustomer = useProductStore(state => state.setThisCustomer);
    const patchAuthenticationState = useAuthStore(state => state.patchAuthState);
    const authState = useAuthStore(state => state.setAuthState);
    const currentUser = useAuthStore(state => state.authState);

    const navigateTo = useNavigate();
    const location = useLocation();

    const handleInputChange = (e: ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
        const {name, value} = e.target;

        if (name.includes('.')) {
            const [addressType, field] = name.split('.');

            setCustomer(prev => ({
                ...prev,
                [addressType]: {
                    ...prev[addressType as keyof Pick<CustomerType, 'billingAddress' | 'shippingAddress'>],
                    [field]: value
                }
            }));
        } else {
            setCustomer(prev => ({
                ...prev,
                [name]: value
            }));
        }
    };

    const handleSameAddressChange = (e: ChangeEvent<HTMLInputElement>) => {
        const isSame = e.target.checked;
        setShippingSameAsBilling(isSame);

        if (isSame) {
            setCustomer(prev => ({
                ...prev,
                shippingAddress: prev.billingAddress
            }));
        } else {
            setCustomer(prev => ({
                ...prev,
                shippingAddress: initialCustomerState.shippingAddress
            }))
        }
    };

    const handleSubmit = (e: FormEvent<HTMLFormElement>) => {
        const form = e.currentTarget;
        e.preventDefault();

        if (!form.checkValidity()) {
            e.stopPropagation();
            setValidated(true);
            return;
        }

        setValidated(true);
        setIsSubmitting(true);

        customer.username = currentUser?.username;

        if (shippingSameAsBilling) {
            customer.shippingAddress = customer.billingAddress;
        }

        addCustomer(customer)
            .then(response => {
                setThisCustomer(response);
                patchAuthenticationState({customerId: response.id!})
                toast.success(`Customer ${response.firstName} ${response.lastName} created successfully`);
                setCustomer(initialCustomerState);
                setValidated(false);
                form.reset();
                navigateTo(location.state?.from ?? '/', {replace: true});
            })
            .catch(error => {
                toast.error(`Error creating customer; issue is - ${error.response?.data?.detail ?? error.message}`);
            })
            .finally(() => {
                setIsSubmitting(false);
            });
    }

    console.log('Auth State: ', JSON.stringify(authState));

    const handleFormReset = () => {
        setCustomer(initialCustomerState);
        setValidated(false);
        setShippingSameAsBilling(true);
    };

    return (
        <div className="container m-5">
            <Row className="justify-content-center">
                <Col
                    md={8}
                    lg={6}
                >
                    <Card className="shadow">
                        <Card.Header className="bg-primary text-white">
                            <h3 className="mb-0">Create Customer</h3>
                        </Card.Header>
                        <Card.Body>
                            <Form
                                noValidate
                                validated={validated}
                                onSubmit={handleSubmit}
                            >
                                <Form.Group
                                    className="mb-3"
                                    controlId="firstName"
                                >
                                    <Form.Label>First Name</Form.Label>
                                    <Form.Control
                                        type="text"
                                        name="firstName"
                                        value={customer.firstName}
                                        onChange={handleInputChange}
                                        placeholder="Enter first name"
                                        required
                                    />
                                    <Form.Control.Feedback type="invalid">
                                        Please provide a first name.
                                    </Form.Control.Feedback>
                                </Form.Group>

                                <Form.Group
                                    className="mb-3"
                                    controlId="lastName"
                                >
                                    <Form.Label>Last Name</Form.Label>
                                    <Form.Control
                                        type="text"
                                        name="lastName"
                                        value={customer.lastName}
                                        onChange={handleInputChange}
                                        placeholder="Enter last name"
                                        required
                                    />
                                    <Form.Control.Feedback type="invalid">
                                        Please provide a last name.
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
                                        value={customer.email}
                                        onChange={handleInputChange}
                                        placeholder="Enter email"
                                        required
                                    />
                                    <Form.Control.Feedback type="invalid">
                                        Please provide a valid email address.
                                    </Form.Control.Feedback>
                                </Form.Group>

                                <Form.Group
                                    className="mb-4"
                                    controlId="phone"
                                >
                                    <Form.Label>Phone</Form.Label>
                                    <Form.Control
                                        type="tel"
                                        name="phone"
                                        value={customer.phone}
                                        onChange={handleInputChange}
                                        placeholder="Enter phone number"
                                        required
                                    />
                                    <Form.Control.Feedback type="invalid">
                                        Please provide a phone number.
                                    </Form.Control.Feedback>
                                </Form.Group>

                                <h5 className="mt-4 mb-3">Billing Address</h5>
                                <Row>
                                    <Col md={12}>
                                        <Form.Group
                                            className="mb-3"
                                            controlId="billingAddressLine1"
                                        >
                                            <Form.Label>Address Line 1</Form.Label>
                                            <Form.Control
                                                type="text"
                                                name="billingAddress.addressLine1"
                                                value={customer.billingAddress.addressLine1}
                                                onChange={handleInputChange}
                                                placeholder="Enter address line 1"
                                                required
                                            />
                                            <Form.Control.Feedback type="invalid">
                                                Please provide address line 1.
                                            </Form.Control.Feedback>
                                        </Form.Group>
                                    </Col>
                                </Row>

                                <Row>
                                    <Col md={12}>
                                        <Form.Group
                                            className="mb-3"
                                            controlId="billingAddressLine2"
                                        >
                                            <Form.Label>Address Line 2</Form.Label>
                                            <Form.Control
                                                type="text"
                                                name="billingAddress.addressLine2"
                                                value={customer.billingAddress.addressLine2}
                                                onChange={handleInputChange}
                                                placeholder="Enter address line 2 (optional)"
                                            />
                                        </Form.Group>
                                    </Col>
                                </Row>

                                <Row>
                                    <Col md={6}>
                                        <Form.Group
                                            className="mb-3"
                                            controlId="billingCity"
                                        >
                                            <Form.Label>City</Form.Label>
                                            <Form.Control
                                                type="text"
                                                name="billingAddress.city"
                                                value={customer.billingAddress.city}
                                                onChange={handleInputChange}
                                                placeholder="Enter city"
                                                required
                                            />
                                            <Form.Control.Feedback type="invalid">
                                                Please provide a city.
                                            </Form.Control.Feedback>
                                        </Form.Group>
                                    </Col>
                                    <Col md={6}>
                                        <Form.Group
                                            className="mb-3"
                                            controlId="billingState"
                                        >
                                            <Form.Label>State</Form.Label>
                                            <Form.Select
                                                name="billingAddress.state"
                                                value={customer.billingAddress.state}
                                                onChange={handleInputChange}
                                                required
                                            >
                                                <option value="NSW">New South Wales</option>
                                                <option value="VIC">Victoria</option>
                                                <option value="QLD">Queensland</option>
                                                <option value="WA">Western Australia</option>
                                                <option value="SA">South Australia</option>
                                                <option value="TAS">Tasmania</option>
                                                <option value="ACT">Australian Capital Territory</option>
                                                <option value="NT">Northern Territory</option>
                                            </Form.Select>
                                            <Form.Control.Feedback type="invalid">
                                                Please select a state.
                                            </Form.Control.Feedback>
                                        </Form.Group>
                                    </Col>
                                </Row>

                                <Row>
                                    <Col md={6}>
                                        <Form.Group
                                            className="mb-3"
                                            controlId="billingPostalCode"
                                        >
                                            <Form.Label>Postal Code</Form.Label>
                                            <Form.Control
                                                type="text"
                                                name="billingAddress.postalCode"
                                                value={customer.billingAddress.postalCode}
                                                onChange={handleInputChange}
                                                placeholder="Enter postal code"
                                                required
                                            />
                                            <Form.Control.Feedback type="invalid">
                                                Please provide a postal code.
                                            </Form.Control.Feedback>
                                        </Form.Group>
                                    </Col>
                                    <Col md={6}>
                                        <Form.Group
                                            className="mb-3"
                                            controlId="billingCountry"
                                        >
                                            <Form.Label>Country</Form.Label>
                                            <Form.Control
                                                type="text"
                                                name="billingAddress.country"
                                                value={customer.billingAddress.country}
                                                onChange={handleInputChange}
                                                placeholder="Enter country"
                                                required
                                            />
                                            <Form.Control.Feedback type="invalid">
                                                Please provide a country.
                                            </Form.Control.Feedback>
                                        </Form.Group>
                                    </Col>
                                </Row>

                                <Form.Group className="mb-4">
                                    <Form.Check
                                        type="checkbox"
                                        id="shippingSameAsBilling"
                                        label="Shipping address is same as billing"
                                        checked={shippingSameAsBilling}
                                        onChange={handleSameAddressChange}
                                    />
                                </Form.Group>

                                {!shippingSameAsBilling && (
                                    <>
                                        <h5 className="mt-4 mb-3">Shipping Address</h5>
                                        <Row>
                                            <Col md={12}>
                                                <Form.Group
                                                    className="mb-3"
                                                    controlId="shippingAddressLine1"
                                                >
                                                    <Form.Label>Address Line 1</Form.Label>
                                                    <Form.Control
                                                        type="text"
                                                        name="shippingAddress.addressLine1"
                                                        value={customer.shippingAddress.addressLine1}
                                                        onChange={handleInputChange}
                                                        placeholder="Enter address line 1"
                                                        required
                                                    />
                                                    <Form.Control.Feedback type="invalid">
                                                        Please provide address line 1.
                                                    </Form.Control.Feedback>
                                                </Form.Group>
                                            </Col>
                                        </Row>

                                        <Row>
                                            <Col md={12}>
                                                <Form.Group
                                                    className="mb-3"
                                                    controlId="shippingAddressLine2"
                                                >
                                                    <Form.Label>Address Line 2</Form.Label>
                                                    <Form.Control
                                                        type="text"
                                                        name="shippingAddress.addressLine2"
                                                        value={customer.shippingAddress.addressLine2}
                                                        onChange={handleInputChange}
                                                        placeholder="Enter address line 2 (optional)"
                                                    />
                                                </Form.Group>
                                            </Col>
                                        </Row>

                                        <Row>
                                            <Col md={6}>
                                                <Form.Group
                                                    className="mb-3"
                                                    controlId="shippingCity"
                                                >
                                                    <Form.Label>City</Form.Label>
                                                    <Form.Control
                                                        type="text"
                                                        name="shippingAddress.city"
                                                        value={customer.shippingAddress.city}
                                                        onChange={handleInputChange}
                                                        placeholder="Enter city"
                                                        required
                                                    />
                                                    <Form.Control.Feedback type="invalid">
                                                        Please provide a city.
                                                    </Form.Control.Feedback>
                                                </Form.Group>
                                            </Col>
                                            <Col md={6}>
                                                <Form.Group
                                                    className="mb-3"
                                                    controlId="shippingState"
                                                >
                                                    <Form.Label>State</Form.Label>
                                                    <Form.Select
                                                        name="shippingAddress.state"
                                                        value={customer.shippingAddress.state}
                                                        onChange={handleInputChange}
                                                        required
                                                    >
                                                        <option value="NSW">New South Wales</option>
                                                        <option value="VIC">Victoria</option>
                                                        <option value="QLD">Queensland</option>
                                                        <option value="WA">Western Australia</option>
                                                        <option value="SA">South Australia</option>
                                                        <option value="TAS">Tasmania</option>
                                                        <option value="ACT">Australian Capital Territory</option>
                                                        <option value="NT">Northern Territory</option>
                                                    </Form.Select>
                                                    <Form.Control.Feedback type="invalid">
                                                        Please select a state.
                                                    </Form.Control.Feedback>
                                                </Form.Group>
                                            </Col>
                                        </Row>

                                        <Row>
                                            <Col md={6}>
                                                <Form.Group
                                                    className="mb-3"
                                                    controlId="shippingPostalCode"
                                                >
                                                    <Form.Label>Postal Code</Form.Label>
                                                    <Form.Control
                                                        type="text"
                                                        name="shippingAddress.postalCode"
                                                        value={customer.shippingAddress.postalCode}
                                                        onChange={handleInputChange}
                                                        placeholder="Enter postal code"
                                                        required
                                                    />
                                                    <Form.Control.Feedback type="invalid">
                                                        Please provide a postal code.
                                                    </Form.Control.Feedback>
                                                </Form.Group>
                                            </Col>
                                            <Col md={6}>
                                                <Form.Group
                                                    className="mb-3"
                                                    controlId="shippingCountry"
                                                >
                                                    <Form.Label>Country</Form.Label>
                                                    <Form.Control
                                                        type="text"
                                                        name="shippingAddress.country"
                                                        value={customer.shippingAddress.country}
                                                        onChange={handleInputChange}
                                                        placeholder="Enter country"
                                                        required
                                                    />
                                                    <Form.Control.Feedback type="invalid">
                                                        Please provide a country.
                                                    </Form.Control.Feedback>
                                                </Form.Group>
                                            </Col>
                                        </Row>
                                    </>
                                )}

                                <div className="d-flex justify-content-between gap-2 mt-4">
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
                                        disabled={isSubmitting}
                                    >
                                        {isSubmitting ? (
                                            <>
                                                <Spinner
                                                    as="span"
                                                    animation="border"
                                                    size="sm"
                                                    role="output"
                                                    aria-hidden="true"
                                                    className="me-2"
                                                />
                                                Creating...
                                            </>
                                        ) : (
                                            'Create Customer'
                                        )}
                                    </Button>
                                </div>
                            </Form>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </div>
    );
}
