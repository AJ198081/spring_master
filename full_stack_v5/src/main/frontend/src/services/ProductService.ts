import {backendClient} from './Api.ts';

export const getDistinctProducts = async () => {

    try {
        const response = await backendClient.get('/products/distinctByName');
        if (response.status === 200) {
            return response.data;
        }
        return [];
    } catch (e) {
        console.log(`Error thrown whilst fetching distinct products ${e}`);
        throw e;
    }
}