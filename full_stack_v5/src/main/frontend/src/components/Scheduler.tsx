import {useEffect, useMemo, useState} from "react";
import {
    Button,
    Dialog,
    DialogActions,
    DialogContent,
    DialogContentText,
    DialogTitle,
    TextField,
    Typography
} from "@mui/material";
import {toast} from "react-toastify";
import {useNavigate} from "react-router-dom";

export type ScheduleMode = "DAILY" | "WEEKLY" | "MONTHLY" | "CRON";

export interface TimeOfDay {
    hour24: number;
    minute: number;
}

export interface WeeklySchedule {
    days: number[];
}

export interface MonthlySchedule {
    dayOfMonth: number;
}

export interface SchedulerValue {
    mode: ScheduleMode;
    time: TimeOfDay;
    weekly?: WeeklySchedule;
    monthly?: MonthlySchedule;
    cron?: string;
}

export interface SchedulerProps {
    value?: Partial<SchedulerValue>;
    onChange?: (value: SchedulerValue) => void;
    onDone?: (value: SchedulerValue) => void;
    onCancel?: () => void;
    title?: string;
}

const defaultWeeklyDays = [0];

function hours24ToHour12(hour24: number) {
    const ampm = hour24 >= 12
        ? "pm"
        : "am";

    const hour12 = hour24 % 12 === 0
        ? 12
        : hour24 % 12;

    const minute = 0;
    return {hour12, minute, ampm};
}

function fromHour12(hour12: number, ampm: "am" | "pm"): number {
    const h = hour12 % 12;
    return ampm === "pm" ? h + 12 : h;
}

function boundANumber(n: number, min: number, max: number) {
    return Math.max(min, Math.min(max, n));
}

const allDays = [
    {key: 0, label: "Sunday"},
    {key: 1, label: "Monday"},
    {key: 2, label: "Tuesday"},
    {key: 3, label: "Wednesday"},
    {key: 4, label: "Thursday"},
    {key: 5, label: "Friday"},
    {key: 6, label: "Saturday"}
];

const MODE_OPTIONS: { k: ScheduleMode; t: string }[] = [
    {k: "DAILY", t: "Daily"},
    {k: "WEEKLY", t: "Weekly"},
    {k: "MONTHLY", t: "Monthly"},
    {k: "CRON", t: "Cron expression"},
];

function pad2(n: number) {
    return n.toString().padStart(2, "0");
}

function sortNumbersArray(arr: number[]) {
    return arr.sort((a, b) => a - b);
}

function buildCron(value: SchedulerValue) {
    const m = value.time.minute;
    const h = value.time.hour24;
    if (value.mode === "CRON" && value.cron) {
        return {cron5: value.cron};
    }
    if (value.mode === "DAILY") {
        const cron5 = `${m} ${h} * * *`;
        return {cron5};
    }
    if (value.mode === "WEEKLY" && value.weekly) {
        const days = value.weekly.days.length ? value.weekly.days : defaultWeeklyDays;

        const dowNames = sortNumbersArray(days)
            .map(d => ["SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"][d])
            .join(",");
        const cron5 = `${m} ${h} * * ${dowNames}`;
        return {cron5};
    }
    if (value.mode === "MONTHLY" && value.monthly) {
        const dom = boundANumber(value.monthly.dayOfMonth, 1, 31);
        const cron5 = `${m} ${h} ${dom} * *`;
        return {cron5};
    }
    const cron5 = `${m} ${h} * * *`;
    return {cron5};
}

const cronToText = (cronExpression: string) => {
        const [minute, hour, dayMonth, dayWeek] = cronExpression.split(' ');
        let explanation = ' ';

        if (dayWeek !== '*') {
            const days = dayWeek.split(',')
                .map(d => allDays.find(day => day.key === parseInt(d) ||
                    ["SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"][day.key] === d)?.label || d)
                .join(', ');
            explanation += `every ${days} `;
        } else if (dayMonth !== '*') {
            explanation += `on day ${dayMonth} of each month `;
        } else {
            explanation += 'every day ';
        }

        const hourNum = parseInt(hour);
        const minuteNum = parseInt(minute);
        const timeStr = `${hourNum % 12 || 12}:${minuteNum.toString().padStart(2, '0')} ${hourNum >= 12 ? 'PM' : 'AM'}`;
        explanation += `at ${timeStr}`;

        return explanation;
    };

export function Scheduler(props: Readonly<SchedulerProps>) {
    const initialHour24 = typeof props.value?.time?.hour24 === "number" ? props.value.time.hour24 : 12;
    const initialMinute = typeof props.value?.time?.minute === "number" ? props.value.time.minute : 0;

    const [mode, setMode] = useState<ScheduleMode>(props.value?.mode ?? "WEEKLY");
    const [hour12, setHour12] = useState<number>(hours24ToHour12(initialHour24).hour12);
    const [minute, setMinute] = useState<number>(initialMinute);
    const [amPm, setAmPm] = useState<"am" | "pm">(hours24ToHour12(initialHour24).ampm as "am" | "pm");

    const [weeklyDays, setWeeklyDays] = useState<number[]>(props.value?.weekly?.days ?? defaultWeeklyDays);
    const [monthlyDay, setMonthlyDay] = useState<number>(props.value?.monthly?.dayOfMonth ?? 1);
    const [cronText, setCronText] = useState<string>(props.value?.cron ?? "");

    const navigateTo = useNavigate();

    const [openCronDialog, setOpenCronDialog] = useState(false);

    const time: TimeOfDay = useMemo(() => ({
        hour24: fromHour12(hour12, amPm),
        minute
    }), [hour12, amPm, minute]);

    const value: SchedulerValue = useMemo(() => ({
        mode,
        time,
        weekly: mode === "WEEKLY" ? {days: weeklyDays} : undefined,
        monthly: mode === "MONTHLY" ? {dayOfMonth: monthlyDay} : undefined,
        cron: mode === "CRON" ? cronText : undefined
    }), [mode, time, weeklyDays, monthlyDay, cronText]);

    const {cron5} = useMemo(() => buildCron(value), [value]);

    useEffect(() => {
        props.onChange?.({...value, cron: cron5});
    }, [value, cron5, props]);

    const toggleDay = (d: number) => {
        setWeeklyDays(prev => prev.includes(d) ? prev.filter(x => x !== d) : [...prev, d]);
    };

    const isWeeklyInvalid = mode === "WEEKLY" && weeklyDays.length === 0;

    const handleDone = () => {
        const finalValue: SchedulerValue = {...value, cron: cron5};
        props.onDone?.(finalValue);
        toast.success(`Your report is scheduled to run ${cronToText(cron5)}.`);
        console.log(finalValue);
        navigateTo("/");
    };

    return (
        <div
            className="container p-3"
            style={{maxWidth: 640}}
        >
            <Typography
                variant="h5"
                className="my-5"
            >
                {props.title ?? "Build schedule for your report"}
            </Typography>

            <div className="mb-3 d-flex gap-3">
                {MODE_OPTIONS.map((opt) => (
                    <label
                        key={opt.k}
                        className="form-check form-check-inline"
                    >
                        <input
                            type="radio"
                            className="form-check-input"
                            name="sched-mode"
                            checked={mode === opt.k}
                            onChange={() => setMode(opt.k)}
                        />
                        <span className="form-check-label">{opt.t}</span>
                    </label>
                ))}
            </div>

            <hr className="my-4"/>

            {mode === "WEEKLY" && (
                <div className="mb-3">
                    <div className="row row-cols-2 row-cols-sm-2 row-cols-md-3 g-2 mb-3">
                        {allDays.map(d => (
                            <div
                                key={d.key}
                                className="col"
                            >
                                <label className="form-check">
                                    <input
                                        type="checkbox"
                                        className="form-check-input"
                                        checked={weeklyDays.includes(d.key)}
                                        onChange={() => toggleDay(d.key)}
                                    />
                                    <span className="form-check-label">{d.label}</span>
                                </label>
                            </div>
                        ))}
                    </div>
                </div>
            )}

            {mode === "DAILY" && (
                <div className="mb-3">
                    <small className="text-muted">Runs every day at the selected time.</small>
                </div>
            )}

            {mode === "MONTHLY" && (
                <div className="mb-3 d-flex align-items-center gap-2">
                    <label
                        className="me-2"
                        style={{minWidth: 70}}
                        htmlFor={"dayOfMonth"}
                    >On day</label>
                    <input
                        id={"dayOfMonth"}
                        type="number"
                        min={1}
                        max={31}
                        className="form-control w-auto"
                        value={monthlyDay}
                        onChange={e => setMonthlyDay(boundANumber(parseInt(e.target.value || "1", 10), 1, 31))}
                    />
                    <span>of each month</span>
                </div>
            )}

            {mode === "CRON" && (
                <TextField
                    label="Cron expression"
                    fullWidth
                    placeholder="e.g. 0 12 * * MON,FRI"
                    value={cronText}
                    onChange={e => setCronText(e.target.value)}
                    helperText="Enter a 5-field cron syntax."
                />
            )}

            {mode !== "CRON" && (
                <div className="mb-3 d-flex align-items-center gap-2">
                    <label
                        className="me-2"
                        style={{minWidth: 70}}
                        htmlFor={"hour12"}
                    >At</label>
                    <select
                        id={"hour12"}
                        className="form-select w-auto"
                        value={hour12}
                        onChange={e => setHour12(parseInt(e.target.value, 10))}
                    >
                        {Array.from({length: 12}, (_, i) => i + 1).map(h => (
                            <option
                                key={h}
                                value={h}
                            >{h}</option>
                        ))}
                    </select>
                    <span>:</span>
                    <select
                        className="form-select w-auto"
                        value={minute}
                        onChange={e => setMinute(parseInt(e.target.value, 10))}
                    >
                        {Array.from({length: 60}, (_, i) => i).map(m => (
                            <option
                                key={m}
                                value={m}
                            >{pad2(m)}</option>
                        ))}
                    </select>
                    <select
                        className="form-select w-auto"
                        value={amPm}
                        onChange={e => setAmPm((e.target.value as "am" | "pm"))}
                    >
                        <option value="am">am</option>
                        <option value="pm">pm</option>
                    </select>
                </div>
            )}

            <Typography
                variant="body1"
                color="textSecondary"
                className="my-4"
            >
                Cron (minute hour day-of-month month day-of-week): <code>{cron5}</code>
            </Typography>

            {isWeeklyInvalid && (
                <div className="text-danger small mb-2">Select at least one day of the week.</div>
            )}

            <div className="d-flex justify-content-between">
                <div className="d-flex gap-2">
                    <Button
                        variant="outlined"
                        color="primary"
                        type="button"
                        onClick={() => setOpenCronDialog(true)}
                    >
                        Explain the schedule
                    </Button>
                    <Dialog
                        open={openCronDialog}
                        onClose={() => setOpenCronDialog(false)}
                    >
                        <DialogTitle id="alert-dialog-title">
                            {"Use the following schedule for report?"}
                        </DialogTitle>
                        <DialogContent>
                            <DialogContentText id="alert-dialog-description">
                                Run
                                <Typography
                                    variant="body1"
                                    component="span"
                                    className="text-primary"
                                    sx={{fontStyle: 'italic'}}
                                >
                                    { openCronDialog && cronToText(cron5)}
                                </Typography>
                                .
                            </DialogContentText>
                        </DialogContent>
                        <DialogActions>
                            <Button
                                variant={'outlined'}
                                color={"error"}
                                onClick={() => setOpenCronDialog(false)}
                            >Disagree</Button>
                            <Button
                                variant={'contained'}
                                color={'success'}
                                onClick={() => {
                                    setOpenCronDialog(false);
                                    handleDone();
                                }}
                                autoFocus
                            >
                                Agree
                            </Button>
                        </DialogActions>
                    </Dialog>
                </div>

                <div className="d-flex justify-content-end gap-2">
                    <Button
                        variant="outlined"
                        color="error"
                        type="button"
                        onClick={() => props.onCancel?.()}
                    >Cancel
                    </Button>
                    <button
                        className="btn btn-success"
                        onClick={handleDone}
                        disabled={isWeeklyInvalid}
                    >Done
                    </button>
                </div>
            </div>
        </div>
    );
}
