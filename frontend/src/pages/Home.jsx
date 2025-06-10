import EventItem from "../components/EventItem";
import "./styles/Home.css";
import useFetchPagination from "../hooks/useFetchPagination";

const Home = () => {
  const {
    data: events,
    loading,
    error,
    page,
    totalPages,
    setPage,
  } = useFetchPagination("http://localhost:8080/api/v1/events");

  if (loading) return <div>Loading...</div>;
  if (error) return <div>Error: {error}</div>;

  return (
    <div className="home-container">
      <div className="events-grid">
        {events.map((event) => (
          <EventItem key={event.id} event={event} />
        ))}
      </div>

      <div className="pagination">
        {Array.from({ length: totalPages }, (_, i) => (
          <button key={i} onClick={() => setPage(i)} disabled={page === i}>
            {i + 1}
          </button>
        ))}
      </div>
    </div>
  );
};

export default Home;
