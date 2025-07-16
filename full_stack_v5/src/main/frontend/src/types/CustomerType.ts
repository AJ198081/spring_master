import type {OrderType} from "./OrderType.ts";

export interface CustomerType {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  address: string;
  user: UserType;
  orders: OrderType[];
}

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
