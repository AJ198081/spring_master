import { Link } from 'react-router-dom';
import { FaTimesCircle, FaShoppingCart, FaStore } from 'react-icons/fa';

export const CheckoutFailure = () => {
    return (
        <div className="container mt-5">
            <div className="row justify-content-center">
                <div className="col-md-8">
                    <div className="card text-center">
                        <div className="card-header bg-danger text-white">
                            <h3 className="mb-0">Payment Failed</h3>
                        </div>
                        <div className="card-body py-5">
                            <div className="mb-4">
                                <FaTimesCircle size={80} className="text-danger mb-3" />
                                <h4>We couldn't process your payment</h4>
                                <p className="lead">
                                    There was an issue processing your payment. Your card has not been charged.
                                </p>
                                <p>
                                    Please check your payment details and try again, or choose another payment method.
                                </p>
                            </div>
                            
                            <div className="mt-5 d-flex justify-content-center gap-3">
                                <Link to="/my-cart" className="btn btn-primary btn-lg">
                                    <FaShoppingCart className="me-2" />
                                    Return to Cart
                                </Link>
                                <Link to="/products" className="btn btn-outline-primary btn-lg">
                                    <FaStore className="me-2" />
                                    Continue Shopping
                                </Link>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};