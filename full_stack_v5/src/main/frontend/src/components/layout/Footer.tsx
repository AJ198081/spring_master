import {FaFacebook, FaInstagram, FaTwitter} from "react-icons/fa";

export const Footer = () => {
    return (
        <footer className="mega-footer">
            <div className={"footer-container"}>
                <div className={"footer-section"}>
                    <h3>About Us</h3>
                    <p>
                        Lorem ipsum dolor sit amet,
                        consectetur adipiscing elit,
                        sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.
                    </p>
                </div>
                <div className={"footer-section"}>
                    <h3>Category</h3>
                    <ul>
                        <li><a href="#">1</a></li>
                        <li><a href="#">2</a></li>
                        <li><a href="#">3</a></li>
                    </ul>
                </div>
                <div className={"footer-section"}>
                    <h3>Contact</h3>
                    <p>Email: dev.aj@gmail.com</p>
                    <p>Phone: +91 9876543210</p>
                </div>
                <div className={"footer-section"}>
                    <h3>Follow Us</h3>
                    <ul>
                        <li><a
                            href="#"
                            target={"_blank"}
                        > <FaFacebook/></a></li>
                        <li><a
                            href="#"
                            target={"_blank"}
                        > <FaTwitter/></a></li>
                        <li><a
                            href="#"
                            target={"_blank"}
                        > <FaInstagram/></a></li>
                    </ul>
                </div>
            </div>
                <div className={"footer-bottom"}>
                    <p>&copy; 2025 dev.aj.com. All rights open.</p>
                </div>
        </footer>
    )
}