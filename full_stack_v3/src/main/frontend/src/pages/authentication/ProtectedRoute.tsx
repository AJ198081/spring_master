import {ReactNode, useContext} from "react";
import {Navigate} from "react-router-dom"; // Add Navigate import
import {UserAuthenticationContext} from "../../contexts/user/UserAuthenticationContext";
import {isJwtValid} from "../../utils/Utils.ts";

interface ProtectedRouteProps {
    children: ReactNode;
}

export const ProtectedRoute = ({children}: ProtectedRouteProps) => {
    const {token} = useContext(UserAuthenticationContext);

    if (token && isJwtValid(token)) {
        return children;
    }

    return <Navigate to="/login"/>;
};
