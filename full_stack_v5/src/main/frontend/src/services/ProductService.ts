import {backendClient} from './Api.ts';

export const getDistinctProducts = async () => {

    try {
        const response = await backendClient.get('/products/distinctByName');
        return response.data;
    } catch (e) {
        console.log(`Error thrown whilst fetching distinct products ${e}`);
        // throw e;
    }
}