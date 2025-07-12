import {useEffect, useState} from "react";
import {useProductStore} from "../../store/ProductStore.tsx";


export const Sidebar = () => {

    const [selectedBrands, setSelectedBrands] = useState<string[]>([]);
    const [currentProductsBrands, setCurrentProductsBrands] = useState<string[]>([]);

    const filteredProducts = useProductStore(state => state.filteredProducts);
    const productsToDisplay = useProductStore(state => state.productsToShow());
    const setFilteredProducts = useProductStore(state => state.setFilteredProducts);

    useEffect(() => {
        setCurrentProductsBrands([...new Set(productsToDisplay.map(product => product.brand))]);
    }, [productsToDisplay])

    useEffect(() => {
        if (selectedBrands.length === 0) {
            setFilteredProducts(filteredProducts);
            return;
        }
        setFilteredProducts(filteredProducts.filter(product => selectedBrands.includes(product.brand)));
    }, [selectedBrands])

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
