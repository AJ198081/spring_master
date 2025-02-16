import dayjs from "dayjs";
import utc from "dayjs/plugin/utc"
import {useEffect, useState} from "react";

dayjs.extend(utc);

export const CurrentDateTime = () => {

    const [now, setNow] = useState<string>('');

    useEffect(() => {
        const interval = setInterval(() => {
            setNow(dayjs().utc(false).format('dddd, DD/MM/YYYY HH:mm:ss'));
        }, 1000);

        return () => clearInterval(interval);
    }, [])

    return <div>{now}</div>;
}
