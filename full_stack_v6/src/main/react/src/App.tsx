import './App.css'
// import {Products} from "./components/Products.tsx";
import {ProductCard} from "./components/ProductCard.tsx";
import {Toaster} from "react-hot-toast";

function App() {

    return (
        <div className={"container h-screen w-full bg-gray-100 flex justify-center items-center"}>
            <div className={"m-5 px-5 bg-gray-200 grid grid-cols-2 gap-5"}>
                {/*<Products/>*/}
                <ProductCard />
                <ProductCard />
                <ProductCard />
            </div>

            <Toaster />
        </div>
    )
}

export default App
