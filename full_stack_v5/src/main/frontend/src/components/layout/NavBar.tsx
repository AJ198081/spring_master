import {Container, Nav, Navbar, NavDropdown} from "react-bootstrap";
import {Link} from "react-router-dom";
import {useEffect} from "react";
import {useProductStore} from "../../store/ProductStore.ts";
import {getCustomer, getCustomerCart} from "../../services/CartService.ts";
import {useAuthStore} from "../../store/AuthStore.ts";
import {Badge} from "@mui/material";
import ShoppingCartIcon from '@mui/icons-material/ShoppingCart';

export const NavBar = () => {
    const currentAuthentication = useAuthStore(state => state.authState);
    const customerCart = useProductStore(state => state.cartForThisCustomer);
    const setCustomerCart = useProductStore(state => state.setCartForThisCustomer);
    const setCurrentCustomerId = useProductStore(state => state.setThisCustomerId);
    const setCurrentUser = useProductStore(state => state.setCurrentUser);

    useEffect(() => {

        if (currentAuthentication?.isAuthenticated && currentAuthentication.customerId) {
            setCurrentCustomerId(currentAuthentication.customerId);
            getCustomerCart(currentAuthentication.customerId)
                .then(response => {
                    setCustomerCart(response);
                })
                .catch(error => {
                    console.log(`Error fetching customer's cart, ${error.response?.data?.detail}`)
                });
        }
        
        getCustomer()
            .then(customer => {
                if (customer) {
                    setCurrentCustomerId(customer.id!);
                }
            })
            .catch(error =>
                console.log(`Error fetching this customer's details, ${error.response?.data?.detail}`)
            );
    }, [currentAuthentication?.customerId, currentAuthentication?.isAuthenticated, setCurrentCustomerId, setCustomerCart]);

    console.log(`NavBar ${currentAuthentication?.isAuthenticated} ${currentAuthentication?.customerId === undefined}`);

    const handleUserRegistration = () => {
        setCurrentUser(null);
    };

    return (
        <Navbar
            collapseOnSelect
            expand="lg"
            sticky={"top"}
            className="nav-bg bg-dark-subtle"
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
                        {currentAuthentication?.isAuthenticated
                            ? <NavDropdown
                                title="Account"
                                id="collapsible-nav-dropdown"
                            >
                                <NavDropdown.Item
                                    to={"/update-profile"}
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
                                    to={"/logout"}
                                    as={Link}
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
                        {currentAuthentication?.customerId !== undefined &&
                            <Link
                                to={"/my-cart"}
                                className={`d-flex align-items-center gap-2 ms-4`}
                            >
                                <Badge badgeContent={customerCart?.cartItems?.length ?? 0} color={"info"}>
                                <ShoppingCartIcon color={"primary"} />
                                </Badge>
                            </Link>
                        }
                    </Nav>
                </Navbar.Collapse>
            </Container>
        </Navbar>
    )
}