import axios, {AxiosResponse} from "axios";
import {Post} from "../domain/types/Post.ts";

export const AxiosInstance = axios.create({
    baseURL: `${import.meta.env.VITE_POST_API_BASE_URL}`,
    headers: {
        "Content-Type": "application/json",
        "Accept": "application/json"
    },
    timeout: 2 * 60 * 1000,
});

export async function fetchAllPosts() {
    const response = await AxiosInstance.get(`/posts`) as AxiosResponse<Post[]>;
    return response.data;
}

export async function fetchPostsCount() {
    const response = await AxiosInstance.get(`/posts/count`) as AxiosResponse<number>;
    return response.data;
}

export async function fetchPosts(pageNumber: number) {
    const response = await AxiosInstance.get(`/posts?page=${pageNumber}`) as AxiosResponse<Post>;
    return response.data;
}

export async function fetchPost(id: number) {
    const response = await AxiosInstance.get(`/posts/${id}`) as AxiosResponse<Post>;
    return response.data;
}

export async function createPost(post: any) {
    const response = await AxiosInstance.post(`/posts`, post) as AxiosResponse<Post>;
    return response.data;
}

export async function updatePost(id: number, post: any) {
    const response = await AxiosInstance.put(`/posts/${id}`, post) as AxiosResponse<Post>;
    return response.data;
}

export async function deletePost(id: number) {
    const response = await AxiosInstance.delete(`/posts/${id}`) as AxiosResponse<Post>;
    return response.data;
}