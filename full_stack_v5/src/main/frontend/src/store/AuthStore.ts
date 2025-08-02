import {create} from "zustand";

export interface Authentication {
    isAuthenticated: boolean;
    username: string;
    roles: string[];
    token: string;
    customerId?: number;
}

export const clearSessionAuthentication = {
    isAuthenticated: false,
    username: '',
    roles: [],
    token: '',
}

export interface AuthStore {
    authState: Authentication | null;
    setAuthState: (authState: Authentication | null) => void;
}

export const useAuthStore = create<AuthStore>((set) => ({
        authState: clearSessionAuthentication,
        
        setAuthState: (authState) => set({authState: authState})
    }
));