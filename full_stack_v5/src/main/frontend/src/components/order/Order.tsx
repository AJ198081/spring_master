import {toast} from "react-toastify";
import {useProductStore} from "../../store/ProductStore.tsx";
import {useEffect} from "react";
import {getOrdersForCustomer} from "../../services/OrderService.ts";
import type {OrderItemType, OrderType} from "../../types/OrderType.ts";
import dayjs from "dayjs";

export const Order = () => {

    const thisCustomerOrders = useProductStore(state => state.thisCustomerOrders);
    const setThisCustomerOrders = useProductStore(state => state.setThisCustomerOrders);
    const thisCustomerId = useProductStore(state => state.thisCustomerId);

    useEffect(() => {
        getOrdersForCustomer(thisCustomerId!)
            .then(orders => {
                setThisCustomerOrders(orders);
            })
            .catch(error => {
                toast.error(`Exception ${error.response?.data?.data}`)
            });

    }, [setThisCustomerOrders, thisCustomerId]);

    const renderOrderItems = (orderItems: OrderItemType[]) => {
        return (
            <table className={'table table-striped table-borderless table-hover align-middle text-center'}>
                <thead>
                <tr>
                    <th>Product Name</th>
                    <th>Unit Price</th>
                    <th>Quantity</th>
                    <th>Total</th>
                </tr>
                </thead>
                <tbody>
                {orderItems.map(orderItem => (
                    <tr key={orderItem.id} className={'text-center'}>
                        <td>{orderItem.productName}</td>
                        <td>{orderItem.price.toFixed(2)}</td>
                        <td>{orderItem.quantity}</td>
                        <td>{orderItem.orderItemTotal}</td>
                    </tr>
                ))}
                </tbody>
            </table>
        )

    };

    const renderOrders = (orders: OrderType[]) => {
        return (
            <table className={'table table-striped table-borderless table-hover caption-top align-middle'}>
                <thead>
                <tr>
                    <th>Order Id</th>
                    <th>Order Date</th>
                    <th>Total</th>
                    <th>Status</th>
                    <th className={'text-center'}>Items</th>
                </tr>
                </thead>
                <tbody>
                {
                    orders.map(order => (
                        <tr key={order.id}>
                            <td className={'text-center'}>{order.id}</td>
                            <td>{dayjs(order.orderDate).format("HH:mm @ DD MMM, YYYY")}</td>
                            <td>{order.total}</td>
                            <td>{order.status}</td>
                            <td>{renderOrderItems(order.orderItems)}</td>
                        </tr>
                    ))
                }
                </tbody>
            </table>
        )

    };

    return (
        <div className={'container mt-5'}>
            <div className={'row'}>
                <div className="col-4">
                    <h4 className={'mb-4'}>My Order History</h4>
                </div>
            </div>
            {
                thisCustomerOrders.length === 0
                ? <div className={'text-center'}> No orders found at the moment.</div>
                    : renderOrders(thisCustomerOrders)
            }
        </div>
    );
}