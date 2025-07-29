import {Navigate, Outlet, useLocation} from "react-router-dom";
import {useAuthStore} from "../../store/AuthStore.ts";
import type {ReactNode} from "react";

interface ProtectedRouteProps {
    children?: ReactNode;
    allowedRoles?: string[];
    useOutlet?: boolean;
}

export const ProtectedRoute = ({children, allowedRoles = [], useOutlet = false}: ProtectedRouteProps) => {

    const location = useLocation();

    const currentAuthentication = useAuthStore(state => state.authState);

    if (!currentAuthentication?.isAuthenticated) {
        return <Navigate to="/login" state={{from: location.pathname}} replace/>;
    }

    const setOfAllowedRoles = new Set(allowedRoles?.map(role => role.toUpperCase()));

    const isAuthorised = currentAuthentication.roles.some(role => setOfAllowedRoles.has(role.toUpperCase()));

    if (isAuthorised) {
        return useOutlet ? <Outlet /> : children;
    } else {
        return <Navigate to="/unauthorized" state={{from: location.pathname}} replace/>;
    }
};