import {ReactNode, useContext} from "react";
import {Navigate} from "react-router-dom"; // Add Navigate import
import {UserAuthenticationContext} from "../../contexts/UserAuthenticationContext";
import {jwtDecode} from "jwt-decode";
import {CustomJwtPayload} from "../../domain/Types.ts";

interface ProtectedRouteProps {
    children: ReactNode;
}

export const ProtectedRoute = ({children}: ProtectedRouteProps) => {
    const {token} = useContext(UserAuthenticationContext);

    if (token) {
        const decodedToken = jwtDecode<CustomJwtPayload>(token);
        if (decodedToken.exp && decodedToken.exp > Date.now() / 1000) {
            return children;
        }
    }
    return <Navigate to="/login"/>;
};
