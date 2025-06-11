import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import "./styles/EventDetailsPage.css";

const EventDetailsPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { isAuthenticated, user, logout, isAdmin } = useAuth();

  const [event, setEvent] = useState(null);
  const [availableTicketTypes, setAvailableTicketTypes] = useState([]);
  const [selectedTicketTypeId, setSelectedTicketTypeId] = useState("");
  const [ticketQuantity, setTicketQuantity] = useState(1);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const [newTicketType, setNewTicketType] = useState("");
  const [newTicketQuantity, setNewTicketQuantity] = useState("5");
  const [newTicketPrice, setNewTicketPrice] = useState("1000");
  const [addTicketLoading, setAddTicketLoading] = useState(false);
  const [addTicketError, setAddTicketError] = useState(null);
  const [refreshTicketsTrigger, setRefreshTicketsTrigger] = useState(0);

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
          setSelectedTicketTypeId(Number(ticketsData[0].id));
        } else {
          setSelectedTicketTypeId("");
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
  }, [id, refreshTicketsTrigger]);

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

  const handleAddTicketType = async () => {
    setAddTicketError(null);
    setAddTicketLoading(true);

    const parsedQuantity = parseInt(newTicketQuantity, 10);
    const parsedPrice = parseFloat(newTicketPrice);

    if (!newTicketType.trim()) {
      setAddTicketError("Ticket type cannot be empty.");
      setAddTicketLoading(false);
      return;
    }
    if (isNaN(parsedQuantity) || parsedQuantity < 5 || parsedQuantity > 20000) {
      setAddTicketError("Quantity must be a number between 5 and 20,000.");
      setAddTicketLoading(false);
      return;
    }
    if (isNaN(parsedPrice) || parsedPrice < 1000 || parsedPrice > 500000) {
      setAddTicketError(
        "Price must be a number between 1,000 and 500,000 RSD."
      );
      setAddTicketLoading(false);
      return;
    }

    const token = localStorage.getItem("jwtToken");
    if (!token) {
      setAddTicketError("Authentication token not found. Please log in.");
      logout();
      navigate("/login");
      setAddTicketLoading(false);
      return;
    }

    const newTicketData = {
      ticketType: newTicketType.trim(),
      quantity: parsedQuantity,
      price: parsedPrice,
    };

    console.log("Attempting to add new ticket type with data:", newTicketData);

    try {
      const response = await fetch(
        `http://localhost:8080/api/v1/events/${id}/tickets`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify(newTicketData),
        }
      );

      if (!response.ok) {
        let errorMessage = "Failed to add ticket type.";
        try {
          const errorData = await response.json();
          if (errorData && errorData.message) {
            errorMessage = errorData.message;
          } else if (errorData) {
            errorMessage = JSON.stringify(errorData);
          }
        } catch (jsonError) {
          console.warn(
            "Could not parse JSON error response for add ticket, attempting to read as text."
          );
          try {
            const textError = await response.text();
            if (textError) {
              errorMessage = textError;
            }
          } catch (textReadError) {
            console.error(
              "Could not read response as text for add ticket either.",
              textReadError
            );
          }
        }
        if (response.status === 400) {
          errorMessage = `Validation Error: ${errorMessage}`;
        }
        throw new Error(errorMessage);
      }

      const addedTicket = await response.json();
      alert(`Ticket type "${addedTicket.ticketType}" added successfully!`);
      console.log("Ticket type added:", addedTicket);

      setNewTicketType("");
      setNewTicketQuantity("5");
      setNewTicketPrice("1000");

      setRefreshTicketsTrigger((prev) => prev + 1);
    } catch (err) {
      setAddTicketError(err.message);
      console.error("Error adding ticket type:", err);
    } finally {
      setAddTicketLoading(false);
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

      {isAdmin ? (
        <div className="admin-ticket-form-section">
          <h2>Add New Ticket Type for this Event</h2>
          {addTicketError && <p className="error-message">{addTicketError}</p>}
          <div className="form-group">
            <label htmlFor="new-ticket-type">Ticket Type Name:</label>
            <input
              type="text"
              id="new-ticket-type"
              value={newTicketType}
              onChange={(e) => setNewTicketType(e.target.value)}
              className="form-input"
              placeholder="e.g., VIP, Standard, Balcony"
            />
          </div>
          <div className="form-group">
            <label htmlFor="new-ticket-quantity">Quantity:</label>
            <input
              type="number"
              id="new-ticket-quantity"
              min="5"
              max="20000"
              value={newTicketQuantity}
              onChange={(e) => setNewTicketQuantity(e.target.value)}
              className="form-input"
            />
          </div>
          <div className="form-group">
            <label htmlFor="new-ticket-price">Price (RSD):</label>
            <input
              type="number"
              id="new-ticket-price"
              min="1000"
              max="500000"
              value={newTicketPrice}
              onChange={(e) => setNewTicketPrice(e.target.value)}
            />
          </div>
          <button
            className="add-ticket-button"
            onClick={handleAddTicketType}
            disabled={addTicketLoading}
          >
            {addTicketLoading ? "Adding..." : "Add Ticket Type"}
          </button>
        </div>
      ) : (
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
                      {ticketType.ticketType} - {ticketType.price.toFixed(2)}RSD
                      ({ticketType.quantity} available){" "}
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
                    setTicketQuantity(
                      Math.max(1, parseInt(e.target.value) || 1)
                    )
                  }
                  className="quantity-input"
                />
              </div>

              <p className="total-price">
                Total: RSD
                {(selectedTicket
                  ? selectedTicket.price * ticketQuantity
                  : 0
                ).toFixed(2)}
              </p>

              <button className="buy-tickets-button" onClick={handleBuyTickets}>
                Buy Tickets
              </button>
            </>
          ) : (
            <p>No ticket types available for this event.</p>
          )}
        </div>
      )}
    </div>
  );
};

export default EventDetailsPage;
