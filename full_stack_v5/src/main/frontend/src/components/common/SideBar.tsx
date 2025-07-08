import {useEffect, useState} from "react";
import {useProductStore} from "../../store/ProductStore.tsx";


export const Sidebar = () => {

    const productBrandList = useProductStore(state => state.productBrands);
    const updateProductBrands = useProductStore(state => state.setProductBrands);
    const allAvailableProducts = useProductStore(state => state.allProducts);
    const filteredProductsByBrand = useProductStore(state => state.filteredProductsByBrand);
    const setFilteredProducts = useProductStore(state => state.setFilteredProducts);
    const [selectedBrands, setSelectedBrands] = useState<string[]>([]);

    useEffect(() => {
        updateProductBrands([...new Set(allAvailableProducts.map(product => product.brand))]);
    }, [allAvailableProducts, updateProductBrands])

    useEffect(() => {
        const filteredProducts = filteredProductsByBrand(selectedBrands);
        setFilteredProducts(filteredProducts);
    }, [selectedBrands, filteredProductsByBrand, setFilteredProducts])

    return (
        <>
            <div className={"h6"}>Filter by brand</div>
            <ul className={"brand-list"}>
                {productBrandList.length > 0 && productBrandList.map(brand => (
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
