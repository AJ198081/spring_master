import {Toaster} from "react-hot-toast";

export const ToasterComponent = () => {

    return <Toaster
        position={'bottom-right'}
        reverseOrder={false}
        gutter={10}
        toastOptions={{
            className: '',
            duration: 3000,
            style: {
                backgroundColor: '#fff',
                color: '#000',
            },

            loading: {
                duration: 20000,
            },
            success: {
                duration: 3000,
                iconTheme: {
                    primary: 'green',
                    secondary: 'white',
                },
            },
            error: {
                duration: 3000,
                iconTheme: {
                    primary: 'red',
                    secondary: 'white',
                },
            },
        }}
    />;
}