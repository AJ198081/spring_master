// import {useState} from "react";
import type {MouseEvent} from "react";
import {HeroSlider} from "./HeroSlider.tsx";
import {SearchBar} from "../search/SearchBar.tsx";

export const Hero = () => {

    // const [currentSlide, setCurrentSlide] = useState(0);

    const onClickHandler = (e: MouseEvent<HTMLButtonElement>) => {
        console.log(e.currentTarget.innerText);
    }

    return (
        <div className={"hero"}>
            <HeroSlider
                // setCurrentSlide={currentSlide}
            />
            <div className={"hero-content"}>
                <h1>Welcome to <span className={"text-primary"}>devAj</span>.com</h1>
                <SearchBar/>
                <div className={"home-button-container"}>
                    <button
                        className={"home-shop-button link"}
                        onClick={onClickHandler}
                    >
                        Shop Now
                    </button>
                    <button
                        className={"deals-button"}
                        onClick={onClickHandler}
                    >
                        Today's deal
                    </button>
                </div>
            </div>
        </div>
    );
}