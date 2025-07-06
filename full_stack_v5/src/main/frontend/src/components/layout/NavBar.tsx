import {Container, Nav, Navbar, NavDropdown} from "react-bootstrap";
import {Link} from "react-router-dom";
import {type MouseEvent, useState} from "react";

export const NavBar = () => {
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const handleLoginAction = (event: MouseEvent<HTMLElement>) => {
        event.preventDefault();
        setIsLoggedIn(prev => !prev);
    }
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
                            to={"/products"}
                            as={Link}
                        >
                            All Products
                        </Nav.Link>
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
                                    to={isLoggedIn ? "/logout" : "/login"}
                                    as={Link}
                                    onClick={event => handleLoginAction(event)}
                                >
                                    Logout
                                </NavDropdown.Item>
                                <NavDropdown.Divider/>
                                <NavDropdown.Item
                                    to={"#action"}
                                    as={Link}
                                >
                                    Update
                                </NavDropdown.Item>
                            </NavDropdown>
                            : <Nav.Link
                                to={"/login"}
                                as={Link}
                                onClick={handleLoginAction}
                            >Login</Nav.Link>
                        }
                    </Nav>
                </Navbar.Collapse>
            </Container>
        </Navbar>
    )
}