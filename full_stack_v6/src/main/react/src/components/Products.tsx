import {useEffect, useState} from "react";
import {FaExclamationTriangle} from "react-icons/fa";

import type {components} from "../assets/schema";

export const Products = () => {

    const [isLoading, setIsLoading] = useState<boolean>(true);
    const [products, _] = useState<string>('Products');
    const [error, setError] = useState<string | null>('Error fetching products! Recovery initiated..');

    const [product, _setProduct] = useState<components["schemas"]["Product"]>({});
    const [address, _setAddress] = useState<components["schemas"]["Address"] | null>(null);

    console.log(address?.addressType === 'BILLING');
    console.log(product.name?.length);

    useEffect(() => {
        setTimeout(() => {
            setIsLoading(false);
        }, 2000);
        setTimeout(() => {
            setError(null);
        }, 5000);
    })


    return (
        <div className={"lg:px-14 sm:px-8 px-4 py-14 2xl:w-[90%] 2xl:mx-auto"}>
            {
                isLoading
                    ? <h1 className={"flex flex-col h-[100%] w-[100%] justify-center items-center"}>Loading...</h1>
                    : !error
                        ? <h1 className={"flex bg-blue-200 justify-center w-[100%] mx-auto"}>{products}</h1>
                        : <h1 className={"text-red-600 flex justify-center gap-2 mx-auto"}>
                            <FaExclamationTriangle/>
                            {error}
                        </h1>
            }
        </div>
    )
}
