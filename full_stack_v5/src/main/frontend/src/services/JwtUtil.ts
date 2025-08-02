import {jwtDecode} from "jwt-decode";

export const parseJwt = (token: string) => {
    if (token) {
        return jwtDecode<JwtUserPayload>(token);
    }

    return null;
}


interface JwtUserPayload {
    sub: string;
    id: number;
    roles: string[];
    customer: number
    ist: Date
    exp: number;
}

export const isJwtValid = (token: string | null): boolean => {
    if (token !== null) {
        const jwtPayload = jwtDecode<JwtUserPayload>(token);
        return jwtPayload.exp !== undefined && (jwtPayload.exp > Date.now() / 1000);
    } else {
        return false;
    }
}