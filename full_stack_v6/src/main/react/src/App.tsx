import './App.css'
import {Toaster} from "react-hot-toast";
import 'sweetalert2/themes/material-ui.css'
import Swal, {type SweetAlertOptions} from "sweetalert2";
import {Circle} from "./components/Circle";
import {useState} from "react";
import RectangleImg from "./assets/rectangle.svg";
import {Products} from "./components/Products.tsx";
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

function App() {

    const [clicked, setClicked] = useState(false);
    const [itemsClicked, setItemsClicked] = useState<number>(0);


    const handleClick = () => {
        setClicked(prev => !prev);
    }

    let clickedProduct: components["schemas"]["Product"] | undefined;

    const onProductClick = (product: components["schemas"]["Product"]) => {
        console.log('Total items clicked:', itemsClicked);

        clickedProduct = product;
        setItemsClicked(itemsClicked + 1);
        setItemsClicked(itemsClicked + 1);
        setItemsClicked(itemsClicked + 1);
        setItemsClicked(itemsClicked + 1);
    };

    console.log(`Clicked product: ${clickedProduct?.name}`);

    return (
        <>
            <div className={"container h-screen w-full bg-gray-100 flex justify-center items-center"}>
                <div className={"m-5 px-5 bg-gray-200 grid grid-cols-4 gap-5 items-center justify-center"}>
                <Circle
                    onClick={() => {
                        void showAlert({
                            icon: 'success',
                            title: 'Hello Reet!!'
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
