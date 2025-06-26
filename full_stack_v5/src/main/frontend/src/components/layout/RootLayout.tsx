import {Outlet} from "react-router-dom";
import {NavBar} from "./NavBar";
import {Footer} from "./Footer.tsx";

export const RootLayout = () => {
    return (
        <main>
            <NavBar />
            <Outlet />
            <Footer />
        </main>
    )
}

