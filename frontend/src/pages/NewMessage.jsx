import React, { useState } from "react";
import { api } from "../services/api";
import { useNavigate } from "react-router-dom";
import "../styles/newmessage.css";
import toast from "react-hot-toast";  

export default function NewMessage() {

  const categories = [
    "Thanks", "Sorry", "Advice", "Love", "Confession",
    "Compliment", "Motivation", "Hurt", "Other"
  ];

  const [form, setForm] = useState({
    content: "",
    category: "",
    forPerson: "",
    date: ""
  });

  const nav = useNavigate();

  function update(e) {
    setForm({ ...form, [e.target.name]: e.target.value });
  }

  async function submit(e) {
    e.preventDefault();

    if (
      !form.content.trim() ||
      !form.category ||
      !form.forPerson.trim()
    ) {
      toast.error("Please fill in all required fields!");
      return;
    }

    try {
      await api.post("/messages", form);
      toast.success("Message saved");
      nav("/messages");
    } catch (err) {
      console.error(err);
      toast.error("Failed to create message");
    }
  }

  return (
     <div className="new-message-container">
      <form className="new-message-form" onSubmit={submit}>

        <h2>New Unspoken Words</h2>

        <textarea
          name="content"
          placeholder="What you didn't say..."
          value={form.content}
          onChange={update} 
           />
        <div className="category-select">
          {categories.map(c => (
            <label key={c} className="cat-option">
              <input
                type="radio"
                name="category"
                value={c}
                checked={form.category === c}
                onChange={update} 
                />
              <span>{c}</span>
            </label>
          ))}
        </div>


        <div className="field">
          <label htmlFor="forPerson">For:</label>
          <input
            name="forPerson"
            placeholder="Who is this for?"
            value={form.forPerson}
            onChange={update} 
            />
        </div>


        <div className="field">
          <label htmlFor="date">Date:</label>
          <input
            name="date"
            type="date"
            value={form.date}
            onChange={update} />
        </div>

        <button className="send-button" type="submit">Save Message</button>
      </form>
    </div>
  );
}
