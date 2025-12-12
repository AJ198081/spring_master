import {AxiosError} from "axios";

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