import './App.css'
import {Container, createTheme, ThemeProvider} from "@mui/material";
import {AppBarComponent} from "./components/AppBarComponent.tsx";
import {FooterComponent} from './components/FooterComponent.tsx';

function App() {

    const theme = createTheme({
        palette: {
            primary: {
                main: "#008000"
            },
            secondary: {
                // main: "#bf00ff"
                main: "#000000",
                contrastText: '#fff'
            },
            common: {
                black: "#000000",
                white: "#ffffff",
            },
            success: {
                main: '#dc004e',
                contrastText: '#000000'
            }
        },
        typography: {
            // fontFamily: ["Lobster", "Helvetica", "Arial", "sans-serif"].join(','),
            fontFamily: ["Roboto", "Helvetica", "Arial", "sans-serif"].join(','),
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

                {/*<RadioGroupExample/>*/}
                {/*<AutoCompleteComponent/>*/}
                {/*<AutoCompleteComponentV1/>*/}
                {/*<PracticeComponent/>*/}
                {/*<ButtonComponent />*/}
                {/*<CheckboxComponent/>*/}
                {/*<FloatingActionButtonComponent />*/}
                {/*<FormControlComponent />*/}
                {/*<MenuComponent />*/}
                {/*<CardComponent/>*/}
                <FooterComponent/>

            </Container>

        </ThemeProvider>
    )
}

export default App
