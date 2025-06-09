import { useState, useEffect, useRef } from "react";
import "./styles/Login.css";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

import usePost from "../hooks/usePost";

const Login = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const navigate = useNavigate();

  const { data, error, loading, post } = usePost();

  const { login, isAuthenticated } = useAuth();

  const hasNavigatedAfterLogin = useRef(false);

  useEffect(() => {
    if (data && !hasNavigatedAfterLogin.current) {
      console.log("Login successful:", data);
      login(data.jwt, data.role, data.id);

      hasNavigatedAfterLogin.current = true;

      navigate("/");
    }
  }, [data, navigate, login]);

  useEffect(() => {
    if (isAuthenticated) {
      navigate("/");
    }
  }, [isAuthenticated, navigate]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await post("http://localhost:8080/auth/authenticate", {
        email,
        password,
      });
    } catch (err) {
      console.error("Login submission caught error (from hook):", err);
    }
  };

  return (
    <div className="container">
      <form onSubmit={handleSubmit}>
        <div className="input-container">
          <h2>Login</h2>
        </div>
        <div className="input-container">
          <input
            type="text"
            name="email"
            id="email"
            placeholder="Example: email@gmail.com"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
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
            {loading ? "Logging in..." : "Login"}
          </button>
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
