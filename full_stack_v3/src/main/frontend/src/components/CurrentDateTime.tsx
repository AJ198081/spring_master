import dayjs from "dayjs";
import utc from "dayjs/plugin/utc"
import {useEffect, useState} from "react";
// import {dateFormat} from "../domain/Types.ts";
import {dateFormatter} from "../utils/Formatter.ts";

dayjs.extend(utc);

export const CurrentDateTime = () => {

    const [now, setNow] = useState<string>('');

    useEffect(() => {
        const interval = setInterval(() => {
            // setNow(dayjs().utc(false).format(`ddd, ${dateFormat} HH:mm:ss`));
            setNow(dateFormatter.format(dayjs().utc(false).toDate()));
        }, 1000);

        return () => clearInterval(interval);
    }, [])

    return <div className={'text-dark-emphasis'}>{now}</div>;
}
