import Slider from "react-slick";
import "slick-carousel/slick/slick.css";
import "slick-carousel/slick/slick-theme.css";
import bg1 from "../../assets/images/hero-1.jpg";
import bg2 from "../../assets/images/hero-2.jpg";
import bg3 from "../../assets/images/hero-3.jpg";
import bg6 from "../../assets/images/hero-6.jpg";
import bg7 from "../../assets/images/hero-7.jpg";

const images = [bg1, bg2, bg3, bg6, bg7];

export const HeroSlider = () => {

    const settings = {
        infinite: true,
        speed: 12000,
        autoplay: true,
        autoplaySpeed: 15000,
    }

    return (
        <Slider {...settings} className={"hero-slider"}>
            {images.map((image, index) => (
                <div
                    key={`${image} - ${index}`}
                    className={"slide"}
                >
                    <img
                        src={image}
                        alt={`Slide ${index + 1}`}
                    />

                </div>))
            }
        </Slider>
    )
}