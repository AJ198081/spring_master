import type {UserRegistrationDto, UserResponseDto} from "../types/User.ts";
import {backendClient} from "./Api.ts";
import {AxiosError, type AxiosResponse} from "axios";

export const registerNewUser = async (newUser: UserRegistrationDto)=> {

    const userResponse : AxiosResponse<UserResponseDto> = await backendClient.post('/users/', {
        ...newUser,
        roles: Array.from(newUser.roles)
    });

    if (userResponse.status === 201) {
        return userResponse.data;
    }

    const axiosError = new AxiosError("Error registering new user");
    axiosError.status = userResponse.status;
    axiosError.response = userResponse;
    throw axiosError;
}

export const resetPassword = async (username: string, email: string, newPassword: string): Promise<number> => {
    const response: AxiosResponse<void> = await backendClient.patch(`/users/resetPassword`, null, {
        params: {
            username: username,
            email: email,
            newPassword: newPassword
        }
    });

    if (response.status !== 201) {
        const axiosError = new AxiosError(`Error resetting password for user ${username}`);
        axiosError.status = response.status;
        axiosError.response = response;
        throw axiosError;
    }
    return response.status;
}