import {IoWalletSharp} from "react-icons/io5";
import {Link, NavLink, useNavigate} from "react-router-dom";
import {useContext} from "react";
import {UserAuthenticationContext} from "../contexts/UserAuthenticationContext.tsx";

export const Navbar = () => {

    const { isAuthenticated} = useContext(UserAuthenticationContext)

    const navigateTo = useNavigate();

    return <nav className="navbar navbar-expand-lg bg-dark border-bottom border-body" data-bs-theme="dark">
        <div className="container-fluid">
            <Link className="navbar-brand" to={"/"}>
                <IoWalletSharp size={'2em'} color={'lightblue'}/>
            </Link>
            <button className="navbar-toggler" type="button" data-bs-toggle="collapse"
                    data-bs-target="#navbarNavAltMarkup" aria-controls="navbarNavAltMarkup" aria-expanded="false"
                    aria-label="Toggle navigation">
                <span className="navbar-toggler-icon"></span>
            </button>

            <div className="collapse navbar-collapse" id="navbarNavAltMarkup">
                <div className="navbar-nav">
                    <NavLink to={"/"} className={"nav-link"}>Dashboard</NavLink>
                    <NavLink className="nav-link" to={'/new'}>New Expense</NavLink>
                </div>
            </div>
        </div>
        <div className="d-flex me-3" role={"button"}>
            <button
                className={`btn btn-outline-primary mx-1 ${isAuthenticated ? 'd-none' : ''}`}
                onClick={() => {
                    navigateTo('/login');
                }
            }
            >
                Login
            </button>
            <Link to={'/register'}
                  className={`btn btn-outline-info mx-1 ${isAuthenticated ? 'd-none' : ''}`}
            >
                Register
            </Link>
            <button className={`btn btn-outline-danger ${isAuthenticated ? '' : 'd-none'}`}
                    onClick={() => {
                        navigateTo('/logout');
                    }}>
                Logout
            </button>
        </div>

    </nav>;
}