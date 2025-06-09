import React, { createContext, useState, useEffect, useContext } from "react";

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [isAdmin, setIsAdmin] = useState(false);

  useEffect(() => {
    const jwtToken = localStorage.getItem("jwtToken");
    const userRole = localStorage.getItem("userRole");
    const userId = localStorage.getItem("userId");

    if (jwtToken && userRole && userId) {
      setIsAuthenticated(true);
      setUser({ id: userId, role: userRole });
      setIsAdmin(userRole === "ADMIN");
    } else {
      setIsAuthenticated(false);
      setUser(null);
      setIsAdmin(false);
    }
  }, []);

  const login = (jwt, role, id) => {
    localStorage.setItem("jwtToken", jwt);
    localStorage.setItem("userRole", role);
    localStorage.setItem("userId", id);
    setIsAuthenticated(true);
    setUser({ id, role });
    setIsAdmin(role === "ADMIN");
  };

  const logout = () => {
    localStorage.removeItem("jwtToken");
    localStorage.removeItem("userRole");
    localStorage.removeItem("userId");
    setIsAuthenticated(false);
    setUser(null);
    setIsAdmin(false);
  };

  return (
    <AuthContext.Provider
      value={{ user, isAuthenticated, isAdmin, login, logout }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  return useContext(AuthContext);
};
