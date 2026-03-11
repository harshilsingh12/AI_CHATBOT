# GenAI Chatbot - Full Stack Application

A production-ready chatbot application with JWT authentication, PostgreSQL database, RAG (Retrieval-Augmented Generation), and AI integration using Groq's Llama 3.3 70B model.

## 🚀 Features

- **User Authentication**: JWT-based secure login/register system
- **Database Integration**: PostgreSQL for persistent storage of users and chat history
- **AI-Powered Responses**: Groq API with Llama 3.3 70B model (free tier)
- **RAG Support**: Upload documents (PDF, DOCX, TXT) for context-aware responses
- **Document Management**: Upload, view, and delete documents per user
- **Modern UI**: Clean React frontend with gradient design and animations
- **RESTful API**: Spring Boot backend with proper security
- **Chat History**: All conversations saved to database

## 📋 Tech Stack

### Backend
- **Framework**: Spring Boot 3.2.0
- **Language**: Java 17
- **Database**: PostgreSQL
- **Security**: Spring Security + JWT
- **ORM**: Hibernate/JPA
- **Build Tool**: Maven
- **Document Processing**: Apache PDFBox, Apache POI

### Frontend
- **Framework**: React 18
- **HTTP Client**: Axios
- **Styling**: Modern CSS with gradients and animations
- **Build Tool**: Create React App

### AI Integration
- **Provider**: Groq Cloud
- **Model**: Llama 3.3 70B Versatile
- **API**: OpenAI-compatible REST API

## 📁 Project Structure

```
GenAI/
├── backend/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/chatbot/
│   │       │   ├── ChatbotApplication.java
│   │       │   ├── config/
│   │       │   │   ├── JwtAuthFilter.java
│   │       │   │   ├── JwtUtil.java
│   │       │   │   └── SecurityConfig.java
│   │       │   ├── controller/
│   │       │   │   ├── AuthController.java
│   │       │   │   └── ChatController.java
│   │       │   ├── dto/
│   │       │   │   ├── AuthRequest.java
│   │       │   │   ├── AuthResponse.java
│   │       │   │   ├── ChatRequest.java
│   │       │   │   └── ChatResponse.java
│   │       │   ├── entity/
│   │       │   │   ├── User.java
│   │       │   │   ├── ChatMessage.java
│   │       │   │   └── Document.java
│   │       │   ├── repository/
│   │       │   │   ├── UserRepository.java
│   │       │   │   ├── ChatMessageRepository.java
│   │       │   │   └── DocumentRepository.java
│   │       │   └── service/
│   │       │       ├── AuthService.java
│   │       │       ├── ChatService.java
│   │       │       └── DocumentService.java
│   │       └── resources/
│   │           └── application.properties
│   └── pom.xml
│
├── frontend/
│   ├── src/
│   │   ├── App.js
│   │   ├── App.css
│   │   ├── Login.js
│   │   ├── Chat.js
│   │   ├── DocumentUpload.js
│   │   ├── api.js
│   │   └── index.js
│   ├── public/
│   └── package.json
│
└── README.md
```

## 🛠️ Setup Instructions

### Prerequisites
- Java 17+
- Node.js 16+
- PostgreSQL 12+
- Maven 3.6+

### 1. Database Setup

```sql
CREATE DATABASE chatbot_db;
```

### 2. Backend Configuration

Edit `backend/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/chatbot_db
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD
jwt.secret=YOUR_JWT_SECRET_KEY
groq.api.key=YOUR_GROQ_API_KEY
```

**Get Groq API Key (Free):**
1. Visit https://console.groq.com/
2. Sign up for free account
3. Navigate to API Keys
4. Create new key and copy it

### 3. Run Backend

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

Backend runs on: http://localhost:8080

### 4. Run Frontend

```bash
cd frontend
npm install
npm start
```

Frontend runs on: http://localhost:3000

## 🔌 API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
  ```json
  {
    "username": "user123",
    "password": "password123"
  }
  ```

- `POST /api/auth/login` - Login user
  ```json
  {
    "username": "user123",
    "password": "password123"
  }
  ```
  Response:
  ```json
  {
    "token": "jwt_token_here",
    "username": "user123"
  }
  ```

### Chat
- `POST /api/chat` - Send message (requires JWT token)
  ```json
  {
    "message": "What is the universe?"
  }
  ```
  Response:
  ```json
  {
    "response": "The universe is all of space and time..."
  }
  ```

### Documents
- `POST /api/documents/upload` - Upload document (requires JWT token)
  - Form data with file field
  - Supports: .txt, .pdf, .docx
  
- `GET /api/documents` - Get user's documents (requires JWT token)
  
- `DELETE /api/documents/{id}` - Delete document (requires JWT token)

## 🗄️ Database Schema

### Users Table
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);
```

### Chat Messages Table
```sql
CREATE TABLE chat_messages (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    user_message TEXT,
    bot_response TEXT,
    timestamp TIMESTAMP
);
```

### Documents Table
```sql
CREATE TABLE documents (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    filename VARCHAR(255),
    content TEXT,
    uploaded_at TIMESTAMP
);
```

## 🔐 Security Features

- **Password Encryption**: BCrypt hashing
- **JWT Authentication**: Stateless token-based auth
- **CORS Configuration**: Configured for localhost:3000
- **SQL Injection Prevention**: JPA parameterized queries
- **Session Management**: Stateless (no server-side sessions)

## 🎯 Key Components

### Backend

**JwtUtil.java** - JWT token generation and validation
- Token expiration: 24 hours
- HS256 signing algorithm

**SecurityConfig.java** - Spring Security configuration
- Public endpoints: `/api/auth/**`
- Protected endpoints: All others
- CORS enabled for React frontend

**ChatService.java** - AI integration
- Groq API integration with Llama 3.3 70B
- RAG implementation with document context
- Fallback responses for API failures
- Chat history persistence

**DocumentService.java** - Document processing
- Text extraction from PDF, DOCX, TXT
- Document storage and retrieval
- Per-user document management

### Frontend

**App.js** - Main component with authentication state
**App.css** - Modern styling with gradients and animations
**Login.js** - Login/Register form with toggle
**Chat.js** - Chat interface with message history
**DocumentUpload.js** - Document upload and management UI
**api.js** - Axios configuration with JWT interceptor

## 🚀 Usage

1. Open http://localhost:3000
2. Click "Don't have an account? Register"
3. Create account with username and password
4. Login with credentials
5. Start chatting with AI bot
6. **Optional**: Click "Show Documents" to upload files for RAG
7. Upload documents (.txt, .pdf, .docx) for context-aware responses
8. All messages and documents are saved to database

## 📊 AI Model Details

**Model**: Llama 3.3 70B Versatile
- **Parameters**: 70 billion
- **Context Window**: 8,192 tokens
- **Max Output**: 150 tokens (configurable)
- **Temperature**: 0.7 (balanced creativity)
- **Provider**: Groq (ultra-fast inference)

## 🔧 Configuration Options

### Backend (application.properties)
- `server.port` - Change backend port (default: 8080)
- `jwt.expiration` - Token validity in milliseconds
- `spring.jpa.show-sql` - Show SQL queries in logs
- `spring.jpa.hibernate.ddl-auto` - Database schema management

### Frontend (api.js)
- `API_URL` - Backend URL (default: http://localhost:8080/api)

## 🐛 Troubleshooting

**Backend won't start:**
- Check PostgreSQL is running
- Verify database credentials in application.properties
- Ensure port 8080 is available

**Frontend can't connect:**
- Verify backend is running on port 8080
- Check CORS configuration in SecurityConfig.java
- Clear browser cache and localStorage

**AI responses not working:**
- Verify Groq API key is valid
- Check internet connection
- Review backend logs for API errors

## 📝 Future Enhancements

- [x] Add document upload for RAG
- [x] Support PDF and DOCX files
- [x] Modern UI with animations
- [ ] Add chat history view
- [ ] Implement message editing/deletion
- [ ] Add user profile management
- [ ] Add conversation export
- [ ] Implement real-time updates with WebSocket
- [ ] Add multiple AI model selection
- [ ] Implement rate limiting
- [ ] Add vector database for better RAG
- [ ] Support image uploads

## 📄 License

This project is open source and available for educational purposes.

## 👨‍💻 Author

Created as a full-stack GenAI chatbot demonstration project.

---

**Note**: Remember to keep your API keys and database credentials secure. Never commit them to public repositories.
