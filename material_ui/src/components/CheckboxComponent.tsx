import {Checkbox, FormControl, FormControlLabel, FormGroup, Stack} from "@mui/material";
import {useState} from "react";
import Battery20Icon from '@mui/icons-material/Battery20';
import DirectionsBikeIcon from '@mui/icons-material/DirectionsBike';

export const CheckboxComponent = () => {

    const [checked, setChecked] = useState<boolean>(false);

    console.log(checked);

    return (
        <Stack
            direction={"column"}
            spacing={2}
            margin={2}
        >
            <Stack direction={"row"}>
                <FormControl disabled={false}>
                    <FormGroup
                        row={true}
                        sx={{
                            margin: 2,
                            alignItems: 'flex-start',
                        }}
                    >
                        <FormControlLabel
                            control={<Checkbox
                                // defaultChecked={true}
                                checkedIcon={<DirectionsBikeIcon
                                    color={'primary'}
                                    fontSize={'large'}
                                />}
                                icon={<Battery20Icon
                                    fontSize={'large'}
                                    color={'error'}
                                />}
                                // indeterminate={true}
                                checked={checked}
                                color="error"
                                onChange={() => setChecked(prevState => !prevState)}
                                name="checkedB"
                            />}
                            label={"Battery"}
                            labelPlacement={"top"}
                            onFocus={(e) => {
                                console.log(e.target.innerHTML, checked)
                            }}
                        />
                        <FormControlLabel
                            control={<Checkbox
                                color="error"
                                sx={{
                                    '& .MuiSvgIcon-root': {
                                        fontSize: '2em',
                                        color: 'primary.dark'
                                    }
                                }}
                            />}
                            sx={{
                                '& .MuiFormControlLabel-label': {
                                    // fontSize: '1rem',
                                    color: 'primary.dark'
                                },
                                '& .MuiSvgIcon-root': {
                                    fontSize: '2em',
                                    color: 'primary.dark'
                                }
                            }}
                            color='error'
                            label={'Another'}
                            labelPlacement={'top'}
                            aria-label={'Another'}
                        />
                    </FormGroup>
                </FormControl>
            </Stack>
        </Stack>

    )

}
