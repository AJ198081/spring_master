import {Link, useParams} from "react-router-dom";
import {type FormEvent, useEffect, useState} from "react";
import type {Product} from "../../store/ProductStore.ts";
import {getProductById, updateProduct} from "../../services/ProductService.ts";
import {toast} from "react-toastify";
import {LoadSpinner} from "../common/LoadSpinner.tsx";
import {BrandSelector} from "../common/BrandSelector.tsx";
import {CategorySelector} from "../common/CategorySelector.tsx";
import {initialProductState} from "../../types/OrderType.ts";

export const UpdateProduct = () => {
    const {productId} = useParams();
    const [isLoading, setIsLoading] = useState(false);
    const [updated, setUpdated] = useState(false);
    const [product, setProduct] = useState<Product>(initialProductState);
    const [selectedBrand, setSelectedBrand] = useState<string>('');
    const [selectedCategory, setSelectedCategory] = useState<string>('');
    const [currentPersistedStateOfProduct, setCurrentPersistedStateOfProduct] = useState<Product | null>(null);

    useEffect(() => {
        setIsLoading(true);
        getProductById(Number(productId))
            .then((product) => {
                if (product) {
                    setProduct(product);
                    setSelectedBrand(product.brand);
                    setSelectedCategory(product.categoryName);
                    setCurrentPersistedStateOfProduct(product);
                }
            })
            .catch((error) => {
                toast.error(`Error getting product; issue is - ${error.response?.data?.detail}`);
                setProduct(initialProductState);
            })
            .finally(() => setIsLoading(false));

    }, [productId]);


    const handleProductUpdate = (e: FormEvent<HTMLFormElement>) => {
        e.preventDefault();

        if (selectedBrand.trim() === '' || selectedCategory.trim() === '') {
            toast.error('Please select a brand and a category');
            return;
        }

        if (product.id === undefined) {
            toast.error('Product id is undefined');
            return;
        }

        if (product.name.trim() === '') {
            toast.error('Product name is empty');
            return;
        }

        Object.keys(product).forEach(key => {
            const value = product[key as keyof Product];
            if (typeof value === 'string') {
                product[key as keyof Product] = value.trim() as never;
            }
        });

        updateProduct(product)
            .then((updatedProduct) => {
                toast.success(`Product ${updatedProduct.name} updated successfully`);
                setUpdated(true);
            })
            .catch((error) => {
                toast.error(`Error updating product; issue is - ${error.response?.data?.detail}`);
            });
    };

    function resetProductState() {
        if (currentPersistedStateOfProduct === null) {
            return;
        }
        setProduct(currentPersistedStateOfProduct);
        setSelectedBrand(currentPersistedStateOfProduct.brand || '');
        setSelectedCategory(currentPersistedStateOfProduct.categoryName || '');
    }

    const renderUpdateProductButtons = () => (
        <>
            <button
                type={"button"}
                className="btn btn-outline-secondary"
                onClick={resetProductState}
            >Reset changes
            </button>
            <button
                type="submit"
                className="btn btn-success"
            >
                Update product
            </button>
            <Link
                to={`/update-product-images/${product.id}`}
                className="btn btn-outline-danger"
            >
                Update images
            </Link>
        </>
    );

    const renderProceedToImageUploader =
        <>
            <Link
                className="btn btn-outline-success"
                to={`/update-product-images/${product.id}`}
            >
                Add images
            </Link>
            <Link to="/products">
                <Link
                    to={`/products/${product.id}/details`}
                    className="btn btn-outline-danger"
                >
                    Done
                </Link>
            </Link>
        </>;

    const handleProductStateUpdate = (e: { target: { name: string, value: string } }) => {

        const {name, value} = e.target as HTMLInputElement;
        setProduct(prevState => ({...prevState, [name]: value}));
    };

    return (<div className={"container"}>
        {isLoading && <LoadSpinner/>}
        {!isLoading && <div className={"mt-5"}>
            <h3 className={'mb-5'}>Update Product</h3>
            <form onSubmit={handleProductUpdate}>
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
                        onChange={handleProductStateUpdate}
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
                        onChange={handleProductStateUpdate}
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
                            onChange={handleProductStateUpdate}
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
                            onChange={handleProductStateUpdate}
                        />
                    </div>
                </div>
                <div className={'my-4 d-flex justify-content-start gap-3'}>
                    {!updated
                        ? renderUpdateProductButtons()
                        : renderProceedToImageUploader
                    }
                </div>
            </form>
        </div>}
    </div>)
}