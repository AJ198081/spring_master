import type {OrderType} from "./OrderType.ts";

export interface CustomerType {
  id?: number;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  billingAddress: AddressType;
  shippingAddress: AddressType;
  username?: string;
  user?: UserType;
  orders?: OrderType[];
}

export interface AddressType {
  addressLine1: string;
  addressLine2: string;
  city: string;
  state: AustralianState;
  postalCode: string;
  country: string;
}

export type AustralianState = 'NSW' | 'VIC' | 'QLD' | 'WA' | 'SA' | 'TAS' | 'ACT' | 'NT';


export interface UserType {
  id: number;
  username: string;
  password: string;
  roles: RoleType[];
}

export interface RoleType {
  id: number;
  name: string;
}
