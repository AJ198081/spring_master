import {createBrowserRouter, createRoutesFromElements, Route, RouterProvider} from "react-router-dom";
import {RootLayout} from "./components/layout/RootLayout.tsx";
import {Home} from "./components/Home.tsx";
import {Products} from "./components/product/Products.tsx";
import {ProductDetails} from "./components/product/ProductDetails.tsx";
import {Cart} from "./components/cart/Cart.tsx";
import {Order} from "./components/order/Order.tsx";
import {AddNewProduct} from "./components/product/AddNewProduct.tsx";

function App() {

    const router = createBrowserRouter(
        createRoutesFromElements(
            <Route
                path="/"
                element={<RootLayout/>}
            >
                <Route
                    index={true}
                    element={<Home/>}
                />
                <Route
                    path={"/products"}
                    element={<Products/>}
                />
                <Route
                    path={"/add-product"}
                    element={<AddNewProduct />}
                    />
                <Route
                    path={"/my-cart"}
                    element={<Cart />}
                />
                <Route
                    path={"/products/:id"}
                    element={<Products />}
                    />
                <Route
                    path={"/products/:productId/details"}
                    element={<ProductDetails />}
                />
                <Route
                    path={"/my-orders"}
                    element={<Order />}
                    />
            </Route>
        )
    );
    return (
        <RouterProvider router={router}/>
    )
}

export default App
