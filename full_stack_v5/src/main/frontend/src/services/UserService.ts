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