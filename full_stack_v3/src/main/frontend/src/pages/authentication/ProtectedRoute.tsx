import {ReactNode} from "react";
import {Navigate} from "react-router-dom"; // Add Navigate import
import {useJwt} from "../../hooks/useJwt.ts";

interface ProtectedRouteProps {
    children: ReactNode;
}

export const ProtectedRoute = ({children}: ProtectedRouteProps) => {
    const {token} = useJwt();
    return token ? children : <Navigate to="/login"/>;
};
