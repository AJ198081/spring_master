import {UserRegistrationRequest} from "../domain/Types.ts";
import {AxiosInstance} from "./api-client.ts";

export const registerNewUser = (values: UserRegistrationRequest, abortController: AbortController) => {
    return AxiosInstance.post('/api/v1/auth/register', values, {
        signal: abortController.signal
    });
}