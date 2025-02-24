import {MRT_ColumnDef} from "mantine-react-table";
import {currencyFormatter} from "../utils/Formatter.ts";
import {date, number, object, ref, string} from "yup";
import dayjs from "dayjs";
import {JwtPayload} from "jwt-decode";


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

export const expenseSchemaValidations = object({
    name: string()
        .min(2, 'Expense name can not be less than 2 character')
        .max(255, 'Expense name can not be more than 255 character')
        .required('Expense name is required'),

    note: string()
        .nullable(),

    category: string()
        .required('Category is required'),

    amount: number()
        .required('Expense amount is required'),

    date: date()
        .default(() => dayjs().toDate())
        .max(dayjs().add(1, 'year').toDate(), 'Expense date can not be more than a year in future')
        .required('Expense Date is required')
});

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
        .min(2, 'First name can not be less than 2 character')
        .max(50, 'First name can not be more than 50 character')
        .required('First name is required'),

    lastname: string()
        .min(2, 'Last name can not be less than 2 character')
        .max(50, 'Last name can not be more than 50 character')
        .required('Last name is required'),

    email: string()
        .email('Email is not valid')
        .required('Email is required'),

    username: string()
        .min(5, 'Username can not be less than 5 character')
        .max(50, 'Username can not be more than 50 character')
        .required('Username is required'),

    password: string()
        .required('Password is required')
        .min(8, 'Password must be at least 8 characters long')
        .max(50, 'Password must be at most 50 characters long')
        .matches(/[a-z]/, 'Password must contain at least one lowercase letter')
        .matches(/[A-Z]/, 'Password must contain at least one uppercase letter')
        .matches(/[0-9]/, 'Password must contain at least one number')
        .matches(/[!@#$%^&*]/,
            'Password must contain at least one special (!@#$%^&*) character'
        ),

    confirmpassword: string()
        .oneOf([ref('password')], 'Passwords must match')
        .required('Confirm password is required'),
});

export const initialUserRegistrationRequest: UserRegistrationRequest = {
    firstname: '',
    lastname: '',
    email: '',
    username: '',
    password: '',
    confirmpassword: ''
};

export interface UserLoginRequest {
    username: string;
    password: string;
}

export interface UserLoginResponse {
    token: string;
}

export const initialUserLoginRequest: UserLoginRequest = {
    username: '',
    password: ''
}

export const UserLoginRequestSchemaValidation = object({
    username: string()
        .required('Username is required')
        .min(2, 'Usernames are usually at least 2 character'),
    password: string()
        .required('Password is required')
        .min(8, 'Passwords are at least 8 characters long')
        .max(50, 'Passwords are at most 50 characters long')
        .matches(/[a-z]/, 'Passwords contain at least one lowercase letter')
        .matches(/[A-Z]/, 'Passwords contain at least one uppercase letter')
        .matches(/[0-9]/, 'Passwords contain at least one number')
        .matches(
            /[!@#$%^&*]/,
            'Password usually contain at least one special (!@#$%^&*) character'
        )
})

export interface CustomJwtPayload extends JwtPayload {
    lastName: string;
    firstName: string;
    email: string;
    username: string;
    roles: string;
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
        filterVariant: "text",
        sortingFn: "textCaseSensitive",
    },
    {
        accessorKey: 'date',
        header: 'Date',
        filterVariant: "date-range",
        sortingFn: "datetime",
        enableColumnFilterModes: false,
        Cell: ({cell}) => {
            return cell.getValue<string>();
        }
    },
    {
        accessorKey: 'amount',
        header: 'Amount',
        filterVariant: "range",
        sortingFn: "currency",
        Cell: ({cell}) => {
            return currencyFormatter.format(cell.getValue<number>());
        }
    }
];