import {useProductStore} from "../../store/ProductStore.tsx";
import {useEffect, useState} from "react";
import {getAvailableBrands} from "../../services/ProductService.ts";
import {toast} from "react-toastify";

export const BrandSelector = () => {

    const availableBrands = useProductStore(state => state.productBrands);
    const setAvailableBrands = useProductStore(state => state.setProductBrands);
    const [newBrand, setNewBrand] = useState<string | null>(null);

    useEffect(() => {
        if (availableBrands.length === 0) {
            getAvailableBrands()
                .then(availableBrands => {
                    setAvailableBrands(availableBrands);
                })
                .catch(error => {
                        toast.error(`Exception ${error.response?.data?.data}`);
                    }
                )
        }
    }, [availableBrands.length, setAvailableBrands])

    const handleAddBrand = () => {
        if (newBrand) {
            setNewBrand(null);
            setAvailableBrands([...availableBrands, newBrand]);
        }
    }

    return (
        <div>
            <button className={"d-flex justify-content-between"} onClick={handleAddBrand}></button>
        </div>
    )
}