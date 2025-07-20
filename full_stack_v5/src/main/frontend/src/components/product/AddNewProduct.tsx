import {type Product, useProductStore} from "../../store/ProductStore.tsx";
import {toast} from "react-toastify";
import {type FormEvent, useState} from "react";
import {addNewProduct} from "../../services/ProductService.ts";
import {BrandSelector} from "../common/BrandSelector.tsx";
import {CategorySelector} from "../common/CategorySelector.tsx";

const initialProductState: Product = {
    name: '',
    description: '',
    brand: '',
    categoryName: '',
    price: 0,
    inventory: 0,
    images: [],
}

export const AddNewProduct = () => {

    const addProductToStore = useProductStore(state => state.addProduct);
    const [product, setProduct] = useState<Product>(initialProductState);
    const [selectedBrand, setSelectedBrand] = useState<string>(initialProductState.brand || '');
    const [selectedCategory, setSelectedCategory] = useState<string>(initialProductState.categoryName || '');

    const handleProductChange = (e: { target: { name: string; value: string | number; }; }) => {
        const {name, value} = e.target;
        setProduct(prevState => ({...prevState, [name]: value}));
    }

    const handleAddNewProduct = (e: FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        console.log(product);
        if (selectedBrand.trim() === '' || selectedCategory.trim() === '') {
            toast.error('Please select a brand and a category');
            return;
        }

        Object.keys(product).forEach(key => {
            const value = product[key as keyof Product];
            if (typeof value === 'string') {
                product[key as keyof Product] = value.trim() as never;
            }
        });

        product.brand = selectedBrand;
        product.categoryName = selectedCategory;

        addNewProduct(product)
            .then(newProduct => {
                toast.success(`Product "${newProduct.name}" added successfully!`);
                addProductToStore(newProduct);
                resetProductState();
            })
            .catch(e =>
                toast.error(`Error adding new product; issue is - ${e.response?.data?.detail}`)
            );
    }


    function resetProductState() {
        setProduct(initialProductState);
        setSelectedBrand(initialProductState.brand || '');
        setSelectedCategory(initialProductState.categoryName || '');
    }

    return (

        <section className={'container my-5'}>
            <div>
                <div>
                    <h4>Add new product</h4>
                    <form onSubmit={handleAddNewProduct}>
                        <div className="mb-3">
                            <label
                                htmlFor="name"
                                className="form-label"
                            >Name</label>
                            <input
                                type="text"
                                className="form-control"
                                id="name"
                                name="name"
                                value={product.name}
                                onChange={handleProductChange}
                                required={true}
                            />
                        </div>
                        <div className="mb-3">
                            <label
                                htmlFor="description"
                                className="form-label"
                            >Description</label>
                            <textarea
                                className="form-control"
                                id="description"
                                name="description"
                                value={product.description}
                                onChange={handleProductChange}
                                style={{height: '100px'}}
                            />
                        </div>
                        <div className={`d-flex justify-content-between col-md-12`}>
                            <div className="mb-3">
                                <label
                                    htmlFor="brand"
                                    className="form-label"
                                >
                                    Brand
                                </label>
                                <BrandSelector
                                    selectedBrand={selectedBrand}
                                    setSelectedBrand={setSelectedBrand}
                                />
                            </div>
                            <div className="mb-3">
                                <label
                                    htmlFor="category"
                                    className="form-label"
                                >
                                    Category
                                </label>
                                <CategorySelector
                                    selectedCategory={selectedCategory}
                                    setSelectedCategory={setSelectedCategory}
                                />
                            </div>

                            <div className="mb-3">
                                <label
                                    htmlFor="price"
                                    className="form-label"
                                >Price</label>
                                <input
                                    type="number"
                                    className="form-control"
                                    id="price"
                                    name="price"
                                    value={product.price}
                                    onChange={handleProductChange}
                                    required={true}
                                    min={1.00}
                                    step={0.01}
                                    max={1000000}
                                    style={{textAlign: 'right'}}
                                    pattern="^\d+(?:\.\d{1,2})?$"
                                    title="Numbers only, 2 decimal places allowed"
                                />
                            </div>
                            <div className="mb-3">
                                <label
                                    htmlFor="inventory"
                                    className="form-label"
                                >Inventory</label>
                                <input
                                    type="number"
                                    className="form-control"
                                    id="inventory"
                                    name="inventory"
                                    value={product.inventory}
                                    onChange={handleProductChange}
                                />
                            </div>
                        </div>
                        <div className="mb-3">
                            <label
                                htmlFor="images"
                                className="form-label"
                            >Images</label>
                            <input
                                type="file"
                                className="form-control"
                                id="images"
                                name="images"
                                multiple={true}
                            />
                        </div>
                        <div className={'my-4 d-flex justify-content-start gap-3'}>
                            <button
                                type={"reset"}
                                className="btn btn-outline-secondary"
                                onClick={resetProductState}
                            >Reset
                            </button>
                            <button
                                type="submit"
                                className="btn btn-success"
                            >Add product
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </section>
    )
}
