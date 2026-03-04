# GenAI Chatbot - Spring Boot + React

A minimal chatbot application with authentication, database connectivity, and free GenAI model integration.

## Features
- User authentication (JWT)
- PostgreSQL database
- Hugging Face DialoGPT integration
- Chat history storage
- React frontend

## Prerequisites
- Java 17+
- Node.js 16+
- PostgreSQL
- Maven

## Setup

### 1. Database Setup
```sql
CREATE DATABASE chatbot_db;
```

### 2. Backend Configuration
Edit `backend/src/main/resources/application.properties`:
- Update database credentials
- Add Hugging Face API key (get free key from https://huggingface.co/settings/tokens)
- Change JWT secret for production

### 3. Run Backend
```bash
cd backend
mvn spring-boot:run
```
Backend runs on http://localhost:8080

### 4. Run Frontend
```bash
cd frontend
npm start
```
Frontend runs on http://localhost:3000

## API Endpoints

### Authentication
- POST `/api/auth/register` - Register new user
- POST `/api/auth/login` - Login user

### Chat
- POST `/api/chat` - Send message (requires authentication)

## Usage
1. Register a new account
2. Login with credentials
3. Start chatting with the AI bot
4. Chat history is automatically saved

## Tech Stack
- **Backend**: Spring Boot, Spring Security, JPA, PostgreSQL
- **Frontend**: React, Axios
- **AI Model**: Hugging Face DialoGPT-medium (free)
- **Auth**: JWT tokens
