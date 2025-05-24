import {FormControl, FormLabel, FormControlLabel, Radio, RadioGroup} from "@mui/material";
import {useState} from "react";

export const RadioGroupExample = () => {
    const [value, setValue] = useState('female');

    console.log(value);
    return (
        <FormControl
            sx={{m: 2, minWidth: 120, maxWidth: 300}}
            // disabled={true}
        >
            <FormLabel id="demo-row-radio-buttons-group-label">Gender</FormLabel>
            <RadioGroup
                value={value}
                onChange={(event) => setValue(event.target.value)}
                sx={{
                    flexDirection: 'row',
                    '& .MuiFormControlLabel-label': {
                        fontSize: '1.2em',
                        color: 'primary.dark',
                    },
                    '& .MuiRadio-root.Mui-checked': {
                        color: 'secondary.main',
                    }

                }}
            >
                <FormControlLabel
                    value="female"
                    control={<Radio size="small" color="primary"/>}
                    label="Female"
                    labelPlacement="end"
                />
                <FormControlLabel
                    value="male"
                    control={<Radio size="small" color="primary"/>}
                    label="mail"
                    labelPlacement="end"
                />
            </RadioGroup>
        </FormControl>
    )
}