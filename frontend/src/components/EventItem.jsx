import "./styles/EventItem.css";

import { Link } from "react-router-dom";

const EventItem = ({ event }) => {
  return (
    <Link to={`/events/${event.id}`} className="event-card-link">
      <div className="event-card">
        <div className="event-image">
          <img
            src={event.imageURL || "src/images/placeholder.jpg"}
            alt={event.artist}
          />
        </div>
        <h2>{event.artist}</h2>
        <p>{new Date(event.dateTime).toLocaleDateString()}</p>
        <button className="buy-tickets-button-item">Buy Tickets</button>
      </div>
    </Link>
  );
};

export default EventItem;
