import {ProductImage} from "./ProductImage.tsx";
import {type Product, useProductStore} from "../../store/ProductStore.ts";
import {useLocation, useNavigate, useParams} from "react-router-dom";
import {useEffect, useReducer, useState} from "react";
import {deleteProduct, getProductById, patchProduct} from "../../services/ProductService.ts";
import {CartQuantityUpdater} from "./CartQuantityUpdater.tsx";
import {BsCart} from "react-icons/bs";
import {toast} from "react-toastify";
import {type AddCartItem, addProductToCartItems} from "../../services/CartService.ts";
import {Button, Modal} from "react-bootstrap";
import {useAuthStore} from "../../store/AuthStore.ts";
import {Box, IconButton, Rating, Tooltip, Typography, Zoom} from "@mui/material";
import PlaylistAddCheckIcon from '@mui/icons-material/PlaylistAddCheck';
import PlaylistAddCircleIcon from '@mui/icons-material/PlaylistAddCircle';
import {useMutation} from "@tanstack/react-query";
import {queryClient} from "../../services/Api.ts";
import {AxiosError} from "axios";
import {getObjectPatch} from "../../utils/Utilities.ts";

function getStockLevelMessage(inventory: number) {
    if (inventory === 0) {
        return 'Out of stock';
    } else if (inventory >= 5) {
        return 'In stock';
    } else {
        return 'Low stock';
    }
}

export const ProductDetails = () => {

    const {productId} = useParams();
    const[originalProduct, setOriginalProduct] = useState<Product | null>(null);
    const [product, setProduct] = useState<Product | null>(null);
    const [quantity, setQuantity] = useState(1);
    const [showDeleteModal, setShowDeleteModal] = useState(false);
    const allAvailableProducts = useProductStore(state => state.allProducts);
    const updateAllProducts = useProductStore(state => state.setAllProducts);
    const setCartForThisCustomer = useProductStore(state => state.setCartForThisCustomer);
    const authenticated = useAuthStore(state => state.authState?.isAuthenticated);
    const thisCustomerId = useAuthStore(state => state.authState?.customerId);
    const [ratings, setRatings] = useState(4.5);
    const [addedToPlayList, toggleAddToPlayList] = useReducer(prevState => !prevState, product?.inWishList ?? false);

    const navigate = useNavigate();
    const location = useLocation();

    useEffect(() => {
        if (productId && productId.length > 0) {
            getProductById(Number(productId))
                .then((product) => {
                    if (product) {
                        setProduct(product);
                        setOriginalProduct(product);
                    }
                })
                .catch((error) => {
                        console.log(error);
                        setProduct({} as Product);
                        setOriginalProduct({} as Product);
                    }
                )
        }
    }, [productId]);

    const mutation = useMutation({
        mutationFn: ({ productId, productChanges }: { productId: string, productChanges: Partial<Product> }) =>
            patchProduct(productId, productChanges),
        onSuccess: (_) => {
            toast.success(`Product ${product?.name} added to cart successfully`);
            void queryClient.invalidateQueries({queryKey: ['products']});
        },
        onError: (error) => {
            if (error instanceof AxiosError) {
                toast.error(`Error adding product to cart; issue is - ${error.response?.data?.detail}`);
            } else {
                toast.error('An unknown error occurred while adding the product to cart');
            }
        },
    });

    const inventory = product?.inventory ?? 0;
    const isAddToCartDisabled = authenticated && inventory === 0;

    const handleWishList = () => {
        if (productId && product && originalProduct) {
            const updatedProduct = {...product, inWishList: !addedToPlayList};
            const productChanges = getObjectPatch(updatedProduct, originalProduct) as Partial<Product>;
            console.log(`productChanges ${JSON.stringify(productChanges)}`);
            mutation.mutate({ productId, productChanges });
            setProduct(updatedProduct);
            setOriginalProduct(updatedProduct);
            toggleAddToPlayList();
        }
    }

    const addProductToCart = () => {
        console.log(`thisCustomerId is ${thisCustomerId}`);
        if (!authenticated) {
            navigate('/login', {state: {from: location.pathname}});
        } else if (!thisCustomerId) {
            navigate('/add-customer', {state: {from: location.pathname}});
        } else {

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

            const cartItemRequest: AddCartItem = {
                customerId: thisCustomerId,
                productId: Number(productId),
                quantity: quantity
            };

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
        }

    };

    const handleDelete = () => {
        setShowDeleteModal(true);
    };

    const confirmDelete = async () => {
        if (productId) {
            try {
                await deleteProduct(Number(productId));
                toast.success(`Product ${product?.name} deleted successfully`);
                navigate('/products');
            } catch (error) {
                if (error instanceof Error) {
                    toast.error(`Error deleting product: ${error.message}`);
                } else {
                    toast.error('An unknown error occurred while deleting the product');
                }
            } finally {
                setShowDeleteModal(false);
            }
        }
    };

    return (
        product && <div className={'container'}>
            <div className="row product-details">
                <div className="col-md-2">
                    {
                        product.images?.map((image) => (
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
                    <p className="product-name">Brand: {product.brand?.toLowerCase()}</p>
                    <div className="my-3">
                        <Typography component="legend">Rating</Typography>
                        <Rating
                            name="simple-controlled"
                            value={ratings}
                            onChange={(event, newValue) => {
                                console.log(event.target, newValue);
                                setRatings(newValue!);
                            }}
                        />
                    </div>
                    <Box
                        display="flex"
                        alignItems="flex-end"
                        justifyContent="start"
                        gap={4}
                    >
                        <p className={`${inventory >= 5 ? 'text-success' : 'text-danger'}`}>
                            {getStockLevelMessage(inventory)}
                        </p>
                        <Tooltip
                            title={`${addedToPlayList ? 'Remove from wishlist' : 'Added to wishlist'}`}
                            placement="top"
                            arrow={true}
                            slots={{
                                transition: Zoom
                            }}
                            slotProps={{
                                transition: {
                                    timeout: 400,
                                },
                            }}
                        >
                            <IconButton
                                aria-label="add to wishlist"
                                color={"info"}
                            >
                                {addedToPlayList ? <PlaylistAddCheckIcon
                                    fontSize={"large"}
                                    onClick={handleWishList}
                                /> : <PlaylistAddCircleIcon
                                    fontSize="large"
                                    onClick={handleWishList}
                                />}
                            </IconButton>
                        </Tooltip>
                    </Box>
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
                            type="button"
                            className={`add-to-cart-button ${isAddToCartDisabled ? 'disabled text-bg-warning' : ''}`}
                            onClick={addProductToCart}
                            disabled={isAddToCartDisabled}
                            data-bs-placement="top"
                            data-bs-delay={0}
                            data-bs-toggle="tooltip"
                            title={isAddToCartDisabled ? 'You need to be logged in to add product to cart' : ''}
                        >
                            <BsCart/> Add to cart
                        </button>

                        <button className={"buy-now-button" + (inventory === 0 ? ' text-bg-dark' : '')}>
                            Buy Now
                        </button>
                        <button
                            className={"btn btn-outline-success rounded-pill"}
                            onClick={() => navigate(`/update-product/${product.id}`)}
                        >
                            Update Product
                        </button>
                        <button
                            className={"btn btn-outline-danger rounded-pill"}
                            onClick={handleDelete}
                        >
                            Delete Product
                        </button>
                    </div>
                </div>
            </div>

            <Modal
                show={showDeleteModal}
                onHide={() => setShowDeleteModal(false)}
            >
                <Modal.Header closeButton>
                    <Modal.Title>Delete Product</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    Are you sure you want to delete the product <b><i>{product.name}</i></b>?
                </Modal.Body>
                <Modal.Footer>
                    <Button
                        variant="secondary"
                        onClick={() => setShowDeleteModal(false)}
                    >
                        Cancel
                    </Button>
                    <Button
                        variant="danger"
                        onClick={confirmDelete}
                    >
                        Delete
                    </Button>
                </Modal.Footer>
            </Modal>
        </div>
    )
}
