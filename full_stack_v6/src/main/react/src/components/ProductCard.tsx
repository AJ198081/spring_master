import {useEffect, useState} from "react";
import toast from "react-hot-toast";

export const ProductCard = () => {

    const [openModal, setOpenModal] = useState<boolean>(false);

    useEffect(() => {
        if (openModal)  {
            toast.success('Product added to cart', {
                id: 'product-added',
                position: 'top-center',
                duration: 5000,
            });
            setOpenModal(false);
        }
    }, [openModal]);

    return (<div className={"border rounded-lg shadow-xl overflow-hidden transition-shadow duration-300 min-w-lg"}>
            <div
                onClick={() => setOpenModal(true)}
                className={"w-full overflow-hidden aspect-[3/2]"}
            >
                <img
                    src={'https://placehold.co/600x400'}
                    alt={'cat'}
                    className={"w-full h-full cursor-pointer transition-transform duration-300 transform hover:scale-105"}
                />
            </div>
            <div className={"p-4"}>
                <h2 className={"text-lg font-semibold"}>Product Name</h2>
                <p className={"text-gray-600"}>Product Description</p>
                <p className={"text-gray-600"}><span className={"font-bold"}>Price:</span> ${Number(100).toFixed(2)}</p>
            </div>
        </div>
    );
}
