import {Autocomplete, TextField} from "@mui/material";

export const AutoCompleteComponentV1 = () => {

    const options = ["Apple", "Banana", "Orange", "Mango", "Grapes", "Pineapple", "Strawberry", "Cherry", "Blueberry"];


    return (
        <div>
            <h1>Hello</h1>
            <h2>Welcome to AutoComplete Component</h2>
            <Autocomplete
                renderInput={params => <TextField {...params} label="Fruit" variant="outlined"/>}
                options={options}/>
        </div>
    );
}