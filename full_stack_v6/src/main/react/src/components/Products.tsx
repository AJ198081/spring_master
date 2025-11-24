import {useEffect, useState} from "react";
import {FaExclamationTriangle} from "react-icons/fa";

import type {components} from "../assets/schema";
import {ProductCard} from "./ProductCard.tsx";
import {getAllProducts} from "../api-client/BackendClient.tsx";

export type Product = components["schemas"]["Product"];

interface ProductsProps {
    onClick: (product: Product, operation?: string) => void;
}

export const Products = ({onClick}: ProductsProps) => {

    const [isLoading, setIsLoading] = useState<boolean>(false);
    const [error, setError] = useState<string | null>();

    const [products, setProducts] = useState<Product[]>([{} as Product]);

    const [_address, _setAddress] = useState<components["schemas"]["Address"] | null>(null);


    /*    useEffect(() => {
            setTimeout(() => {
                setIsLoading(false);
            }, 2000);
            setTimeout(() => {
                setError(null);
            }, 5000);
        })*/

    useEffect(() => {
        console.log('Fetching products.');
        setIsLoading(true);
        getAllProducts()
            .then(response => {
                console.log('Products fetched successfully:');
                setProducts(response.data);
            })
            .catch(error => {
                console.error('Error fetching products:', error);
                setError(error.message);
            })
            .finally(() => setIsLoading(false));
    }, []);

    const renderProductCards = () => {

        return <ul className={"grid md:grid-cols-4 xl:grid-cols-2 gap-5 items-center justify-center"}>
            {products
                .map(product => <li key={product.id}>
                        <ProductCard
                            product={product}
                            onClick={onClick}
                        />
                    </li>
                )}
        </ul>
    }
    
    return (
        <div className={"lg:px-14 sm:px-8 px-4 py-14 2xl:w-[90%] 2xl:mx-auto"}>
            {
                isLoading
                    ? <h1 className={"flex flex-col h-[100%] w-[100%] justify-center items-center"}>Loading...</h1>
                    : !error
                        ? renderProductCards()
                        : <h1 className={"text-red-600 flex justify-center gap-2 mx-auto"}>
                            <FaExclamationTriangle/>
                            {error}
                        </h1>
            }
        </div>
    )
}
