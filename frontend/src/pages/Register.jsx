import "./styles/Login.css";

const Register = () => {
  return (
    <div className="container">
      <form action="">
        <div className="input-container">
          <h2>Register</h2>
        </div>
        <div className="input-container">
          <input
            type="text"
            name="firstname"
            id="email"
            placeholder="First name"
          />
        </div>
        <div className="input-container">
          <input
            type="text"
            name="lastname"
            id="email"
            placeholder="Last name"
          />
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
          <button>Register</button>
        </div>
      </form>
    </div>
  );
};

export default Register;
