import {useEffect, useState} from "react";
import {useProductStore} from "../../store/ProductStore.ts";


export const Sidebar = () => {

    const [selectedBrands, setSelectedBrands] = useState<string[]>([]);
    const [currentProductsBrands, setCurrentProductsBrands] = useState<string[]>([]);

    const currentSearchedProducts = useProductStore(state => state.searchedProducts);
    const setFilteredProductsList = useProductStore(state => state.setFilteredProducts);

    useEffect(() => {
        setCurrentProductsBrands([...new Set(currentSearchedProducts.map(product => product.brand))]);
    }, [currentSearchedProducts])

    useEffect(() => {
        setFilteredProductsList(selectedBrands);
    }, [selectedBrands, setFilteredProductsList])

    return (
        <>
            <div className={"h6"}>Filter by brand</div>
            <ul className={"brand-list"}>
                {currentProductsBrands.length > 0 && currentProductsBrands.map(brand => (
                    <li
                        key={brand}
                        className={"brand-item"}
                    >
                        <label className={"checkbox-container"}>
                            <input
                                type={"checkbox"}
                                className={"checkbox-input"}
                                checked={selectedBrands.includes(brand)}
                                onChange={() => {
                                    if (selectedBrands.includes(brand)) {
                                        setSelectedBrands(prevState => prevState.filter(b => b !== brand));
                                    } else {
                                        setSelectedBrands(prevState => [...prevState, brand]);
                                    }
                                }}
                            />
                            <span className={"checkmark"}/>
                            {brand}
                        </label>
                    </li>))}
            </ul>
        </>
    );
};
