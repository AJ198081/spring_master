import {Outlet} from "react-router-dom";
import {NavBar} from "./NavBar";
import {Footer} from "./Footer.tsx";

export const RootLayout = () => {
    return (
        <main className={"d-flex flex-column min-vh-100"}>
            <NavBar />
            <div className="flex-grow-1">
                <Outlet/>
            </div>
            <Footer/>
        </main>
    )
}

