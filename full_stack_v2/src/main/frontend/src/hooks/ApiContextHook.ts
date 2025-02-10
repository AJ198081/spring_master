import {useContext} from "react";

import {ContextApi} from "../store/ContextApi.tsx";

export const useApiContext = () => {
    return useContext(ContextApi);
};