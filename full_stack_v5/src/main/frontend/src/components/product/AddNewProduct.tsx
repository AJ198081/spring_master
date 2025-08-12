import {type Product, useProductStore} from "../../store/ProductStore.ts";
import {toast} from "react-toastify";
import {type FormEvent, useState} from "react";
import {addNewProduct} from "../../services/ProductService.ts";
import {BrandSelector} from "../common/BrandSelector.tsx";
import {CategorySelector} from "../common/CategorySelector.tsx";
import {Step, StepLabel, Stepper} from "@mui/material";
import {ImageUploader} from "../common/ImageUploader.tsx";
import {Link} from "react-router-dom";
import {initialProductState} from "../../types/OrderType.ts";

const STEPS = [`Add new product`, `Upload product image(s)`, 'Done'];

export const AddNewProduct = () => {
    const addProductToStore = useProductStore(state => state.addProduct);
    const [product, setProduct] = useState<Product>(initialProductState);
    const [selectedBrand, setSelectedBrand] = useState<string>(initialProductState.brand || '');
    const [selectedCategory, setSelectedCategory] = useState<string>(initialProductState.categoryName || '');
    const [activeStep, setActiveStep] = useState<number>(0);

    const handleProductChange = (e: { target: { name: string; value: string | number; }; }) => {

        if (product.id) {
            toast.warn(`This Product has already been saved, with id ${product.id}, try update the product tab`);
            return;
        }

        const {name, value} = e.target;
        setProduct(prevState => ({...prevState, [name]: value}));
    }

    const handleAddNewProduct = (e: FormEvent<HTMLFormElement>) => {

        e.preventDefault();

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
                setProduct(newProduct); //need productId in next step, can manage a state of its own too.
                setActiveStep(1);
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

    const renderNewProductButtons = () => (
        <>
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
        </>
    );

   const renderProceedToImageUploader =
        <>
            <button
                type="button"
                className="btn btn-outline-success"
                onClick={() => setActiveStep(1)}
            >
                Add images
            </button>
            <Link to="/products">
                <button
                    type="button"
                    className="btn btn-outline-danger"
                >
                    Done
                </button>
            </Link>
        </>;
    return (

        <section className={'container my-5'}>
            <div>
                <div>
                    <h4 className={'mb-4'}>Add new product</h4>
                    <div className={`d-flex justify-content-center align-items-center`}>
                        <Stepper
                            activeStep={activeStep}
                            className="m-4"
                        >
                            {STEPS.map((step) => (
                                <Step key={step}>
                                    <StepLabel>{step}</StepLabel>
                                </Step>
                            ))}
                        </Stepper></div>

                    {activeStep === 0
                        && <form onSubmit={handleAddNewProduct}>
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
                            <div className={'my-4 d-flex justify-content-start gap-3'}>
                                {activeStep === 0 && !product.id
                                    ? renderNewProductButtons()
                                    : renderProceedToImageUploader
                                }
                            </div>
                        </form>
                    }

                    {
                        product.id
                        && activeStep === 1
                        && <ImageUploader
                            productId={product.id}
                            setActiveStep={setActiveStep}
                        />
                    }

                    {
                        activeStep === 2 &&
                        <div className="d-flex justify-content-center align-items-center gap-5 mt-5">
                            <Link
                                to={`/products/${product.id}`}
                                className="btn btn-outline-success"
                            >
                                Go to uploaded product
                            </Link>
                            <Link
                                to={`/update-product/${product.id}`}
                                className="btn btn-outline-primary"
                            >
                                Update product
                            </Link>
                            <Link
                                to={`/products/all`}
                                className="btn btn-outline-danger"
                            >
                                Go to all products
                            </Link>
                        </div>
                    }
                </div>
            </div>
        </section>
    )
}
