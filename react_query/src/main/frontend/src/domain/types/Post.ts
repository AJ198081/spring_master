import {User} from "./User.ts";

export interface Post {
    id: number;
    title: string;
    body: string;
    user: User
}