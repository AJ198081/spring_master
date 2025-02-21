import {ReactNode, useContext} from "react";
import { Navigate } from "react-router-dom"; // Add Navigate import
import {UserAuthenticationContext} from "../../contexts/UserAuthenticationContext";

interface ProtectedRouteProps {
    children: ReactNode;
}

export const ProtectedRoute = ({children}: ProtectedRouteProps) => {
    const {token} = useContext(UserAuthenticationContext);

    if (token && token.exp && token.exp > Date.now()/1000) {
        return children;
    } else {
        return <Navigate to="/login"/>;
    }
};
