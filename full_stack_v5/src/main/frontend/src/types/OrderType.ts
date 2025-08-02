import type {Product} from "../store/ProductStore.tsx";

export interface OrderItemType {
    id: number;
    quantity: number;
    price: number;
    productName: string;
    orderItemTotal: number;
}

export interface OrderType {
    id: number;
    orderDate: string;
    shipDate: string | null;
    status: string;
    comments: string | null;
    total: number;
    orderItems: OrderItemType[];
}

export const initialProductState: Product = {
    name: '',
    description: '',
    brand: '',
    categoryName: '',
    price: 0,
    inventory: 0,
    images: [],
}