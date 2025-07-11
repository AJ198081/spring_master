import {createBrowserRouter, createRoutesFromElements, Route, RouterProvider} from "react-router-dom";
import {RootLayout} from "./components/layout/RootLayout.tsx";
import {Home} from "./components/Home.tsx";
import {Products} from "./components/product/Products.tsx";
import {ProductDetails} from "./components/product/ProductDetails.tsx";

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
                {/*<Route
                    path={"/products/all"}
                    element={<Products />}
                />*/}
                <Route
                    path={"/products/:id"}
                    element={<Products />}
                    />
                <Route
                    path={"/products/:productId/details"}
                    element={<ProductDetails />}
                />
            </Route>
        )
    );
    return (
        <RouterProvider router={router}/>
    )
}

export default App
