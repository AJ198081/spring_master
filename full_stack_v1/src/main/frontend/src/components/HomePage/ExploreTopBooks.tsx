export const ExploreTopBooks = () => {

    return (
        <div className={"p-5 mb-4 bg-dark-subtle header"}>
            <div className={"container-fluid py-5 text-white d-flex justify-content-center align-items-center"}>
                <div>
                    <h1 className={"display-5 fw-bold text-black text-capitalize"}>Find your next adventure</h1>
                    <div className={"container"}>
                        <p className={"col-md-8 fs-4 text-capitalize text-start"}>Where would you like to go next?</p>
                    </div>
                    <a type={"button"} className={"btn btn-primary btn-lg text-white"} href={"#"}>Explore top books</a>
                </div>

            </div>
        </div>
    );
}