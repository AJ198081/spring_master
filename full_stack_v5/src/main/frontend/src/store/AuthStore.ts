import {create} from "zustand";

export interface Authentication {
    isAuthenticated: boolean;
    username: string;
    roles: string[];
    token: string;
    customerId?: number;
}

export const authDefaultValues: Authentication = {
    isAuthenticated: false,
    username: '',
    roles: [],
    token: '',
};

export interface AuthStore {
    authState: Authentication | null;
    setAuthState: (authState: Authentication | null) => void;
    patchAuthState: (authState: Partial<Authentication>) => void;
}

export const useAuthStore = create<AuthStore>((set) => ({
    authState: authDefaultValues,
    setAuthState: (authState) => set({authState}),
    patchAuthState: (stateToUpdate: Partial<Authentication>) => set((prevStoreState) => ({
        authState: {
            ...(prevStoreState.authState ?? authDefaultValues),
            ...stateToUpdate,
        },
    })),
}));
