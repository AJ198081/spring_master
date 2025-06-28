import {create} from 'zustand';

const products: Product[] = [
    {
        id: 1,
        name: "Laptop Pro",
        price: 1299.99,
        description: "High-performance laptop for professionals",
        inventory: 2,
        images: [
            {
                downloadUrl: "hero-1",
                fileName: "image",
                contentType: "image/jpg"
            }
        ]
    },
    {
        id: 2,
        name: "Smartphone X",
        price: 799.99,
        description: "Latest smartphone with advanced features",
        inventory: 2,
        images: [
            {
                downloadUrl: "hero-1",
                fileName: "image",
                contentType: "image/jpg"
            }
        ]
    },
    {
        id: 3,
        name: "Wireless Headphones",
        price: 199.99,
        description: "Premium noise-canceling headphones",
        inventory: 2,
        images: [
            {
                downloadUrl: "hero-1",
                fileName: "image",
                contentType: "image/jpg"
            }
        ]
    },
    {
        id: 4,
        name: "Smart Watch",
        price: 299.99,
        description: "Fitness and health tracking smartwatch",
        inventory: 2,
        images: [
            {
                downloadUrl: "hero-1",
                fileName: "image",
                contentType: "image/jpg"
            }
        ]
    },
    {
        id: 5,
        name: "Gaming Console",
        price: 499.99,
        description: "Next-gen gaming console",
        inventory: 2,
        images: [
            {
                downloadUrl: "hero-1",
                fileName: "image",
                contentType: "image/jpg"
            }
        ]
    },
    {
        id: 6,
        name: "Tablet Pro",
        price: 649.99,
        description: "Versatile tablet for work and entertainment",
        inventory: 2,
        images: [
            {
                downloadUrl: "hero-1",
                fileName: "image",
                contentType: "image/jpg"
            }
        ]
    },
    {
        id: 7,
        name: "Wireless Speaker",
        price: 129.99,
        description: "Portable bluetooth speaker",
        inventory: 2,
        images: [
            {
                downloadUrl: "hero-1",
                fileName: "image",
                contentType: "image/jpg"
            }
        ]
    },
    {
        id: 8,
        name: "Digital Camera",
        price: 799.99,
        description: "Professional DSLR camera",
        inventory: 2,
        images: [
            {
                downloadUrl: "hero-1",
                fileName: "image",
                contentType: "image/jpg"
            }
        ]
    },
    {
        id: 9,
        name: "Smart TV",
        price: 899.99,
        description: "4K Smart TV with HDR",
        inventory: 2,
        images: [
            {
                downloadUrl: "hero-1",
                fileName: "image",
                contentType: "image/jpg"
            }
        ]
    },
    {
        id: 10,
        name: "Wireless Mouse",
        price: 59.99,
        description: "Ergonomic wireless mouse",
        inventory: 2,
        images: [
            {
                downloadUrl: "hero-1",
                fileName: "image",
                contentType: "image/jpg"
            }
        ]
    }
];

export interface Product {
    id: number,
    name: string,
    price: number,
    description: string,
    inventory: number,
    images: Image[],
}

export interface Image {
    fileName: string,
    downloadUrl: string,
    contentType: string
}
// This is what you need to declare first up,
// An interface that will contain the state you want to manage, and the setter/update functions of the state

interface ProductStore {
    allProducts: Product[],
    filteredProducts: Product[],
    setAllProducts: (products: Product[]) => void,
    setFiltered: (filteredProducts: Product[]) => void
}

export const useProductStore = create<ProductStore>(set => ({
    allProducts: products,
    filteredProducts: [] as Product[],
    setAllProducts: (products) => set({allProducts: products}),
    setFiltered: (filteredProducts) => set({filteredProducts})
}))
