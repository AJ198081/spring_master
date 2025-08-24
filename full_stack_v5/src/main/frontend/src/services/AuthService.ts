import {backendClient} from "./Api";
import {AxiosError, type AxiosResponse} from "axios";

export interface LoginRequestDto {
    username: string;
    password: string;
}

export const loginUser = async (loginRequestDto: LoginRequestDto) => {

    const loginResponse: AxiosResponse<string> = await backendClient.post(`/auth/login`, loginRequestDto);

    if (loginResponse.status === 200) {
        return loginResponse.data;
    }

    const axiosError = new AxiosError(`Error logging the user with username ${loginRequestDto.username} and password ${loginRequestDto.password}`);
    axiosError.status = loginResponse.status;
    axiosError.response = loginResponse;
    throw axiosError;
};

export const logoutUser = async (signal: AbortSignal) => {
    const logoutResponse: AxiosResponse<void> = await backendClient.get(`/auth/logout`,
        {
            signal: signal
        });

    if (!signal.aborted && logoutResponse.status !== 200) {
        const axiosError = new AxiosError(`Error logging out the user`);
        axiosError.status = logoutResponse.status;
        axiosError.response = logoutResponse;
        throw axiosError;
    }
}