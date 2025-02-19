import {CurrentDateTime} from "./CurrentDateTime.tsx";
import {currencyFormatter} from "../utils/Formatter.ts";

export interface DashboardStatusProps {
    useName?: string;
    totalExpenses: number;
}

export const DashboardStatus = ({useName = 'AJ Bhandal', totalExpenses}: DashboardStatusProps) => {
    return <div className={'m-3'}>
        <div className={'text-center'}>
            <h5>Total expenses</h5>
            <h3>
                <span className={'badge rounded-pill bg-info-subtle text-dark'}>
                    {currencyFormatter.format(totalExpenses)}
                </span>
            </h3>
        </div>

        <div className={'d-flex justify-content-between'}>
            <div>Welcome, <span className={'text-bg-light fs-5 text-dark-emphasis'}>{useName}</span></div>
            <CurrentDateTime/>
        </div>
    </div>;
}