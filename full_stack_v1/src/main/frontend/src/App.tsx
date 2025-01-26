
function App() {

    const element_name = "rest_assured_it_will_work";

  return (<div className="container">
          <div className={"text-capitalize"}>{element_name.replace(/_/g, " ")}</div>
    </div>
  )
}

export default App
