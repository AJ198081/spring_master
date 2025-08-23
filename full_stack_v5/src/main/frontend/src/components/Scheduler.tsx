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

export type ScheduleMode = "DAILY" | "WEEKLY" | "MONTHLY" | "CRON";

export interface TimeOfDay {
    hour24: number;
    minute: number;
}

export interface WeeklySchedule {
    days: number[];
    interval: "once_per_day";
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

function toHourMinuteAmPm(hour24: number) {
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

function clamp(n: number, min: number, max: number) {
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
    {k: "WEEKLY", t: "Days per week"},
    {k: "MONTHLY", t: "Days per month"},
    {k: "CRON", t: "Cron expression"},
];

function pad2(n: number) {
    return n.toString().padStart(2, "0");
}

function buildCron(value: SchedulerValue) {
    const m = value.time.minute;
    const h = value.time.hour24;
    if (value.mode === "CRON" && value.cron) {
        return {cron5: value.cron, cron6: value.cron};
    }
    if (value.mode === "DAILY") {
        const cron5 = `${m} ${h} * * *`;
        const cron6 = `0 ${m} ${h} * * *`;
        return {cron5, cron6};
    }
    if (value.mode === "WEEKLY" && value.weekly) {
        const days = value.weekly.days.length ? value.weekly.days : defaultWeeklyDays;

        const dowNames = days
            .sort((a, b) => a - b)
            .map(d => ["SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"][d])
            .join(",");
        const cron5 = `${m} ${h} * * ${dowNames}`;
        const cron6 = `0 ${m} ${h} ? * ${dowNames}`; // Quartz style uses ? in day-of-month
        return {cron5, cron6};
    }
    if (value.mode === "MONTHLY" && value.monthly) {
        const dom = clamp(value.monthly.dayOfMonth, 1, 31);
        const cron5 = `${m} ${h} ${dom} * *`;
        const cron6 = `0 ${m} ${h} ${dom} * ?`;
        return {cron5, cron6};
    }
    const cron5 = `${m} ${h} * * *`;
    const cron6 = `0 ${m} ${h} * * *`;
    return {cron5, cron6};
}

export function Scheduler(props: Readonly<SchedulerProps>) {
    const initialHour24 = typeof props.value?.time?.hour24 === "number" ? props.value!.time!.hour24 : 12;
    const initialMinute = typeof props.value?.time?.minute === "number" ? props.value!.time!.minute : 0;

    const [mode, setMode] = useState<ScheduleMode>(props.value?.mode ?? "WEEKLY");
    const [hour12, setHour12] = useState<number>(toHourMinuteAmPm(initialHour24).hour12);
    const [minute, setMinute] = useState<number>(initialMinute);
    const [amPm, setAmPm] = useState<"am" | "pm">(toHourMinuteAmPm(initialHour24).ampm as "am" | "pm");

    const [weeklyDays, setWeeklyDays] = useState<number[]>(props.value?.weekly?.days ?? defaultWeeklyDays);
    const [monthlyDay, setMonthlyDay] = useState<number>(props.value?.monthly?.dayOfMonth ?? 1);
    const [cronText, setCronText] = useState<string>(props.value?.cron ?? "");

    const [openCronDialog, setOpenCronDialog] = useState(false);

    const time: TimeOfDay = useMemo(() => ({
        hour24: fromHour12(hour12, amPm),
        minute
    }), [hour12, amPm, minute]);

    const value: SchedulerValue = useMemo(() => ({
        mode,
        time,
        weekly: mode === "WEEKLY" ? {days: weeklyDays, interval: "once_per_day"} : undefined,
        monthly: mode === "MONTHLY" ? {dayOfMonth: monthlyDay} : undefined,
        cron: mode === "CRON" ? cronText : undefined
    }), [mode, time, weeklyDays, monthlyDay, cronText]);

    const {cron5, cron6} = useMemo(() => buildCron(value), [value]);

    // notify parent on each change
    useEffect(() => {
        props.onChange?.({...value, cron: cron5});
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [value, cron5]);

    const toggleDay = (d: number) => {
        setWeeklyDays(prev => prev.includes(d) ? prev.filter(x => x !== d) : [...prev, d]);
    };

    const isWeeklyInvalid = mode === "WEEKLY" && weeklyDays.length === 0;

    const handleDone = () => {
        const finalValue: SchedulerValue = {...value, cron: cron5};
        props.onDone?.(finalValue);
    };

    const cronToText = (cronExpression: string) => {
        const [minute, hour, dayMonth, dayWeek] = cronExpression.split(' ');
        let explanation = '- ';

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

    return (
        <div
            className="container p-3"
            style={{maxWidth: 640}}
        >
            <h5 className="mb-3">{props.title ?? "Schedule editor"}</h5>

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

            <hr/>

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

                    <div className="mb-3 d-flex align-items-center gap-2">
                        <label
                            className="me-2"
                            style={{minWidth: 70}}
                        >Interval</label>
                        <select
                            className="form-select w-auto"
                            value="once_per_day"
                            disabled
                        >
                            <option value="once_per_day">once per day</option>
                        </select>
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
                    >On day</label>
                    <input
                        type="number"
                        min={1}
                        max={31}
                        className="form-control w-auto"
                        value={monthlyDay}
                        onChange={e => setMonthlyDay(clamp(parseInt(e.target.value || "1", 10), 1, 31))}
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
                    helperText="You can enter either 5-field or 6-field cron syntax."
                />
            )}

            {mode !== "CRON" && (
                <div className="mb-3 d-flex align-items-center gap-2">
                    <label
                        className="me-2"
                        style={{minWidth: 70}}
                    >At</label>
                    <select
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

            <div className="mb-3">
                <div className="small text-muted">Cron (5-field): <code>{cron5}</code></div>
                <div className="small text-muted">Cron (6-field): <code>{cron6}</code></div>
            </div>

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
                        title="Cron expression"
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
                                    {cronToText(cron5)}
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
                                onClick={() => setOpenCronDialog(false)}
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
