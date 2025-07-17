interface ImageType {
    id: number;
    fileName: string;
    contentType: string;
    downloadUrl: string;
    contents?: string;
}

interface CategoryType {
    id: number;
    name: string;
}

interface ProductType {
    id: number;
    name: string;
    description: string;
    price: number;
    inventory: number;
    brand: BrandType;
    category: CategoryType;
    images: ImageType[];
}

interface BrandType {
    id: number;
    name: string;
}

export interface CartItemType {
    id: number;
    quantity: number;
    unitPrice: number;
    total: number;
    product: ProductType;
}

interface UserTypes {
    id: number;
    username: string;
    password: string;
    roles: string[];
}

interface CustomerType {
    id: number;
    firstName: string;
    lastName: string;
    email: string;
    phone: string;
    address: string;
    user: UserTypes;
    orders: unknown[];
}

export interface CartType {
    id: number;
    total: number;
    customer: CustomerType;
    cartItems: CartItemType[];
}