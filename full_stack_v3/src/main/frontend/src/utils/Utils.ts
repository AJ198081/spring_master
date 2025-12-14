import {AxiosError} from "axios";
import {jwtDecode} from "jwt-decode";
import {CustomJwtPayload} from "../domain/Types.ts";

export const isProblemDetail = (error: AxiosError) => {
    if (!error.response) {
        console.log('Not Problem Detail')
        return false;
    }

    const data = error.response.data;
    return (
        typeof data === 'object' &&
        data !== null &&
        'status' in data &&
        'title' in data &&
        'type' in data &&
        'detail' in data
    );
}

export const isJwtValid = (token: string) => {
    const decodedToken = jwtDecode<CustomJwtPayload>(token);
    return !!(decodedToken.exp && decodedToken.exp > Date.now() / 1000);
}


export const getUserName = (token: string) => {
    if (!isJwtValid(token)) {
        return 'Guest';
    }
    const decodedToken = jwtDecode<CustomJwtPayload>(token);
    return `${decodedToken.lastName}, ${decodedToken.firstName}`.trim();
}

export const getUserRole = (token: string) => {
    return jwtDecode<CustomJwtPayload>(token).roles.split("_")[1];
}