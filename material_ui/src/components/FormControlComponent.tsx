import {Checkbox, FormControl, FormControlLabel, FormGroup, FormHelperText, FormLabel} from "@mui/material";

export const FormControlComponent = () => {
    return (
        <FormControl
            disabled={false}
            required={true}
            sx={{
                m: 2,
                minWidth: 120,
                '& > *': {
                    color: 'blue',
                }
            }}
            component={"fieldset"}
        >
            <FormLabel
                component={"legend"}
                sx={{margin: 2, fontSize: 18}}
            >
                Selection of checkboxes
            </FormLabel>
            <FormGroup row>
                <FormControlLabel
                    label="Label"
                    labelPlacement="top"
                    control={<Checkbox color={"secondary"}/>}
                />
                <FormControlLabel
                    label="Label"
                    labelPlacement="top"
                    control={<Checkbox color={"secondary"}/>}
                />
                <FormControlLabel
                    label="Label"
                    labelPlacement="top"
                    control={<Checkbox color={"secondary"}/>}
                />
                <FormControlLabel
                    label="Label"
                    labelPlacement="top"
                    control={<Checkbox color={"secondary"}/>}
                />
                <FormControlLabel
                    label="Label"
                    labelPlacement="top"
                    control={<Checkbox color={"secondary"}/>}
                />
            </FormGroup>
            <FormHelperText>
                Please select a value
            </FormHelperText>
        </FormControl>
    )
}