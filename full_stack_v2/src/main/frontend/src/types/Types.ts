export interface SecurityUser {
    id: number;
    username: string;
    password?: string; // Optional since it may be null after eraseCredentials
    authorities: string[];
    twoFactorSecret?: string | null; // Optional or null
    isTwoFactorEnabled: boolean;
    signUpMethod: string;
    auditMetaData: {
            createdDate: string; // Assuming ISO string format
            lastModifiedDate: string; // Assuming ISO string format
            createdBy: string;
            lastModifiedBy: string;
        };
    enabled: boolean;
    accountNonExpired: boolean;
    accountNonLocked: boolean;
    credentialsNonExpired: boolean;
}
