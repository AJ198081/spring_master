import {createBrowserRouter, createRoutesFromElements, Route, RouterProvider} from "react-router-dom";
import {RootLayout} from "./components/layout/RootLayout.tsx";
import {Home} from "./components/Home.tsx";
import {Products} from "./components/product/Products.tsx";
import {ProductDetails} from "./components/product/ProductDetails.tsx";
import {Cart} from "./components/cart/Cart.tsx";
import {Order} from "./components/order/Order.tsx";
import {AddNewProduct} from "./components/product/AddNewProduct.tsx";
import {UpdateProduct} from "./components/product/UpdateProduct.tsx";
import {ImageUpdater} from "./components/product/ImageUpdater.tsx";
import {UserRegistrationComponent} from "./components/user/UserRegistrationComponent.tsx";
import {CreateCustomerComponent} from "./components/customer/CreateCustomerComponent.tsx";
import {LoginComponent} from "./components/auth/LoginComponent.tsx";
import {LogoutComponent} from "./components/auth/LogoutComponent.tsx";
import {UnauthorizedAccessComponent} from "./components/auth/UnauthorizedAccessComponent.tsx";
import {ProtectedRoute} from "./components/auth/ProtectedRoute.tsx";
import {ResetPasswordComponent} from "./components/auth/ResetPasswordComponent.tsx";
import {UpdateCustomerProfileComponent} from "./components/customer/UpdateCustomerProfileComponent.tsx";
import {ErrorPage} from "./components/common/ErrorPage.tsx";
import {CheckoutComponent} from "./components/checkout/CheckoutComonent.tsx";
import {StripeWrapper} from "./components/checkout/StripeWrapper.tsx";
import {CheckoutSuccess} from "./components/checkout/CheckoutSuccess.tsx";
import {CheckoutFailure} from "./components/checkout/CheckoutFailure.tsx";
import {QueryClientProvider} from "@tanstack/react-query";
import {queryClient} from "./services/Api.ts";
import {Scheduler} from "./components/Scheduler.tsx";
import {AdminDashboard} from "./components/dashboard/AdminDashboard.tsx";
import {useLayoutEffect, useState} from "react";

function App() {
    const [firstRender, setFirstRender] = useState(true);

    useLayoutEffect(() => {
        if (localStorage.getItem('token') === null) {
            console.log(`App.tsx - useLayoutEffect started`);
            localStorage.setItem('token', 'token-set');
            console.log(`App.tsx - useLayoutEffect set token`);
        } else {
            console.log(`App.tsx - useLayoutEffect token already set`);
        }
        setFirstRender(false);
    }, [])

    const router = createBrowserRouter(
        createRoutesFromElements(
            <Route
                path="/"
                element={<RootLayout/>}
                errorElement={<ErrorPage/>}
            >
                <Route
                    index={true}
                    element={<Home/>}
                />
                <Route
                    path={"/oauth2/callback"}
                    element={<AdminDashboard/>}
                />
                <Route
                    path={"/scheduler"}
                    element={<Scheduler/>}
                />
                <Route
                    path={"/register"}
                    element={<UserRegistrationComponent/>}
                />
                <Route
                    path={"/login"}
                    element={<LoginComponent/>}
                />
                <Route
                    path={"/logout"}
                    element={<LogoutComponent/>}
                />
                <Route
                    path={"/unauthorized"}
                    element={<UnauthorizedAccessComponent/>}
                />
                <Route
                    path={"/add-customer"}
                    element={<CreateCustomerComponent/>}
                />
                <Route
                    path={"/products"}
                    element={<Products/>}
                />
                <Route
                    path={"/update-product/:productId"}
                    element={<UpdateProduct/>}
                />

                <Route
                    path={"/reset-password"}
                    element={<ResetPasswordComponent/>}
                />

                <Route
                    path={`/update-product-images/:productId`}
                    element={<ImageUpdater/>}
                />
                <Route
                    element={
                        <ProtectedRoute
                            useOutlet={true}
                            allowedRoles={['ADMIN']}
                        />
                    }
                >
                    <Route
                        path={"/add-product"}
                        element={<AddNewProduct/>}
                    />
                </Route>

                <Route
                    element={
                        <ProtectedRoute
                            useOutlet={true}
                            allowedRoles={['ADMIN', 'USER']}
                        />
                    }
                >
                    <Route
                        path={"/my-cart"}
                        element={<Cart/>}
                    />
                    <Route
                        path={"/my-orders"}
                        element={<Order/>}
                    />
                    <Route
                        path={"/update-profile"}
                        element={<UpdateCustomerProfileComponent/>}
                    />
                    <Route
                        path={"/checkout/:customerId"}
                        element={<StripeWrapper><CheckoutComponent/></StripeWrapper>}
                    />
                    <Route
                        path={"/checkout/success"}
                        element={<CheckoutSuccess/>}
                    />
                    <Route
                        path={"/checkout/failure"}
                        element={<CheckoutFailure/>}
                    />
                </Route>

                <Route
                    element={
                        <ProtectedRoute
                            useOutlet={true}
                            allowedRoles={['ADMIN']}
                        />
                    }
                >
                    <Route
                        path={"/admin-dashboard"}
                        element={<AdminDashboard/>}
                    />
                </Route>
                <Route
                    path={"/products/:id"}
                    element={<Products/>}
                />
                <Route
                    path={"/products/:productId/details"}
                    element={<ProductDetails/>}
                />
            </Route>
        )
    );
    return (
        <QueryClientProvider client={queryClient}>
            {!firstRender &&  <RouterProvider router={router}/>}
        </QueryClientProvider>
    );
}

export default App
