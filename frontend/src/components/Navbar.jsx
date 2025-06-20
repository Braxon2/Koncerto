import { useState } from "react";
import { useLocation, useNavigate, Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import "./styles/Navbar.css";

const Navbar = () => {
  const [isMenuOpen, setIsMenuOpen] = useState(false);

  const location = useLocation();

  const navigate = useNavigate();

  const { isAuthenticated, isAdmin, logout } = useAuth();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  const toggleMenu = () => {
    setIsMenuOpen(!isMenuOpen);
  };

  return (
    <nav>
      <div className="logo-container">
        <h1>Koncerto</h1>
      </div>
      <ul className={`navLinks ${isMenuOpen ? "active" : ""}`}>
        {isAuthenticated ? (
          <>
            <li>
              <a href="/">Home</a>
            </li>
            {!isAdmin && (
              <li>
                <Link to="/profile">Profile</Link>
              </li>
            )}
            {isAdmin && (
              <li>
                <Link to="/events/create">Create event</Link>
              </li>
            )}
            <li>
              <a href="#" onClick={handleLogout}>
                Logout
              </a>
            </li>
          </>
        ) : (
          <>
            {location.pathname === "/login" ? (
              <li>
                <a href="/register">Register</a>
              </li>
            ) : (
              <li>
                <a href="/login">Login</a>
              </li>
            )}
          </>
        )}
      </ul>
      <div className="hamburger-menu" onClick={toggleMenu}>
        <span></span>
        <span></span>
        <span></span>
      </div>
    </nav>
  );
};

export default Navbar;
