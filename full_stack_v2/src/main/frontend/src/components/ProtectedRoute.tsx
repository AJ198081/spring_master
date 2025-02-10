import {ReactNode} from "react";
import { Navigate } from "react-router-dom";
import {useApiContext} from "./../hooks/ApiContextHook.ts";

interface ProtectedRouteProps {
    children: ReactNode;
    adminPage: boolean;
}

export const ProtectedRoute = ({ children, adminPage }: ProtectedRouteProps) => {
    const { token, isAdmin } = useApiContext();

    if (!token) {
        return <Navigate to="/login" />;
    }

    if (token && adminPage && !isAdmin) {
        return <Navigate to="/access-denied" />;
    }

    return children;
};
