import { useState, useEffect } from "react";
import "./styles/Login.css";
import { useNavigate } from "react-router-dom";
import usePost from "../hooks/usePost";

const Register = () => {
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const navigate = useNavigate();

  const { data, error, loading, post } = usePost();

  useEffect(() => {
    if (data) {
      console.log("Registration successful:", data);
      alert("Registration successful! Please log in.");
      navigate("/login");
    }
  }, [data, navigate]);

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!firstName || !lastName || !email || !password) {
      alert("Please fill in all fields.");
      return;
    }

    try {
      await post(
        "http://localhost:8080/auth/register",
        {
          firstName,
          lastName,
          email,
          password,
        },
        false
      );
    } catch (err) {
      console.error("Registration submission failed:", err);
    }
  };

  return (
    <div className="container">
      <form onSubmit={handleSubmit}>
        <div className="input-container">
          <h2>Register</h2>
        </div>
        <div className="input-container">
          <input
            type="text"
            name="firstname"
            id="firstname"
            placeholder="First name"
            value={firstName}
            onChange={(e) => setFirstName(e.target.value)}
            required
          />
        </div>
        <div className="input-container">
          <input
            type="text"
            name="lastname"
            id="lastname"
            placeholder="Last name"
            value={lastName}
            onChange={(e) => setLastName(e.target.value)}
            required
          />
        </div>
        <div className="input-container">
          <input
            type="email"
            name="email"
            id="email"
            placeholder="Example: email@gmail.com"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
        </div>
        <div className="input-container">
          <input
            type="password"
            name="password"
            id="password"
            placeholder="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </div>

        {error && (
          <div
            className="error-message"
            style={{ color: "red", marginTop: "10px" }}
          >
            {error}
          </div>
        )}

        <div className="button-container">
          <button type="submit" disabled={loading}>
            {loading ? "Registering..." : "Register"}
          </button>
        </div>
        <div className="input-container">
          <p>
            Already have an account? <a href="/login">Login</a>
          </p>
        </div>
      </form>
    </div>
  );
};

export default Register;
