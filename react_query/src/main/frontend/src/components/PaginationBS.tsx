import {Page} from "../domain/types/Page.ts";
import {Post} from "../domain/types/Post.ts";

export function PaginationBS(page: Page<Post>, setCurrentPage: (value: (((prevState: number) => number) | number)) => void) {

    return <nav aria-label="Page navigation example">
        <ul className="pagination justify-content-center">
            <li className="page-item">
                <a className={`page-link ${page?.first ? 'disabled' : ''}`}
                   href={"#"}
                   onClick={(e) => {
                       if (page?.first) {
                           e.preventDefault();
                       } else {
                           setCurrentPage(prevState => prevState - 1);
                       }
                   }}>
                    Previous
                </a>
            </li>


            <li className="page-item"><a className="page-link" href="#"
                                         onClick={() => setCurrentPage(1)}
            >
                1
            </a>
            </li>

            <li className="page-item"><a className="page-link" href="#"
                                         onClick={() => setCurrentPage(2)}
            >
                2
            </a>
            </li>

            <li className="page-item"><a className="page-link" href="#"
                                         onClick={() => setCurrentPage(3)}
            >
                3
            </a>
            </li>

            <li className="page-item"><a
                className={`page-link ${page?.last ? 'disabled' : ''}`}
                href="#"
                onClick={(event) => {
                    if (page?.last) {
                        event.preventDefault();
                    } else {
                        setCurrentPage(prevState => prevState + 1);
                    }
                }}
            >
                Next
            </a>
            </li>
        </ul>
    </nav>

}
