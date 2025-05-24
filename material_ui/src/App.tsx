import './App.css'
import {Box, Container, createTheme, ThemeProvider} from "@mui/material";
import {FormControlComponent} from "./components/FormControlComponent.tsx";

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
                black: "#002166"
            },
            success: {
                main: '#dc004e',
                contrastText: '#000000'
            }
        }
    });

    return (
        <ThemeProvider theme={theme}>
            <Container
                maxWidth="lg"
                fixed={true}
                sx={{
                    // backgroundColor: 'grey',
                    marginTop: 4,
                    height: '100vh',
                    justifyContent: 'center',
                    alignItems: 'flex-start',
                }}
            >
                <Box
                    component="main"
                    sx={{
                        p: 3,
                        border: '1px dashed grey'
                    }}
                >
                    {/*<RadioGroupExample/>*/}
                    {/*<AutoCompleteComponent/>*/}
                    {/*<AutoCompleteComponentV1/>*/}
                    {/*<PracticeComponent/>*/}
                    {/*<ButtonComponent />*/}
                    {/*<CheckboxComponent/>*/}
                    {/*<FloatingActionButtonComponent />*/}
                    <FormControlComponent />
                </Box>
            </Container>

        </ThemeProvider>
    )
}

export default App
