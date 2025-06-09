import "./styles/EventItem.css";
const EventItem = ({ img }) => {
  return (
    <div className="event-card">
      <div className="event-image">
        <img src={img} alt="goran_karan_event" />
      </div>
      <h2>Koncert 1</h2>
      <p>25.07.2025</p>
      <button id="buy_ticket">Buy Tickets</button>
    </div>
  );
};

export default EventItem;
