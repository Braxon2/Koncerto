import "./styles/Login.css";

const Login = () => {
  return (
    <div className="container">
      <form action="">
        <div className="input-container">
          <h2>Login</h2>
        </div>
        <div className="input-container">
          <input
            type="text"
            name="email"
            id="email"
            placeholder="Example: email@gmail.com"
          />
        </div>
        <div className="input-container">
          <input
            type="password"
            name="password"
            id="password"
            placeholder="password"
          />
        </div>

        <div className="button-container">
          <button>Login</button>
        </div>
        <div className="input-container">
          <p>
            Dont have an account? <a href="/register">Register</a>
          </p>
        </div>
      </form>
    </div>
  );
};

export default Login;
