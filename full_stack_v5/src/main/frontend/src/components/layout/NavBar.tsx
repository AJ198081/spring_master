import {Container, Nav, Navbar, NavDropdown} from "react-bootstrap";
import {Link} from "react-router-dom";
import {type MouseEvent, useEffect, useState} from "react";
import {FaShoppingCart} from "react-icons/fa";
import {useProductStore} from "../../store/ProductStore.tsx";
import {getFirstCustomer} from "../../services/CartService.ts";

export const NavBar = () => {
    const [isLoggedIn, setIsLoggedIn] = useState(true);
    const customerCart = useProductStore(state => state.cartForThisCustomer);
    const setCurrentCustomerId = useProductStore(state => state.setThisCustomerId);
    const setCurrentUser = useProductStore(state => state.setCurrentUser);

    useEffect(() => {
        getFirstCustomer()
            .then(customer => {
                setCurrentCustomerId(customer.id!);
            })
            .catch(error =>
                console.log(`Error fetching this customer's details, ${error.response?.data?.detail}`)
            );
    }, [setCurrentCustomerId]);

    const handleLoginAction = (event: MouseEvent<HTMLElement>) => {
        event.preventDefault();
        setIsLoggedIn(prev => !prev);
    }
    const handleUserRegistration = () => {
        setCurrentUser(null);
    };

    return (
        <Navbar
            collapseOnSelect
            expand="lg"
            sticky={"top"}
            className="nav-bg"
        >
            <Container>
                <Nav.Link
                    to={"/"}
                    as={Link}
                    className={"navbar-brand"}
                >
                    <span className={"shop-home"}>dev-aj.com</span>
                </Nav.Link>
                <Navbar.Toggle aria-controls="responsive-navbar-nav"/>
                <Navbar.Collapse>
                    <Nav className="me-auto">
                        <Nav.Link
                            to={"/"}
                            as={Link}
                        >
                            Home
                        </Nav.Link>
                        <Nav.Link
                            to={"/products/all"}
                            as={Link}
                        >
                            All Products
                        </Nav.Link>
                    </Nav>
                    <Nav className="me-5">
                        <Nav.Link
                            to={`/add-product`}
                            as={Link}
                            className={"btn btn-outline-secondary"}
                        >Add Product</Nav.Link>
                    </Nav>
                    <Nav>
                        {isLoggedIn
                            ? <NavDropdown
                                title="Account"
                                id="collapsible-nav-dropdown"
                            >
                                <NavDropdown.Item
                                    to={"#action"}
                                    as={Link}
                                >
                                    Profile
                                </NavDropdown.Item>
                                <NavDropdown.Item
                                    to={"/my-orders"}
                                    as={Link}
                                >
                                    My Orders
                                </NavDropdown.Item>
                                <NavDropdown.Divider/>
                                <NavDropdown.Item
                                    to={isLoggedIn ? "/logout" : "/login"}
                                    as={Link}
                                    onClick={event => handleLoginAction(event)}
                                >
                                    Logout
                                </NavDropdown.Item>
                            </NavDropdown>
                            : <NavDropdown
                                title={"Sign-in"}
                                id="collapsible-nav-dropdown"
                            >
                                <NavDropdown.Item
                                    to={"/login"}
                                    as={Link}
                                    onClick={handleLoginAction}
                                >
                                    Login
                                </NavDropdown.Item>
                                <NavDropdown.Divider/>
                                <NavDropdown.Item
                                    to={"/register"}
                                    onClick={handleUserRegistration}
                                    as={Link}
                                >
                                    Register
                                </NavDropdown.Item>
                            </NavDropdown>
                        }
                        <Link
                            to={"/my-cart"}
                            className={`d-flex align-items-center gap-2 ms-4`}
                        >
                            <FaShoppingCart
                                size={20}
                                color={"black"}
                            />
                            <span className="translate-middle badge rounded-pill bg-danger">
                                {customerCart?.cartItems.length ?? 0}
                                <span className="visually-hidden">unread messages</span>
                            </span>
                        </Link>
                    </Nav>
                </Navbar.Collapse>
            </Container>
        </Navbar>
    )
}