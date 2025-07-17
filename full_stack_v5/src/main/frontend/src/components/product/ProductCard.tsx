import {Card} from "react-bootstrap";
import {Link} from "react-router-dom";
import {ProductImage} from "./ProductImage.tsx";
import {type Product} from "../../store/ProductStore.tsx";

export interface ProductCardProps {
    productsToDisplay: Product[]
}

export const ProductCard = ({productsToDisplay}: ProductCardProps) => {

    return (
        <main className={"row m-2"}>
            {productsToDisplay.length > 0 && productsToDisplay
                .map((product) => (
                    <div
                        className={"col-12 col-sm-6 col-md-4 col-lg-2 mt-4 d-flex"}
                        key={product.id}
                    >
                        <Card className={"mb-2 mt-2 h-100"}>
                            <Link
                                to={`/products/${product.id}/details`}
                                className={'link'}
                            >
                                <div className={"image-container"}>
                                    {product.images.length > 0
                                        && <ProductImage
                                            key={product.images[0].downloadUrl}
                                            imageDownloadUrl={product.images[0].downloadUrl}
                                        />}
                                </div>
                            </Link>
                            <Card.Body>
                                <p className={"product-description overflow-hidden"}>
                                    {product.name
                                        .concat(' - ')
                                        .concat(product.description)}
                                </p>
                                <h4 className={"price mt-2"}>${product.price}</h4>
                                <p className={`${product.inventory > 5 ? 'text-success' : 'text-danger'}`}>{product.inventory} in stock</p>
                                <p>{product.categoryName}</p>
                                <div className={"d-flex gap-2 justify-content-center mt-auto"}>
                                    <button className={"shop-now-button"}>Add to cart</button>
                                </div>
                            </Card.Body>
                        </Card>
                    </div>
                ))
            }
        </main>
    )
}
