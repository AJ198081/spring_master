export interface UserRegistrationDto {
  username: string;
  password: string;
  roles: Set<string>;
}

export interface UserResponseDto {
  username: string;
  roles: Set<string>;
}
