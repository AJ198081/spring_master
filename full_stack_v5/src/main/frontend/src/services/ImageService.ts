import {backendClient} from "./Api.ts";


export const getImageById = async (downloadUrl: string) => {
    try{
        const response = await backendClient.get(`images/${downloadUrl}`);

        if (response.status === 200) {

            console.log(typeof response.data);

            return response.data;
        }
        return null;
    } catch (e) {
        console.log(e);
    }
}