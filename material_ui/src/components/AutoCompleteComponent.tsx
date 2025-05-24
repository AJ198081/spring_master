import {Autocomplete, TextField, autocompleteClasses} from "@mui/material";
import {useState} from "react";

export const AutoCompleteComponent = () => {

    const [value, setValue] = useState<string[]>([]);

    return (
        <Autocomplete
            multiple
            open
            value={value}
            onChange={(event, newValue) => {
                console.log(event, newValue);
                setValue(newValue);
            }}
            renderInput={params => <TextField {...params} label="Fruit" variant="outlined"/>}
            options={["Apple", "Banana", "Orange", "Mango", "Grapes"]}
            slotProps={{
                chip: {
                    sx: {
                        variant: "standard",
                        bgcolor: "black",
                        color: "white",
                        [`& .MuiChip-deleteIcon`]: {
                            color: "floralwhite"
                        }
                    }
                },
                paper: {
                    sx: {
                        bgcolor: 'purple',
                        color: 'white',
                        maxWidth: 300,
                        [`& .${autocompleteClasses.option}`]: {
                            fontSize: '1rem',
                            fontWeight: 500,
                            fontFamily: 'verdana',
                            color: 'white',


                        }

                    }
                }
            }}
        />

    )
}