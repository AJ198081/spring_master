export const SearchBar = () => {
    return (
        <div className={"search-bar input-group, input-group-sm"}>
            <select className={"form-control-sm"}>
                <option value={"all"}>All Categories</option>
                <option value={"clothes"}>Clothes</option>
                <option value={"electronics"}>Electronics</option>
                <option value={"furniture"}>Furniture</option>
                <option value={"toys"}>Toys</option>
            </select>
            <input type={"text"} className={"form-control"} placeholder={"search for products (e.g. shoes)"}/>
            <button className={"btn btn-outline-info"}>Clear Filters</button>
        </div>
    )
}