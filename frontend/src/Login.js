import React, { useState } from 'react';
import { login, register } from './api';

function Login({ onLogin }) {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [isRegister, setIsRegister] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const { data } = isRegister
        ? await register(username, password)
        : await login(username, password);
      localStorage.setItem('token', data.token);
      localStorage.setItem('username', data.username);
      onLogin();
    } catch (err) {
      setError(err.response?.data?.message || 'Authentication failed');
    }
  };

  return (
    <div className="login-container">
      <div className="login-card">
        <h2>🤖 {isRegister ? 'Create Account' : 'Welcome Back'}</h2>
        <form onSubmit={handleSubmit}>
          <input
            className="login-input"
            placeholder="Username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
          />
          <input
            className="login-input"
            type="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
          {error && <p className="error-message">{error}</p>}
          <button className="btn btn-login" type="submit">
            {isRegister ? 'Register' : 'Login'}
          </button>
        </form>
        <p className="toggle-text" onClick={() => setIsRegister(!isRegister)}>
          {isRegister ? 'Already have an account? Login' : "Don't have an account? Register"}
        </p>
      </div>
    </div>
  );
}

export default Login;
