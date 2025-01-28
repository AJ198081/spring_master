import {NavBar} from "./components/NavBar.tsx";
import {ExploreTopBooks} from "./components/HomePage/ExploreTopBooks.tsx";
import {Carousel} from "./components/HomePage/Carousel.tsx";
import {Child} from "./scratches/Child.tsx";

function App() {

    return (
        <>
            <NavBar/>
            <ExploreTopBooks />
            <Carousel />
            <Child color="blue" />
        </>
);
}

export default App
