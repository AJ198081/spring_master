import './App.css'
import {Toaster} from "react-hot-toast";
import 'sweetalert2/themes/material-ui.css'
import Swal, {type SweetAlertOptions} from "sweetalert2";
import {Circle} from "./components/Circle";
import {useState} from "react";
import RectangleImg from "./assets/rectangle.svg";
import {type Product, Products} from "./components/Products.tsx";
import type {components} from "./assets/schema";

const showAlert = (props: SweetAlertOptions | undefined) => {
    return Swal.fire({
        title: 'Default alert!',
        icon: 'success',
        iconColor: '#008000',
        showConfirmButton: true,
        timer: 5000,
        confirmButtonText: 'Ok',
        theme: 'material-ui-dark',
        inputAutoFocus: true,
        ...props
    });
}

interface CartItem {
    product: Product;
    quantity: number;
}

function App() {

    const [clicked, setClicked] = useState(false);
    const [_itemsClicked, setItemsClicked] = useState<components["schemas"]["Product"][]>([]);
    const [_cartItems, setCartItems] = useState<CartItem[]>([]);


    const handleClick = () => {
        setClicked(prev => !prev);
    }

    const onProductClick = (product: components["schemas"]["Product"], operation?: string) => {

        if (operation && operation === 'DELETE') {
            console.log('Product deleted from the cart');
            setCartItems(prev => prev.filter(item => item.product.id !== product.id));
        }

        setItemsClicked(prev => {
            if (prev.some(item => item.id === product.id)) {
                return prev;
            } else {
                return [...prev, product];
            }
        });

        setCartItems(prevState => prevState.map(item => item.product.id === product.id
            ? {
                ...item,
                quantity: item.quantity + 1
            }
            : item));
    };

    return (
        <>
            <div className={"container h-screen w-full bg-gray-100 flex justify-center items-center"}>
                <div className={"m-5 px-5 bg-gray-200 grid grid-cols-3 gap-5 items-center justify-center"}>
                <Circle
                    onClick={() => {
                        void showAlert({
                            icon: 'success',
                            title: 'Hello AJ!!'
                        });
                        handleClick();
                    }}
                    clicked={clicked}
                />
                <img
                    src={RectangleImg}
                    alt={"Rectangle image"}
                    width={500}
                    onClick={() => {
                        console.log('Image clicked!!');
                    }}
                />
                <div className={"flex flex-col gap-5"}>
                    <h1 className={"text-3xl font-bold"}>Welcome to the Shopping Cart</h1>
                    <p className={"text-lg"}>This is a simple shopping cart application built with React and Spring
                                             Boot.</p>
                </div>
            </div>
            </div>

            <Products onClick={onProductClick}/>

            <Toaster/>
        </>
    );
}

export default App
