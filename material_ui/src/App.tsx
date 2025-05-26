import './App.css'
import {Box, Container, createTheme, ThemeProvider} from "@mui/material";
import {AppBarComponent} from "./components/AppBarComponent.tsx";
import {CardComponent} from "./components/CardComponent.tsx";

function App() {

    const theme = createTheme({
        palette: {
            primary: {
                main: "#008000"
            },
            secondary: {
                main: "#bf00ff"
            },
            common: {
                black: "#000000",
                darkBlue: "#002166",
                white: "#ffffff",
                grey: "#cccccc",
                red: "#ff0000",
                green: "#00ff00",
                blue: "#0000ff",
                yellow: "#ffff00",
                orange: "#ffa500",
                purple: "#800080",
                pink: "#ffc0cb",
                teal: "#008080",
                cyan: "#00ffff",
            },
            success: {
                main: '#dc004e',
                contrastText: '#000000'
            }
        },
        typography: {
            fontFamily: ["Lobster", "Helvetica", "Arial", "sans-serif"].join(','),
        },
    });

    return (
        <ThemeProvider theme={theme}>
            <Container
                // maxWidth="xl"
                // fixed={true}
                sx={{
                    // backgroundColor: 'grey',
                    marginTop: 4,
                    height: '100vh',
                    width: '100vw',
                    justifyContent: 'center',
                    alignItems: 'flex-start',
                }}
            >
                <AppBarComponent/>
                <Box
                    component="main"
                    sx={{
                        marginTop: 4,
                        p: 3,
                        border: '5px dashed purple',
                        width: '100%',
                    }}
                >
                    {/*<RadioGroupExample/>*/}
                    {/*<AutoCompleteComponent/>*/}
                    {/*<AutoCompleteComponentV1/>*/}
                    {/*<PracticeComponent/>*/}
                    {/*<ButtonComponent />*/}
                    {/*<CheckboxComponent/>*/}
                    {/*<FloatingActionButtonComponent />*/}
                    {/*<FormControlComponent />*/}
                    <CardComponent/>
                    <CardComponent/>
                    <CardComponent/>
                    <CardComponent/>

                </Box>
            </Container>

        </ThemeProvider>
    )
}

export default App
