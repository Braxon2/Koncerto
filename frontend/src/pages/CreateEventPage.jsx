import React, { useState, useEffect } from "react";
import DatePicker from "react-datepicker";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

import "react-datepicker/dist/react-datepicker.css";
import "./styles/CreateEventPage.css";

const CreateEventPage = () => {
  const navigate = useNavigate();
  const { isAuthenticated, isAdmin, logout } = useAuth();

  const [artist, setArtist] = useState("");
  const [city, setCity] = useState("");
  const [address, setAddress] = useState("");
  const [venue, setVenue] = useState("");
  const [dateTime, setDateTime] = useState(new Date());
  const [description, setDescription] = useState("");
  const [imageFile, setImageFile] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [successMessage, setSuccessMessage] = useState(null);

  useEffect(() => {
    if (!isAuthenticated) {
      setError("You are not logged in. Please log in to create an event.");
      navigate("/login");
      return;
    }
    if (!isAdmin) {
      setError(
        "Access Denied: You do not have administrator privileges to create events."
      );
      navigate("/");
    }
  }, [isAuthenticated, isAdmin, navigate]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    setSuccessMessage(null);

    const token = localStorage.getItem("jwtToken");

    if (!token) {
      setError("Authentication token not found. Please log in.");
      setLoading(false);
      navigate("/login");
      return;
    }
    if (!isAdmin) {
      setError("Access Denied: You do not have administrator privileges.");
      setLoading(false);
      return;
    }

    const formData = new FormData();

    const eventData = {
      artist,
      city,
      address,
      venue,
      dateTime: dateTime.toISOString(),
      description,
    };
    formData.append(
      "eventData",
      new Blob([JSON.stringify(eventData)], { type: "application/json" })
    );

    if (imageFile) {
      formData.append("imageFile", imageFile);
    } else {
      setError("Event image is required.");
      setLoading(false);
      return;
    }

    try {
      const response = await fetch("http://localhost:8080/api/v1/events", {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
        },
        body: formData,
      });

      if (!response.ok) {
        let errorMessage = "Failed to create event.";
        try {
          const errorData = await response.json();
          errorMessage = errorData.message || errorMessage;
        } catch (jsonError) {
          console.error("Request failed:", jsonError);
        }

        if (response.status === 403) {
          errorMessage = "You do not have permission to perform this action.";
          logout();
          navigate("/login");
        } else if (response.status === 401) {
          errorMessage = "Your session has expired. Please log in again.";
          logout();
          navigate("/login");
        }
        throw new Error(errorMessage);
      }

      const responseData = await response.json();
      setSuccessMessage("Event created successfully! Redirecting...");
      console.log("Event created:", responseData);

      setTimeout(() => {
        navigate("/");
      }, 2000);
    } catch (err) {
      setError(err.message);
      console.error("Error creating event:", err);
    } finally {
      setLoading(false);
    }
  };

  if (!isAuthenticated && !error) {
    return (
      <div className="create-event-container">
        <p>Checking authentication...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="create-event-container">
        <p className="error-message">{error}</p>
        {(!isAuthenticated || !isAdmin) && (
          <button onClick={() => navigate("/login")}>Go to Login</button>
        )}
      </div>
    );
  }

  return (
    <div className="create-event-container">
      <h1>Create New Event</h1>
      <form onSubmit={handleSubmit} className="create-event-form">
        {successMessage && <p className="success-message">{successMessage}</p>}

        {error && <p className="error-message">{error}</p>}

        <div className="form-group">
          <label htmlFor="artist">Artist:</label>
          <input
            type="text"
            id="artist"
            value={artist}
            onChange={(e) => setArtist(e.target.value)}
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="city">City:</label>
          <input
            type="text"
            id="city"
            value={city}
            onChange={(e) => setCity(e.target.value)}
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="address">Address:</label>
          <input
            type="text"
            id="address"
            value={address}
            onChange={(e) => setAddress(e.target.value)}
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="venue">Venue:</label>
          <input
            type="text"
            id="venue"
            value={venue}
            onChange={(e) => setVenue(e.target.value)}
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="dateTime">Date and Time:</label>
          <DatePicker
            id="dateTime"
            selected={dateTime}
            onChange={(date) => setDateTime(date)}
            showTimeSelect
            dateFormat="Pp"
            className="date-picker-input"
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="description">Description:</label>
          <textarea
            id="description"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            rows="5"
            required
          ></textarea>
        </div>

        <div className="form-group">
          <label htmlFor="imageFile">Event Image:</label>
          <input
            type="file"
            id="imageFile"
            accept="image/jpeg,image/png,image/gif"
            onChange={(e) => setImageFile(e.target.files[0])}
            required
          />
        </div>

        <button type="submit" disabled={loading}>
          {loading ? "Creating..." : "Create Event"}
        </button>
      </form>
    </div>
  );
};

export default CreateEventPage;
