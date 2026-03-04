import React, { useState } from 'react';
import { sendMessage } from './api';
import DocumentUpload from './DocumentUpload';

function Chat({ onLogout }) {
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState('');
  const [loading, setLoading] = useState(false);
  const [showDocs, setShowDocs] = useState(false);
  const username = localStorage.getItem('username');

  const handleSend = async () => {
    if (!input.trim()) return;
    
    const userMessage = { text: input, sender: 'user' };
    setMessages([...messages, userMessage]);
    setInput('');
    setLoading(true);

    try {
      const { data } = await sendMessage(input);
      setMessages((prev) => [...prev, { text: data.response, sender: 'bot' }]);
    } catch (err) {
      setMessages((prev) => [...prev, { text: 'Error: Could not get response', sender: 'bot' }]);
    }
    setLoading(false);
  };

  return (
    <div className="chat-container">
      <div className="chat-header">
        <h2>🤖 GenAI Chatbot</h2>
        <div className="header-actions">
          <button className="btn btn-docs" onClick={() => setShowDocs(!showDocs)}>
            📄 {showDocs ? 'Hide' : 'Show'} Documents
          </button>
          <span className="username">👤 {username}</span>
          <button className="btn btn-logout" onClick={onLogout}>Logout</button>
        </div>
      </div>
      
      {showDocs && <DocumentUpload />}
      
      <div className="chat-messages">
        {messages.map((msg, idx) => (
          <div key={idx} className={`message message-${msg.sender}`}>
            <div className="message-content">{msg.text}</div>
          </div>
        ))}
        {loading && (
          <div className="message message-bot">
            <div className="message-content message-typing">Typing...</div>
          </div>
        )}
      </div>

      <div className="chat-input-container">
        <input
          className="chat-input"
          value={input}
          onChange={(e) => setInput(e.target.value)}
          onKeyPress={(e) => e.key === 'Enter' && handleSend()}
          placeholder="Type your message..."
        />
        <button className="btn btn-send" onClick={handleSend}>Send</button>
      </div>
    </div>
  );
}

export default Chat;
