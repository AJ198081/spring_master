import {type Product, useProductStore} from "../../store/ProductStore.tsx";
import {type ChangeEvent, type KeyboardEvent, useEffect, useState} from "react";
import {toast} from "react-toastify";

const ALL = "all";
export const SearchBar = () => {

    const [searchText, setSearchText] = useState("");
    const [availableCategories, setAvailableCategories] = useState<string[]>([]);
    const [selectedCategory, setSelectedCategory] = useState(ALL);

    const allProducts = useProductStore(state => state.allProducts);
    const setSearchedProducts = useProductStore(state => state.setSearchedProducts);

    useEffect(() => {
        if (allProducts && allProducts.length > 0) {
            const distinctCategories = [...new Set(allProducts.map(product => product.categoryName))];
            distinctCategories.sort((a, b) => a.localeCompare(b));
            distinctCategories.unshift(ALL);
            setAvailableCategories(distinctCategories);
        }
    }, [allProducts]);

    const handleInputChange = (e: ChangeEvent<HTMLInputElement>) => {
        setSearchText(e.target.value);
    };

    const handleKeyDown = (e: KeyboardEvent<HTMLInputElement>) => {
        if (e.key === 'Enter') {
            searchProducts();
        }
    };

    const handleCategoryChange = (e: ChangeEvent<HTMLSelectElement>) => {
        setSelectedCategory(e.target.value);
    };

    const searchProducts = () => {

        if (searchText.trim() !== "") {

            const searchTerm = searchText.trim().toLowerCase();

            let searchedProducts: Product[];

            if (selectedCategory === ALL) {
                searchedProducts = allProducts
                    .filter(product =>
                        product.name.toLowerCase().includes(searchTerm) ||
                        product.description.toLowerCase().includes(searchTerm)
                    )
            } else {
                searchedProducts = allProducts
                    .filter(product =>
                            product.categoryName.split(' ')
                                .filter(cat => cat !== '&')
                                .some(cat => selectedCategory.split(' ')
                                    .filter(cat => cat != '&')
                                    .includes(cat))
                            && (product.name.toLowerCase().includes(searchTerm)
                                || product.categoryName === selectedCategory && product.description.toLowerCase().includes(searchTerm)
                            )
                    );
            }

            if (searchedProducts.length === 0) {
                toast.error("No product meets yours selected search criteria, please try again with different search criteria.");
            } else {
                setSearchedProducts(searchedProducts);
            }
        } else if (selectedCategory !== ALL) {
            const productsInSelectedCategory = allProducts
                .filter(product =>
                    product.categoryName.split(' ')
                        .filter(cat => cat !== '&')
                        .some(cat => selectedCategory.split(' ')
                            .filter(cat => cat !== '&')
                            .includes(cat)));
            setSearchedProducts(productsInSelectedCategory);
        } else {
            setSearchedProducts(allProducts);
        }
    };

    const clearFilters = () => {
        setSearchText("");
        setSelectedCategory(ALL);
        setSearchedProducts(allProducts);
    };

    return (
        <div className={"search-bar input-group input-group-sm"}>
            <select
                className={"form-control-sm"}
                value={selectedCategory}
                onChange={handleCategoryChange}
            >
                {availableCategories.map(category => (
                    <option
                        key={category}
                        value={category}
                    >
                        {category.charAt(0).toUpperCase() + category.slice(1)}
                    </option>
                ))}
            </select>
            <input
                type={"text"}
                className={"form-control"}
                placeholder={"search for products (e.g. shoes)"}
                value={searchText}
                onChange={handleInputChange}
                onKeyDown={handleKeyDown}
            />
            <button
                className={"btn btn-primary mx-2 rounded"}
                onClick={searchProducts}
            >
                Search
            </button>
            <button
                className={"btn btn-info rounded"}
                onClick={clearFilters}
            >
                Clear Filters
            </button>
        </div>
    )
}
