import {type ChangeEvent, type FormEvent, useEffect, useState} from "react";
import {useCustomerStore} from "../../store/CustomerStore.ts";
import {getCustomer, updateCustomer} from "../../services/CustomerService.ts";
import {useAuthStore} from "../../store/AuthStore.ts";
import {toast} from "react-toastify";
import {AxiosError} from "axios";
import {useLocation, useNavigate} from "react-router-dom";
import {Button, Card, Col, Form, Row, Spinner} from "react-bootstrap";
import type {CustomerType} from "../../types/CustomerType.ts";

export const UpdateCustomerProfileComponent = () => {
    const customer = useCustomerStore(state => state.customer);
    const setCustomer = useCustomerStore(state => state.setCustomer);
    const thisUser = useAuthStore(state => state.authState?.username);
    const navigateTo = useNavigate();
    const location = useLocation();

    const [customerForm, setCustomerForm] = useState<CustomerType | undefined>(undefined);
    const [isSubmitting, setIsSubmitting] = useState<boolean>(false);
    const [validated, setValidated] = useState(false);
    const [shippingSameAsBilling, setShippingSameAsBilling] = useState<boolean>(true);

    useEffect(() => {
        if (thisUser) {
            getCustomer(thisUser)
                .then(response => {
                    setCustomer(response);
                })
                .catch(error => {
                    if (error instanceof AxiosError) {
                        if (error.response?.status === 404) {
                            toast.error(`Customer with username ${thisUser} not found, redirecting to create profile.`);
                            navigateTo("/add-customer");
                        }
                    }
                    toast.error(error.message);
                });
        }
    }, [thisUser, setCustomer, navigateTo]);

    useEffect(() => {
        if (customer) {
            setCustomerForm(customer);
            const isSame = JSON.stringify(customer.billingAddress) === JSON.stringify(customer.shippingAddress);
            setShippingSameAsBilling(isSame);
        }
    }, [customer]);

    const handleInputChange = (e: ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
        if (!customerForm) return;
        
        const {name, value} = e.target;

        if (name.includes('.')) {
            const [addressType, field] = name.split('.');

            setCustomerForm(prev => ({
                ...prev!,
                [addressType]: {
                    ...prev![addressType as keyof Pick<CustomerType, 'billingAddress' | 'shippingAddress'>],
                    [field]: value
                }
            }));
        } else {
            setCustomerForm(prev => ({
                ...prev!,
                [name]: value
            }));
        }
    };

    const handleSameAddressChange = (e: ChangeEvent<HTMLInputElement>) => {
        if (!customerForm) return;
        
        const isSame = e.target.checked;
        setShippingSameAsBilling(isSame);

        if (isSame) {
            setCustomerForm(prev => ({
                ...prev!,
                shippingAddress: prev!.billingAddress
            }));
        }
    };

    const handleSubmit = (e: FormEvent<HTMLFormElement>) => {
        const form = e.currentTarget;
        e.preventDefault();

        if (!form.checkValidity() || !customerForm) {
            e.stopPropagation();
            setValidated(true);
            return;
        }

        setValidated(true);
        setIsSubmitting(true);

        customerForm.username = thisUser;

        if (shippingSameAsBilling) {
            customerForm.shippingAddress = customerForm.billingAddress;
        }

        updateCustomer(customerForm.id!, customerForm)
            .then(response => {
                setCustomer(response);
                toast.success(`Customer profile updated successfully`);
                navigateTo(location.state?.from ?? '/', {replace: true});
            })
            .catch(error => {
                toast.error(`Error updating customer; issue is - ${error.response?.data?.detail ?? error.message}`);
            })
            .finally(() => {
                setIsSubmitting(false);
            });
    }

    if (!customerForm) {
        return (
            <div className="d-flex justify-content-center align-items-center" style={{ height: '50vh' }}>
                <Spinner animation="border" role="status">
                    <span className="visually-hidden">Loading...</span>
                </Spinner>
            </div>
        );
    }

    return (
        <div className="container m-5">
            <Row className="justify-content-center">
                <Col md={8} lg={6}>
                    <Card className="shadow">
                        <Card.Header className="text-bg-danger">
                            <h3 className="mb-0">Update Customer Profile</h3>
                        </Card.Header>
                        <Card.Body>
                            <Form
                                noValidate
                                validated={validated}
                                onSubmit={handleSubmit}
                            >
                                <Form.Group className="mb-3" controlId="firstName">
                                    <Form.Label>First Name</Form.Label>
                                    <Form.Control
                                        type="text"
                                        name="firstName"
                                        value={customerForm.firstName}
                                        onChange={handleInputChange}
                                        placeholder="Enter first name"
                                        required
                                    />
                                    <Form.Control.Feedback type="invalid">
                                        Please provide a first name.
                                    </Form.Control.Feedback>
                                </Form.Group>

                                <Form.Group className="mb-3" controlId="lastName">
                                    <Form.Label>Last Name</Form.Label>
                                    <Form.Control
                                        type="text"
                                        name="lastName"
                                        value={customerForm.lastName}
                                        onChange={handleInputChange}
                                        placeholder="Enter last name"
                                        required
                                    />
                                    <Form.Control.Feedback type="invalid">
                                        Please provide a last name.
                                    </Form.Control.Feedback>
                                </Form.Group>

                                <Form.Group className="mb-3" controlId="email">
                                    <Form.Label>Email</Form.Label>
                                    <Form.Control
                                        type="email"
                                        name="email"
                                        value={customerForm.email}
                                        onChange={handleInputChange}
                                        placeholder="Enter email"
                                        required
                                    />
                                    <Form.Control.Feedback type="invalid">
                                        Please provide a valid email address.
                                    </Form.Control.Feedback>
                                </Form.Group>

                                <Form.Group className="mb-4" controlId="phone">
                                    <Form.Label>Phone</Form.Label>
                                    <Form.Control
                                        type="tel"
                                        name="phone"
                                        value={customerForm.phone}
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
                                        <Form.Group className="mb-3" controlId="billingAddressLine1">
                                            <Form.Label>Address Line 1</Form.Label>
                                            <Form.Control
                                                type="text"
                                                name="billingAddress.addressLine1"
                                                value={customerForm.billingAddress.addressLine1}
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
                                        <Form.Group className="mb-3" controlId="billingAddressLine2">
                                            <Form.Label>Address Line 2</Form.Label>
                                            <Form.Control
                                                type="text"
                                                name="billingAddress.addressLine2"
                                                value={customerForm.billingAddress.addressLine2}
                                                onChange={handleInputChange}
                                                placeholder="Enter address line 2 (optional)"
                                            />
                                        </Form.Group>
                                    </Col>
                                </Row>

                                <Row>
                                    <Col md={6}>
                                        <Form.Group className="mb-3" controlId="billingCity">
                                            <Form.Label>City</Form.Label>
                                            <Form.Control
                                                type="text"
                                                name="billingAddress.city"
                                                value={customerForm.billingAddress.city}
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
                                        <Form.Group className="mb-3" controlId="billingState">
                                            <Form.Label>State</Form.Label>
                                            <Form.Select
                                                name="billingAddress.state"
                                                value={customerForm.billingAddress.state}
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
                                        <Form.Group className="mb-3" controlId="billingPostalCode">
                                            <Form.Label>Postal Code</Form.Label>
                                            <Form.Control
                                                type="text"
                                                name="billingAddress.postalCode"
                                                value={customerForm.billingAddress.postalCode}
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
                                        <Form.Group className="mb-3" controlId="billingCountry">
                                            <Form.Label>Country</Form.Label>
                                            <Form.Control
                                                type="text"
                                                name="billingAddress.country"
                                                value={customerForm.billingAddress.country}
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
                                                <Form.Group className="mb-3" controlId="shippingAddressLine1">
                                                    <Form.Label>Address Line 1</Form.Label>
                                                    <Form.Control
                                                        type="text"
                                                        name="shippingAddress.addressLine1"
                                                        value={customerForm.shippingAddress.addressLine1}
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
                                                <Form.Group className="mb-3" controlId="shippingAddressLine2">
                                                    <Form.Label>Address Line 2</Form.Label>
                                                    <Form.Control
                                                        type="text"
                                                        name="shippingAddress.addressLine2"
                                                        value={customerForm.shippingAddress.addressLine2}
                                                        onChange={handleInputChange}
                                                        placeholder="Enter address line 2 (optional)"
                                                    />
                                                </Form.Group>
                                            </Col>
                                        </Row>

                                        <Row>
                                            <Col md={6}>
                                                <Form.Group className="mb-3" controlId="shippingCity">
                                                    <Form.Label>City</Form.Label>
                                                    <Form.Control
                                                        type="text"
                                                        name="shippingAddress.city"
                                                        value={customerForm.shippingAddress.city}
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
                                                <Form.Group className="mb-3" controlId="shippingState">
                                                    <Form.Label>State</Form.Label>
                                                    <Form.Select
                                                        name="shippingAddress.state"
                                                        value={customerForm.shippingAddress.state}
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
                                                <Form.Group className="mb-3" controlId="shippingPostalCode">
                                                    <Form.Label>Postal Code</Form.Label>
                                                    <Form.Control
                                                        type="text"
                                                        name="shippingAddress.postalCode"
                                                        value={customerForm.shippingAddress.postalCode}
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
                                                <Form.Group className="mb-3" controlId="shippingCountry">
                                                    <Form.Label>Country</Form.Label>
                                                    <Form.Control
                                                        type="text"
                                                        name="shippingAddress.country"
                                                        value={customerForm.shippingAddress.country}
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
                                        onClick={() => navigateTo('/')}
                                        className="w-50"
                                    >
                                        Cancel
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
                                                Updating...
                                            </>
                                        ) : (
                                            'Update Profile'
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