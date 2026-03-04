import React, { useState, useEffect } from 'react';
import Login from './Login';
import Chat from './Chat';
import './App.css';

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (token) setIsAuthenticated(true);
  }, []);

  const handleLogin = () => setIsAuthenticated(true);
  
  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    setIsAuthenticated(false);
  };

  return isAuthenticated ? <Chat onLogout={handleLogout} /> : <Login onLogin={handleLogin} />;
}

export default App;
