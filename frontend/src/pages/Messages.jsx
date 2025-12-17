import React, { useEffect, useState } from "react";
import { api } from "../services/api";
import { Link } from "react-router-dom";
import "../styles/messages.css";
import Footer from "../components/Footer.jsx";

export default function Messages() {
  const [messages, setMessages] = useState([]);
  const [loading, setLoading] = useState(true);

  async function load() {
    try {
      const res = await api.get("/messages");
      setMessages(res.data || []);
    } catch (err) {
      console.error(err);
      alert("Erro ao carregar mensagens");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    load();
  }, []);

  if (loading) return <div className="container">Carregando...</div>;

  async function deleteMessage(id) {
    if (!window.confirm("Delete this message?")) return;

    try {
      await api.delete(`/messages/${id}`);
      setMessages(messages.filter(m => m.id !== id));
    } catch (err) {
      console.error(err);
      alert("Erro ao deletar");
    }
  }


  return (
    <>
     <div className="messages-page">

      <Link className="new-message-btn" to="/messages/new">
        <div className="create-btn">
          <svg width="2.2rem" height="2.2rem" viewBox="-0.5 0 25 25" fill="none" xmlns="http://www.w3.org/2000/svg"><g id="SVGRepo_bgCarrier" stroke-width="0"></g><g id="SVGRepo_tracerCarrier" stroke-linecap="round" stroke-linejoin="round"></g><g id="SVGRepo_iconCarrier"> <path d="M22.0098 12.39V7.39001C22.0098 6.32915 21.5883 5.31167 20.8382 4.56152C20.0881 3.81138 19.0706 3.39001 18.0098 3.39001H6.00977C4.9489 3.39001 3.93148 3.81138 3.18134 4.56152C2.43119 5.31167 2.00977 6.32915 2.00977 7.39001V17.39C2.00977 18.4509 2.43119 19.4682 3.18134 20.2184C3.93148 20.9685 4.9489 21.39 6.00977 21.39H12.0098" stroke="#000000" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"></path> <path d="M21.209 5.41992C15.599 16.0599 8.39906 16.0499 2.78906 5.41992" stroke="#000000" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"></path> <path d="M15.0098 18.39H23.0098" stroke="#000000" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"></path> <path d="M20.0098 15.39L23.0098 18.39L20.0098 21.39" stroke="#000000" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"></path> </g></svg> Create New Message
        </div>
      </Link>

      <h2>Your Unspoken Words</h2>


      <div className="messages-list">
        {messages.length === 0 && <p className="empty">none</p>}

        {messages.map(m => (
          <div className="message-card" key={m.id}>

            <div className="delete-and-for-container">

              {/* PARA QUEM */}
              <div className="message-for">
                <span>For: <b>{m.forPerson || "—"}</b></span>
              </div>

              {/* botão delete */}
              <button className="delete-btn" onClick={() => deleteMessage(m.id)}>
                <span>✕</span>
              </button>

            </div>

            {/* CATEGORIA */}
            <div className="message-category">
              {m.category}
            </div>

            {/* CONTEÚDO */}
            <p className="message-content">{m.content}</p>

            {/* DATA */}
            <div className="message-date">
              {m.date}
            </div>

          </div>
        ))}

       </div>
     </div> 

     <Footer /> 
    </> 
  );
}
