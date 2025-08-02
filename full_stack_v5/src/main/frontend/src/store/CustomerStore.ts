import type { CustomerType } from "../types/CustomerType";
import {create} from "zustand";

export interface CustomerStore {
    customer?: CustomerType;
    setCustomer: (customer: CustomerType) => void;
}

export const useCustomerStore = create<CustomerStore>((set) => ({
    customer: undefined,
    setCustomer: (customer: CustomerType) => set({customer: customer}),
}));