import { IoWalletSharp } from "react-icons/io5";
import { NavLink } from "react-router-dom";

export const Navbar = () => {
    return <nav className="navbar navbar-expand-lg bg-dark border-bottom border-body" data-bs-theme="dark">
        <div className="container-fluid">
            <a className="navbar-brand" href="#">
                <IoWalletSharp size={'2em'} color={'lightblue'}/>
            </a>
            <button className="navbar-toggler" type="button" data-bs-toggle="collapse"
                    data-bs-target="#navbarNavAltMarkup" aria-controls="navbarNavAltMarkup" aria-expanded="false"
                    aria-label="Toggle navigation">
                <span className="navbar-toggler-icon"></span>
            </button>

            <div className="collapse navbar-collapse" id="navbarNavAltMarkup">
                <div className="navbar-nav">
                    <NavLink to={"/"} className={"nav-link active"}>Dashboard</NavLink>
                    <a className="nav-link" href="#">Features</a>
                    <a className="nav-link" href="#">Pricing</a>
                </div>
            </div>
        </div>
        <div className="d-flex me-3" role={"button"}>
            <button className="btn btn-primary">Login</button>
            <button className="btn btn-danger d-none">Logout</button>
        </div>

    </nav>;
}