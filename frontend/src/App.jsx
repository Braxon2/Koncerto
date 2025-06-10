import "./App.css";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import Login from "./pages/Login";
import Home from "./pages/Home";
import Navbar from "./components/Navbar";
import Register from "./pages/Register";
import EventDetailsPage from "./pages/EventDetailsPage ";
import CreateEventPage from "./pages/CreateEventPage";

function App() {
  return (
    <BrowserRouter>
      <div className="App">
        <Navbar />
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/events/:id" element={<EventDetailsPage />} />
          <Route path="/events/create" element={<CreateEventPage />} />
        </Routes>
      </div>
    </BrowserRouter>
  );
}

export default App;
