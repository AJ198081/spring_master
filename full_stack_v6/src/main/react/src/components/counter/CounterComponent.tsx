import {Header} from "./Header.tsx";
import {Counter} from "./Counter.tsx";
import {Footer} from "./Footer.tsx";

export const CounterComponent = () => {
    return <>
        <Header/>
        <main className={"flex-fill"}>
            <Counter/>
        </main>
        <Footer/>
    </>;
}