import EventItem from "../components/EventItem";
import "./styles/Home.css";
const Home = () => {
  return (
    <div className="home-container">
      <EventItem img={"src/images/karan_koncert.jpg"} />
      <EventItem img={"src/images/serifovic.jpg"} />
      <EventItem img={"src/images/ceca.jpg"} />
      <EventItem img={"src/images/dangelo_concert.jpeg"} />
    </div>
  );
};

export default Home;
