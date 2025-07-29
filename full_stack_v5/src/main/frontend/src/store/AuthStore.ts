import {create} from "zustand";

export interface Authentication {
    isAuthenticated: boolean;
    username: string;
    roles: string[];
    token: string;
}

export interface AuthStore {
    authState: Authentication | null;
    setAuthState: (authState: Authentication | null) => void;
}

export const useAuthStore = create<AuthStore>((set) => ({
        authState: {
            isAuthenticated: false,
            username: '',
            roles: [],
            token: ''
        },
        
        setAuthState: (authState) => set({authState: authState})
    }
));