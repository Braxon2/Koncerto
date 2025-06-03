import { useState } from "react";
import "./styles/Navbar.css";

const Navbar = () => {
  const [isMenuOpen, setIsMenuOpen] = useState(false);

  const toggleMenu = () => {
    setIsMenuOpen(!isMenuOpen);
  };

  return (
    <nav>
      <div className="logo-container">
        <h1>Koncerto</h1>
      </div>
      <ul className={`navLinks ${isMenuOpen ? "active" : ""}`}>
        <li>
          <a href="/">Home</a>
        </li>
        <li>
          <a href="/profile">Profile</a>
        </li>
        <li>
          <a href="/login">Login</a>
        </li>
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
