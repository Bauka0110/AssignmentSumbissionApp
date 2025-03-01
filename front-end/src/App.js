import "./App.css";

function App() {
  console.log("HEllo!");

  const reqBody = {
    username: "bauka",
    password: "asdfasdf",
  };

  fetch("api/auth/login", {
    headers: {
      "Content-Type": "application/json",
    },
    method: "post",
    body: JSON.stringify(reqBody),
  })
    .then((response) => Promise.all([response.json(), response.headers]))
    .then(([body, headers]) => {
      const aithValue = headers.get("authorization");
      console.log(aithValue);
      console.log(body);
    })
    .catch((error) => console.error("Error:", error));
  return (
    <div className="App">
      <h1>Hello Bauka!</h1>
    </div>
  );
}

export default App;
