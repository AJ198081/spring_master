import {type Product, useProductStore} from "../../store/ProductStore.tsx";
import {type ChangeEvent, type KeyboardEvent, useEffect, useState} from "react";
import {toast} from "react-toastify";

const ALL = "all";
export const SearchBar = () => {

    const [searchText, setSearchText] = useState("");
    const [availableCategories, setAvailableCategories] = useState<string[]>([]);
    const [selectedCategory, setSelectedCategory] = useState(ALL);

    const productStore = useProductStore();

    useEffect(() => {
        const distinctCategories = [...new Set(productStore.allProducts.map(product => product.categoryName))];
        distinctCategories.sort((a, b) => a.localeCompare(b));
        distinctCategories.unshift(ALL);
        setAvailableCategories(distinctCategories);
    }, [productStore.allProducts]);

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
        const allAvailableProducts = productStore.allProducts;

        if (searchText.trim() !== "") {

            const searchTerm = searchText.trim().toLowerCase();

            let filteredProducts: Product[];

            if (selectedCategory === ALL) {
                filteredProducts = allAvailableProducts
                    .filter(product =>
                        product.name.toLowerCase().includes(searchTerm) ||
                        product.description.toLowerCase().includes(searchTerm
                        ))
            } else {
                filteredProducts = allAvailableProducts
                    .filter(product =>
                            product.categoryName.split(' ').some(cat => selectedCategory.split(' ').includes(cat))
                            && (product.name.toLowerCase().includes(searchTerm)
                                || product.categoryName === selectedCategory && product.description.toLowerCase().includes(searchTerm)
                            )
                    );
            }

            if (filteredProducts.length === 0) {
                toast.error("No product meets yours selected search criteria, please try again with different search criteria.");
            } else {
                productStore.setFilteredProducts(filteredProducts);
            }
        } else if (selectedCategory !== ALL) {
            const productsInSelectedCategory = allAvailableProducts
                .filter(product =>
                    product.categoryName.split(' ')
                        .some(cat => selectedCategory.split(' ').includes(cat)));
            productStore.setFilteredProducts(productsInSelectedCategory);
        }
    };

    const clearFilters = () => {
        setSearchText("");
        setSelectedCategory(ALL);
        productStore.setFilteredProducts([]);
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
                className={"btn btn-primary mx-2"}
                onClick={searchProducts}
            >
                Search
            </button>
            <button
                className={"btn btn-info"}
                onClick={clearFilters}
            >
                Clear Filters
            </button>
        </div>
    )
}
