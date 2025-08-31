import {useSearchParams} from "react-router-dom";
import {useEffect} from "react";
import {updateAuthStateWithJwt} from "../../services/Api.ts";

export const AdminDashboard = () => {

    const [searchParams] = useSearchParams();

    useEffect(() => {
        updateAuthStateWithJwt(searchParams.get('token'));
    }, [searchParams]);

    return (
        <h1>Admin Dashboard</h1>
    )
}
