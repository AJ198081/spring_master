import {Link, useNavigate} from "react-router-dom";
import {useApiContext} from "../hooks/ApiContextHook.ts";

export const Navbar = () => {
    const navigate = useNavigate();

    const {
        token,
        setToken,
        isAdmin,
        setIsAdmin,
        setCurrentUser,
        setOpenSidebar,
    } = useApiContext();

    const handleLogout = () => {
        localStorage.removeItem('token');
        localStorage.removeItem('isAdmin');
        localStorage.removeItem('USER');
        localStorage.removeItem('CSRF_TOKEN');

        setToken(null);
        setIsAdmin(false);
        setCurrentUser(null);
        setOpenSidebar(false);
        navigate('/login');
    }

    return (<nav className="navbar navbar-expand-lg bg-body-tertiary">
        <div className="container-fluid">
            <a className="navbar-brand" href="#">Home</a>
            <button className="navbar-toggler" type="button" data-bs-toggle="collapse"
                    data-bs-target="#navbarSupportedContent" aria-controls="navbarSupportedContent"
                    aria-expanded="false" aria-label="Toggle navigation">
                <span className="navbar-toggler-icon"></span>
            </button>
            <div className="collapse navbar-collapse" id="navbarSupportedContent">
                <ul className="navbar-nav me-auto mb-2 mb-lg-0">
                    {token && (<>
                        <li className="nav-item">
                            <Link to={"/notes"}>
                                <a className="nav-link" aria-current="page">My Notes</a>
                            </Link>
                        </li>

                        <li className="nav-item">
                            <Link to={"/create-note"}>
                                <a className="nav-link" aria-current="page">Create Note</a>
                            </Link>
                        </li>
                    </>)}

                    <Link to={"/contact"}>
                        <li className="nav-item">
                            <a className="nav-link" aria-current="page">Contact</a>
                        </li>
                    </Link>

                    <Link to={"/about"}>
                        <li className="nav-item">
                            <a className="nav-link" aria-current="page">About</a>
                        </li>
                    </Link>

                    {token ? (
                        <>
                            <Link to={"/profile"}>
                                <li className="nav-item">
                                    <a className="nav-link" aria-current="page">Profile</a>
                                </li>
                            </Link>
                            {isAdmin && (<Link to={"/admin/users"}>
                                <li className="nav-item">
                                    <a className="nav-link" aria-current="page">Admin</a>
                                </li>
                            </Link>)}
                            <button className="btn btn-outline-danger" onClick={handleLogout}>Logout</button>
                        </>
                    )
                    : (
                        <Link to={"/signup"}>
                            <li className="nav-item">
                                Signup
                            </li>
                        </Link>
                        )}
                </ul>
                <form className="d-flex" role="search">
                    <input className="form-control me-2" type="search" placeholder="Search" aria-label="Search"/>
                    <button className="btn btn-outline-success" type="submit">Search</button>
                </form>
            </div>
        </div>
    </nav>);
}
