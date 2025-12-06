import {MRT_ColumnDef} from "mantine-react-table";
import {currencyFormatter} from "../utils/Formatter.ts";
import {date, number, object, ref, string} from "yup";
import dayjs from "dayjs";
import {jwtDecode, JwtPayload} from "jwt-decode";

export const dateFormat = 'YYYY-MM-DD';

export interface ExpenseRequest {
    name: string;
    note: string;
    category: string;
    date: string;
    amount: number;
}

export interface ExpenseResponse extends Omit<ExpenseRequest, 'date'>{
    expenseId: string;
    date: string;
}

export const categoryOptions = [
    'Food',
    'Transportation',
    'Entertainment',
    'Shopping',
    'Health',
    'Other'
];

export const expenseSchemaValidations = object(
    {
    name: string()
        .required('Expense name is required')
        .min(2, 'Expense name cannot be less than character 2')
        .max(255, 'Expense name cannot be more than character 255'),

    note: string()
        .nullable(),

    category: string()
        .required('Category is required'),

    amount: number()
        .required('Expense amount is required'),

    date: date()
        .default(() => dayjs().toDate())
        .max(dayjs().add(2, 'year').toDate(), 'Expense date cannot be more than a year in the future')
        .required('Expense Date is required')
    }
);

export interface UserRegistrationRequest {
    firstname: string;
    lastname: string;
    email: string;
    username: string;
    password: string;
    confirmpassword: string;
}

export interface UserRegistrationResponse extends Omit<UserRegistrationRequest, 'password' | 'confirmpassword'> {
    userId: string;
}

export const UserRegistrationRequestSchemaValidations = object({
    firstname: string()
        .min(2, 'The first name cannot be less than two characters')
        .max(50, 'The first name cannot be more than 50 characters')
        .required('The first name is required'),

    lastname: string()
        .min(2, 'Last name cannot be less than two characters')
        .max(50, 'Last name cannot be more than 50 characters')
        .required('Last name is required'),

    email: string()
        .email('Email is not valid')
        .required('Email is required'),

    username: string()
        .min(5, 'Username cannot be less than five characters')
        .max(50, 'Username cannot be more than 50 characters')
        .required('Username is required'),

    password: string()
        .required('Password is required')
        .min(8, 'Password must be at least eight characters long')
        .max(50, 'Password must be at most 50 characters long')
        .matches(/[a-z]/, 'Password must contain at least one lowercase letter')
        .matches(/[A-Z]/, 'Password must contain at least one uppercase letter')
        .matches(/[0-9]/, 'Password must contain at least one number')
        .matches(/[!@#$%^&*]/,
            'Password must contain at least one special (!@#$%^&*) character'
        ),

    confirmpassword: string()
        .required('Confirm password is required')
        .oneOf([ref('password')], 'Passwords must match'),
});

export const defaultUserRegistrationRequest: UserRegistrationRequest = {
    firstname: 'AJ',
    lastname: 'Bhandal',
    email: 'abg@gmail.com.au',
    username: 'aj_user_admin',
    password: 'Password@1',
    confirmpassword: 'Password@1'
};

export interface UserLoginRequest {
    username: string;
    password: string;
}

export interface UserLoginResponse {
    token: string;
}

export const initialUserLoginRequest: UserLoginRequest = {
    username: 'aj_admin',
    password: 'Password@1'
}

export type ProblemDetail = {
    type: string;
    title: string;
    status: number;
    detail: string;
    instance: string;
}

export const UserLoginRequestSchemaValidation = object({
    username: string()
        .required('Username is required')
        .min(2, 'Usernames are usually at least character 2'),
    password: string()
        .required('Password is required')
        .min(8, 'Passwords are at least eight characters long')
        .max(50, 'Passwords are at most 50 characters long')
        .matches(/[a-z]/, 'Passwords contain at least one lowercase letter')
        .matches(/[A-Z]/, 'Passwords contain at least one uppercase letter')
        .matches(/[0-9]/, 'Passwords contain at least one number')
        .matches(
            /[!@#$%^&*]/,
            'Password usually contains at least one special (!@#$%^&*) character'
        )
})

export interface CustomJwtPayload extends JwtPayload {
    lastName: string;
    firstName: string;
    email: string;
    username: string;
    roles: string;
}

export const isJwtValid = (token: string | null): boolean => {
    if (token !== null) {
        const jwtPayload = jwtDecode<CustomJwtPayload>(token);
        return jwtPayload.exp !== undefined && (jwtPayload.exp > Date.now() / 1000);
    } else {
        return false;
    }
}

export const columnsDescription: MRT_ColumnDef<ExpenseResponse>[] = [
    {
        accessorKey: 'expenseId',
        header: 'ID',
    },
    {
        accessorKey: 'name',
        header: 'Name'
    },
    {
        accessorKey: 'note',
        header: 'Note / Description '
    },
    {
        accessorKey: 'category',
        header: 'Category',
        filterVariant: "multi-select",
        sortingFn: "textCaseSensitive",
    },
    {
        accessorKey: 'date',
        header: 'Date',
        filterVariant: "date-range",
        sortingFn: "datetime",
        enableColumnFilterModes: true,
        Cell: ({cell}) => {
            return cell.getValue<string>();
        }
    },
    {
        accessorKey: 'amount',
        header: 'Amount',
        filterVariant: "range-slider",
        filterFn: "betweenInclusive",
        sortingFn: "currency",
        enableColumnFilterModes: true,
        Cell: ({cell}) => {
            return currencyFormatter.format(cell.getValue<number>());
        }
    }
];