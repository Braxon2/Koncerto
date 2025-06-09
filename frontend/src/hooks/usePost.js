import { useState, useCallback } from "react";

const usePost = () => {
  const [data, setData] = useState(null);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);

  const post = useCallback(async (url, body, requiresAuth = false) => {
    setLoading(true);
    setError(null);
    setData(null);

    try {
      const headers = {
        "Content-Type": "application/json",
      };

      if (requiresAuth) {
        const jwtToken = localStorage.getItem("jwtToken");
        if (jwtToken) {
          headers["Authorization"] = `Bearer ${jwtToken}`;
        } else {
          throw new Error("Authentication token not found. Please log in.");
        }
      }

      const response = await fetch(url, {
        method: "POST",
        headers: headers,
        body: JSON.stringify(body),
      });

      if (!response.ok) {
        let errorMessage = "An unexpected error occurred.";
        try {
          const errorData = await response.json();

          if (errorData && typeof errorData === "object" && errorData.detail) {
            errorMessage = errorData.detail;
          } else if (
            errorData &&
            typeof errorData === "object" &&
            errorData.message
          ) {
            errorMessage = errorData.message;
          } else {
            errorMessage =
              response.statusText ||
              `Request failed with status ${response.status}`;
          }
        } catch (jsonError) {
          errorMessage =
            response.statusText ||
            `Request failed with status ${response.status}`;
        }
        throw new Error(errorMessage);
      }

      const responseData = await response.json();
      setData(responseData);
      return responseData;
    } catch (err) {
      console.error("Error in usePost hook:", err);
      setError(
        err.message ||
          "Could not connect to the server. Please try again later."
      );
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  return { data, error, loading, post };
};

export default usePost;
