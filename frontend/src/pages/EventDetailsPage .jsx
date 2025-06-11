import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import "./styles/EventDetailsPage.css";

const EventDetailsPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { isAuthenticated, user, logout } = useAuth();

  const [event, setEvent] = useState(null);
  const [availableTicketTypes, setAvailableTicketTypes] = useState([]);
  const [selectedTicketTypeId, setSelectedTicketTypeId] = useState("");
  const [ticketQuantity, setTicketQuantity] = useState(1);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchEventAndTickets = async () => {
      try {
        const eventResponse = await fetch(
          `http://localhost:8080/api/v1/events/${id}`
        );
        if (!eventResponse.ok) {
          throw new Error(
            `HTTP error! status: ${eventResponse.status} for event details`
          );
        }
        const eventData = await eventResponse.json();
        setEvent(eventData);

        const ticketsResponse = await fetch(
          `http://localhost:8080/api/v1/events/${id}/tickets`
        );
        if (!ticketsResponse.ok) {
          throw new Error(
            `HTTP error! status: ${ticketsResponse.status} for ticket types`
          );
        }
        const ticketsData = await ticketsResponse.json();
        setAvailableTicketTypes(ticketsData);

        if (ticketsData.length > 0) {
          setSelectedTicketTypeId(ticketsData[0].id);
        }
        setTicketQuantity(1);
      } catch (err) {
        setError(err.message);
        console.error("Error fetching event or ticket types:", err);
      } finally {
        setLoading(false);
      }
    };

    fetchEventAndTickets();
  }, [id]);

  const selectedTicket = availableTicketTypes.find(
    (ticket) => ticket.id === selectedTicketTypeId
  );

  const handleBuyTickets = async () => {
    setError(null);

    if (!isAuthenticated) {
      alert("Please log in to buy tickets.");
      navigate("/login");
      return;
    }

    if (!selectedTicket) {
      alert("Please select a ticket type.");
      return;
    }

    if (ticketQuantity <= 0) {
      alert("Quantity must be at least 1.");
      return;
    }

    if (ticketQuantity > selectedTicket.quantity) {
      alert(
        `Maximum available for this ticket type is ${selectedTicket.quantity}.`
      );
      return;
    }

    const token = localStorage.getItem("jwtToken");
    if (!token) {
      alert("Authentication token not found. Please log in again.");
      logout();
      navigate("/login");
      return;
    }

    const purchaseData = {
      ticketType: selectedTicket.ticketType,
      quantity: ticketQuantity,
    };

    console.log("Attempting to buy tickets with data:", purchaseData);

    try {
      const purchaseResponse = await fetch(
        `http://localhost:8080/api/v1/events/${id}/buy`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify(purchaseData),
        }
      );

      if (!purchaseResponse.ok) {
        let purchaseErrorMessage =
          "Failed to purchase tickets. Please try again.";

        try {
          const errorData = await purchaseResponse.json();
          if (errorData && errorData.message) {
            purchaseErrorMessage = errorData.message;
          } else if (errorData) {
            purchaseErrorMessage = JSON.stringify(errorData);
          }
        } catch (jsonParseError) {
          console.warn(
            "Could not parse JSON error response, attempting to read as text:",
            jsonParseError
          );
          try {
            const errorText = await purchaseResponse.text();
            if (errorText) {
              purchaseErrorMessage = errorText;
            }
          } catch (textReadError) {
            console.error(
              "Could not read response as text either.",
              textReadError
            );
          }
        }

        if (
          purchaseResponse.status === 401 ||
          purchaseResponse.status === 403
        ) {
          purchaseErrorMessage =
            "Session expired or unauthorized. Please log in again.";
          logout();
          navigate("/login");
        } else if (purchaseResponse.status === 400) {
          purchaseErrorMessage = `Error: ${purchaseErrorMessage}`;
        }

        throw new Error(purchaseErrorMessage);
      }

      const resultText = await purchaseResponse.text();
      alert(resultText || "Tickets purchased successfully!");

      console.log("Purchase successful:", resultText);
      navigate(`/profile`);
    } catch (err) {
      setError(err.message);
      console.error("Error during ticket purchase:", err);
    }
  };

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
        <strong>Date:</strong> {new Date(event.dateTime).toLocaleDateString()}{" "}
        at{" "}
        {new Date(event.dateTime).toLocaleTimeString([], {
          hour: "2-digit",
          minute: "2-digit",
        })}
      </p>
      <p>
        <strong>Venue:</strong> {event.venue}, {event.address}, {event.city}
      </p>
      <p>
        <strong>Description:</strong>{" "}
        {event.description || "No description available."}
      </p>

      <div className="ticket-selection">
        <h2>Select Your Tickets</h2>
        {availableTicketTypes.length > 0 ? (
          <>
            <div className="form-group">
              <label htmlFor="ticket-type">Ticket Type:</label>
              <select
                id="ticket-type"
                value={selectedTicketTypeId}
                onChange={(e) =>
                  setSelectedTicketTypeId(Number(e.target.value))
                }
                className="ticket-dropdown"
              >
                {availableTicketTypes.map((ticketType) => (
                  <option key={ticketType.id} value={ticketType.id}>
                    {ticketType.ticketType} - {ticketType.price}RSD (
                    {ticketType.quantity} available){" "}
                  </option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <label htmlFor="quantity">Quantity:</label>
              <input
                type="number"
                id="quantity"
                min="1"
                max={selectedTicket ? selectedTicket.quantity : 1}
                value={ticketQuantity}
                onChange={(e) =>
                  setTicketQuantity(Math.max(1, parseInt(e.target.value) || 1))
                }
                className="quantity-input"
              />
            </div>

            <p className="total-price">
              Total: RSD
              {selectedTicket ? selectedTicket.price * ticketQuantity : 0}
            </p>

            <button className="buy-tickets-button" onClick={handleBuyTickets}>
              Buy Tickets
            </button>
          </>
        ) : (
          <p>No ticket types available for this event.</p>
        )}
      </div>
    </div>
  );
};

export default EventDetailsPage;
