import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import "./styles/EventDetailsPage.css";

const EventDetailsPage = () => {
  const { id } = useParams();
  const [event, setEvent] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchEventDetails = async () => {
      try {
        const response = await fetch(
          `http://localhost:8080/api/v1/events/${id}`
        );
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        const data = await response.json();
        setEvent(data);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchEventDetails();
  }, [id]);

  if (loading)
    return (
      <div className="event-details-container">Loading event details...</div>
    );

  if (error)
    return <div className="event-details-container">Error: {error}</div>;

  if (!event)
    return <div className="event-details-container">Event not found.</div>;

  return (
    <div className="event-details-container">
      <img
        src={event.imageURL || "src/images/placeholder.jpg"}
        alt={event.artist}
        className="event-details-image"
      />
      <h1>{event.artist}</h1>
      <p>
        Date: {new Date(event.dateTime).toLocaleDateString()} at{" "}
        {new Date(event.dateTime).toLocaleTimeString([], {
          hour: "2-digit",
          minute: "2-digit",
        })}
      </p>
      <p>
        Venue: {event.venue}, {event.address}, {event.city}
      </p>
      <p>Description: {event.description || "No description available."}</p>
      <button className="buy-tickets-button">Buy Tickets</button>
    </div>
  );
};

export default EventDetailsPage;
