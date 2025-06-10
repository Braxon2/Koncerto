import { useState, useEffect } from "react";
import { useAuth } from "../context/AuthContext";

const useFetchPagination = (url, initialPage = 0, size = 9) => {
  const { isAuthenticated } = useAuth();
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [page, setPage] = useState(initialPage);
  const [totalPages, setTotalPages] = useState(0);

  const fetchData = async () => {
    if (!isAuthenticated) return;

    setLoading(true);
    try {
      const token = localStorage.getItem("jwtToken");

      const response = await fetch(`${url}?page=${page}&size=${size}`, {
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      });

      if (!response.ok) throw new Error("Failed to fetch");

      const result = await response.json();
      setData(result.content);
      setTotalPages(result.totalPages);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, [page, size, isAuthenticated]);

  return { data, loading, error, page, totalPages, setPage };
};

export default useFetchPagination;
