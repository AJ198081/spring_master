import {useProductStore} from "../../store/ProductStore.tsx";
import {useState, type KeyboardEvent, type ChangeEvent} from "react";

export const SearchBar = () => {

    const [searchText, setSearchText] = useState("");
    const [selectedCategory, setSelectedCategory] = useState("all");

    const productStore = useProductStore();

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
        let filteredProducts = productStore.allProducts;

        // Filter by search text if provided
        if (searchText.trim() !== "") {
            filteredProducts = filteredProducts.filter(product => 
                product.name.toLowerCase().includes(searchText.toLowerCase()) ||
                product.description.toLowerCase().includes(searchText.toLowerCase())
            );
        }

        if (selectedCategory !== "all") {
            const categoryMappings: {[key: string]: string[]} = {
                "clothes": ["shirt", "pants", "jacket", "clothing"],
                "electronics": ["laptop", "smartphone", "headphones", "camera", "tv", "mouse", "console", "speaker", "tablet"],
                "furniture": ["chair", "table", "desk", "sofa", "furniture"],
                "toys": ["toy", "game", "gaming"]
            };

            filteredProducts = filteredProducts.filter(product => {
                const keywords = categoryMappings[selectedCategory] || [];
                return keywords.some(keyword => 
                    product.name.toLowerCase().includes(keyword) || 
                    product.description.toLowerCase().includes(keyword)
                );
            });
        }

        productStore.setFilteredProducts(filteredProducts);
    };

    const clearFilters = () => {
        setSearchText("");
        setSelectedCategory("all");
        productStore.setFilteredProducts([]);
    };

    return (
        <div className={"search-bar input-group input-group-sm"}>
            <select 
                className={"form-control-sm"}
                value={selectedCategory}
                onChange={handleCategoryChange}
            >
                <option value={"all"}>All Categories</option>
                <option value={"clothes"}>Clothes</option>
                <option value={"electronics"}>Electronics</option>
                <option value={"furniture"}>Furniture</option>
                <option value={"toys"}>Toys</option>
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
