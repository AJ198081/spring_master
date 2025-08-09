import type { CustomerType } from "../types/CustomerType";
import {create} from "zustand";

export interface CustomerStore {
    customer: CustomerType | null;
    setCustomer: (customer: CustomerType | null) => void;
}

export const useCustomerStore = create<CustomerStore>((set) => ({
    customer: null,
    setCustomer: (customer: CustomerType | null) => set({customer: customer}),
}));