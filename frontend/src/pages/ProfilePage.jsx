import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import "./styles/ProfilePage.css";

const ProfilePage = () => {
  const navigate = useNavigate();
  const { isAuthenticated, user, logout } = useAuth();

  const [profileData, setProfileData] = useState(null);
  const [tickets, setTickets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [ticketLoading, setTicketLoading] = useState(false);
  const [ticketError, setTicketError] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      // Basic check for authentication
      if (!isAuthenticated || !user || !user.id) {
        setError(
          "You are not logged in or user ID is missing. Please log in to view your profile."
        );
        setLoading(false);
        navigate("/login");
        return;
      }

      const token = localStorage.getItem("jwtToken");
      if (!token) {
        setError("Authentication token not found. Please log in.");
        setLoading(false);
        logout();
        navigate("/login");
        return;
      }

      try {
        const profileResponse = await fetch(
          `http://localhost:8080/api/v1/users/${user.id}`,
          {
            method: "GET",
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${token}`,
            },
          }
        );

        if (!profileResponse.ok) {
          let errorMessage = "Failed to fetch profile data.";
          try {
            const errorData = await profileResponse.json();
            errorMessage = errorData.message || errorMessage;
          } catch (jsonError) {
            // If response is not JSON
          }
          if (
            profileResponse.status === 401 ||
            profileResponse.status === 403
          ) {
            errorMessage =
              "Session expired or access denied. Please log in again.";
            logout();
            navigate("/login");
          }
          throw new Error(errorMessage);
        }
        const profileJson = await profileResponse.json();
        setProfileData(profileJson);

        setTicketLoading(true);
        const ticketsResponse = await fetch(
          `http://localhost:8080/api/v1/users/${user.id}/tickets`,
          {
            method: "GET",
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${token}`,
            },
          }
        );

        if (!ticketsResponse.ok) {
          let errorMessage = "Failed to fetch tickets.";
          try {
            const errorData = await ticketsResponse.json();
            errorMessage = errorData.message || errorMessage;
          } catch (jsonError) {
            // If response is not JSON
          }

          if (
            ticketsResponse.status === 401 ||
            ticketsResponse.status === 403
          ) {
            errorMessage =
              "Session expired or access denied for tickets. Please log in again.";
            logout();
            navigate("/login");
          }
          throw new Error(errorMessage);
        }
        const ticketsJson = await ticketsResponse.json();
        setTickets(ticketsJson);
      } catch (err) {
        setError(err.message);
        console.error("Error fetching data:", err);
      } finally {
        setLoading(false);
        setTicketLoading(false);
      }
    };

    fetchData();
  }, [isAuthenticated, user, navigate, logout]);

  const handleDownloadPdf = async (ticketId) => {
    setTicketLoading(true);
    setTicketError(null);

    const token = localStorage.getItem("jwtToken");
    if (!token) {
      setTicketError("Authentication token not found. Please log in.");
      setTicketLoading(false);
      logout();
      navigate("/login");
      return;
    }

    try {
      const response = await fetch(
        `http://localhost:8080/api/v1/tickets/${ticketId}/pdf`,
        {
          method: "GET",
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      if (!response.ok) {
        let errorMessage = "Failed to download ticket PDF.";
        if (response.status === 404) {
          errorMessage = "Ticket PDF not found or ticket doesn't exist.";
        } else if (response.status === 401 || response.status === 403) {
          errorMessage =
            "Session expired or access denied. Please log in again.";
          logout();
          navigate("/login");
        }
        throw new Error(errorMessage);
      }

      const pdfBlob = await response.blob();

      const url = window.URL.createObjectURL(pdfBlob);

      const a = document.createElement("a");
      a.href = url;
      a.download = `ticket_${ticketId}.pdf`;
      document.body.appendChild(a);

      a.click();

      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);
    } catch (err) {
      setTicketError(err.message);
      console.error("Error downloading PDF:", err);
    } finally {
      setTicketLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="profile-container">
        <p>Loading profile and tickets...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="profile-container">
        <p className="error-message">{error}</p>
        {!isAuthenticated && (
          <button onClick={() => navigate("/login")}>Go to Login</button>
        )}
      </div>
    );
  }

  if (!profileData) {
    return (
      <div className="profile-container">
        <p>No profile data available.</p>
      </div>
    );
  }

  return (
    <div className="profile-container">
      <h1>User Profile</h1>
      <div className="profile-info">
        <p>
          <strong>First Name:</strong> {profileData.firstName}
        </p>
        <p>
          <strong>Last Name:</strong> {profileData.lastName}
        </p>
        <p>
          <strong>Email:</strong> {profileData.email}
        </p>
        {user && user.id && (
          <p>
            <strong>User ID:</strong> {user.id}
          </p>
        )}
      </div>

      <h2 className="tickets-heading">My Tickets</h2>
      {ticketLoading && <p>Loading tickets...</p>}
      {ticketError && <p className="error-message">{ticketError}</p>}
      {tickets.length === 0 && !ticketLoading && !ticketError && (
        <p>You don't have any tickets yet.</p>
      )}

      {tickets.length > 0 && (
        <div className="tickets-list">
          {tickets.map((ticket) => (
            <div key={ticket.id} className="ticket-item">
              <div className="ticket-details">
                <p>
                  <strong>Event:</strong> {ticket.artist}
                </p>
                <p>
                  <strong>Venue:</strong> {ticket.venue}, {ticket.city}
                </p>
                <p>
                  <strong>Date:</strong>{" "}
                  {new Date(ticket.dateTime).toLocaleDateString()}{" "}
                  {new Date(ticket.dateTime).toLocaleTimeString([], {
                    hour: "2-digit",
                    minute: "2-digit",
                  })}
                </p>
                <p>
                  <strong>Type:</strong> {ticket.typeOfTicket}
                </p>
                <p>
                  <strong>Price:</strong> ${ticket.price.toFixed(2)}
                </p>
              </div>
              <button
                className="download-pdf-button"
                onClick={() => handleDownloadPdf(ticket.id)}
                disabled={ticketLoading}
              >
                {ticketLoading ? "Downloading..." : "Download PDF"}
              </button>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default ProfilePage;
