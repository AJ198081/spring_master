export function Carousel() {

    return (
        <div id="carouselHome" className="carousel carousel-dark slide">
            <div className="carousel-inner w-25 h-25 mx-auto">
                <div className="carousel-item active text-center">
                    <img src="/github_actions.jpg" className="d-block w-100" alt="..."/>
                    <h6>GitHub Actions</h6>
                    <p>Manning publication</p>
                    <a className="btn btn-primary" href="https://www.manning.com/books/github-actions-in-action">
                        Reserve
                    </a>
                </div>
                <div className="carousel-item text-center">
                    <img src="/kafka_streams.jpg" className="d-block w-100" alt="..."/>
                    <h6>Kafka Streams</h6>
                    <p>Manning publication</p>
                    <a className="btn btn-primary" href="https://www.manning.com/books/kafka-streams-in-action">
                        Reserve
                    </a>
                </div>
                <div className="carousel-item text-center">
                    <img src="/elasticsearch.jpg" className="d-block w-100" alt="..."/>
                    <h6>Elasticsearch</h6>
                    <p>Manning publication</p>
                    <a className="btn btn-primary" href="https://www.manning.com/books/elasticsearch-in-action">
                        Reserve
                    </a>
                </div>
                <div className="carousel-item text-center">
                    <img src="/spring_security.jpg" className="d-block w-100" alt="..."/>
                    <h6>Spring Security</h6>
                    <p>Manning publication</p>
                    <a className="btn btn-primary" href="https://www.manning.com/books/spring-security-in-action">
                        Reserve
                    </a>
                </div>
                <div className="carousel-item text-center">
                    <img src="/react_quickly.jpg" className="d-block w-100" alt="..."/>
                    <h6>React Quickly</h6>
                    <p>Manning publication</p>
                    <a className="btn btn-primary" href="https://www.manning.com/books/react-quickly">
                        Reserve
                    </a>
                </div>
            </div>
            <button className="carousel-control-prev" type="button" data-bs-target="#carouselHome"
                    data-bs-slide="prev">
                <span className="carousel-control-prev-icon" aria-hidden="true"></span>
                <span className="visually-hidden">Previous</span>
            </button>
            <button className="carousel-control-next" type="button" data-bs-target="#carouselHome"
                    data-bs-slide="next">
                <span className="carousel-control-next-icon" aria-hidden="true"></span>
                <span className="visually-hidden">Next</span>
            </button>
        </div>
    );
}