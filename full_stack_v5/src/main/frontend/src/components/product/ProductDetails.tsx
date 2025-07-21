import {ProductImage} from "./ProductImage.tsx";
import {type Product, useProductStore} from "../../store/ProductStore.tsx";
import {useParams} from "react-router-dom";
import {useEffect, useState} from "react";
import {getProductById} from "../../services/ProductService.ts";
import {CartQuantityUpdater} from "./CartQuantityUpdater.tsx";
import {BsCart} from "react-icons/bs";
import {toast} from "react-toastify";
import {type AddCartItem, addProductToCartItems, getFirstCustomer} from "../../services/CartService.ts";

export const ProductDetails = () => {

    const {productId} = useParams();
    const [product, setProduct] = useState<Product | null>(null);
    const [quantity, setQuantity] = useState(1);
    const allAvailableProducts = useProductStore(state => state.allProducts);
    const updateAllProducts = useProductStore(state => state.setAllProducts);
    const setCartForThisCustomer = useProductStore(state => state.setCartForThisCustomer);
    const setThisCustomerId = useProductStore(state => state.setThisCustomerId);
    const thisCustomerId = useProductStore(state => state.thisCustomerId);

    useEffect(() => {
        if (productId && productId.length > 0) {
            getProductById(Number(productId))
                .then((product) => {
                    if (product) {
                        setProduct(product);
                    }
                })
                .catch((error) => {
                        console.log(error);
                        setProduct({} as Product);
                    }
                )
        }
    }, [productId])

    useEffect(() => {
        getFirstCustomer()
            .then(customer => {
                setThisCustomerId(customer.id);
            })
            .catch(error => {
                toast.error(`Error getting first customer; issue is - ${error.response?.data?.detail}`);
            })
    }, [setThisCustomerId]);

    const inventory = product?.inventory ?? 0;

    const addProductToCart = () => {
        if (inventory <= 0 || quantity > inventory) {
            return;
        }

        updateAllProducts(allAvailableProducts
            .map(product => {
                if (product.id === Number(productId)) {
                    return {
                        ...product,
                        inventory: product.inventory - quantity
                    }
                }
                return product;
            }));

        const cartItemRequest : AddCartItem = {
            customerId: thisCustomerId!,
            productId: Number(productId),
            quantity: quantity
        }

        addProductToCartItems(cartItemRequest)
            .then((updatedCart) => {
                setCartForThisCustomer(updatedCart);
                toast.success(`Product ${product?.name} added to cart successfully`);
            })
            .catch((error) => {
                toast.error(`Error adding product to cart; issue is - ${error.response?.data?.detail}`);
            })
            .finally(() => {
                    setQuantity(0);
                }
            );

    };
    
    return (
        product && <div className={'container'}>
            <div className="row product-details">
                <div className="col-md-2">
                    {
                        product.images.map((image) => (
                            <div
                                key={image.downloadUrl}
                                className={"mt-4 image-container"}
                            >
                                <ProductImage imageDownloadUrl={image.downloadUrl}/>
                            </div>
                        ))
                    }
                </div>
                <div className="col-md-8 details-container">
                    <h1 className={'product-name'}>{product.name}</h1>
                    <h4 className={'price'}>{product.price}</h4>
                    <p className={'product-description'}>{product.description}</p>
                    <p className="product-name">Brand: {product.brand.toLowerCase()}</p>
                    <p className="product-name">
                        Rating: <span className="rating text-danger">4.5</span>
                    </p>
                    <p className={`${inventory >= 5 ? 'text-success' : 'text-danger'}`}>
                        {inventory >= 5
                            ? 'In stock'
                            : inventory === 0
                                ? 'Out of stock'
                                : 'Low stock'}
                    </p>
                    <p>Quantity: {inventory}</p>
                    <CartQuantityUpdater
                        quantity={quantity}
                        setQuantity={setQuantity}
                        maxQuantity={inventory}
                        onDecrease={() => setQuantity(prevState => {
                            return prevState === 0 ? 0 : prevState - 1
                        })}
                        onIncrease={() => setQuantity(prevState => {
                            if (inventory === 0) {
                                toast.error('Out of stock');
                                return prevState;
                            }
                            if (prevState === inventory) {
                                toast.error('Max quantity reached')
                                return prevState;
                            }
                            return prevState === product?.inventory ? prevState : prevState + 1;
                        })
                        }
                    />

                    <div className="d-flex gap-2 mt-3">
                        <button
                            className={`add-to-cart-button ${inventory === 0 ? 'disabled text-bg-warning' : ''}`}
                            onClick={addProductToCart}
                            disabled={inventory === 0}
                        >
                           <BsCart /> Add to cart
                        </button>
                        <button className={"buy-now-button" + (inventory === 0 ? ' text-bg-dark' : '')}>
                            Buy Now
                        </button>
                    </div>
                </div>
            </div>
        </div>
    )
}