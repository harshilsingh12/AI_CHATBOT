# GenAI Chatbot - Spring Boot + React

A full-stack AI chatbot application with document-based Q&A, user authentication, and vector search capabilities.

## Features
- 🔐 User authentication (JWT)
- 💬 AI-powered chat (Groq LLM)
- 📄 Document upload & processing (PDF, DOCX, TXT)
- 🔍 Document-based Q&A with semantic search
- 💾 Chat history storage
- 🗄️ Vector database (ChromaDB)
- 📊 PostgreSQL database
- ⚡ Real-time responses

## Tech Stack

### Backend
- **Framework:** Spring Boot 3.2.0
- **Language:** Java 17+
- **Database:** PostgreSQL
- **Security:** Spring Security + JWT
- **ORM:** JPA/Hibernate
- **AI API:** Groq (llama-3.3-70b-versatile)

### Frontend
- **Framework:** React
- **HTTP Client:** Axios
- **Styling:** CSS

### AI & Vector Search
- **LLM:** Groq API
- **Vector DB:** ChromaDB
- **Embeddings:** Local hash-based embeddings (deterministic)
- **Document Processing:** Apache PDFBox, Apache POI

## Prerequisites

- Java 17 or higher
- Node.js 16 or higher
- PostgreSQL 12 or higher
- Python 3.8 or higher
- Maven 3.6 or higher

## Installation & Setup

### 1. Database Setup

Create PostgreSQL database:
```sql
CREATE DATABASE chatbot_db;
```

### 2. Backend Configuration

Edit `backend/src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/chatbot_db
spring.datasource.username=YOUR_POSTGRES_USERNAME
spring.datasource.password=YOUR_POSTGRES_PASSWORD

# JWT Configuration
jwt.secret=YOUR_SECRET_KEY_HERE
jwt.expiration=86400000

# Groq API Configuration
groq.api.key=YOUR_GROQ_API_KEY

# ChromaDB Configuration
chroma.url=http://localhost:8000

# File Upload Configuration
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

**Get API Keys:**
- Groq API: https://console.groq.com/keys (Free)

### 3. Start Services

You need to start 4 services in order:

#### Step 1: Start Embedding Service
```bash
# In project root directory
start-embedding-service.bat
```
Wait for: "Starting local embedding service on http://localhost:5000"

#### Step 2: Start ChromaDB
```bash
# In project root directory
python start_chroma.py
```
Wait for: ChromaDB server running on http://localhost:8000

#### Step 3: Start Backend
```bash
cd backend
mvn spring-boot:run
```
Wait for: "Started ChatbotApplication" on http://localhost:8080

#### Step 4: Start Frontend
```bash
cd frontend
npm install  # First time only
npm start
```
Opens automatically on http://localhost:3000

## Usage

1. **Register:** Create a new account
2. **Login:** Sign in with your credentials
3. **Chat:** Ask questions to the AI
4. **Upload Documents:** Upload PDF, DOCX, or TXT files
5. **Ask About Documents:** The AI will answer based on your uploaded documents

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login user

### Chat
- `POST /api/chat` - Send message (requires authentication)

### Documents
- `POST /api/documents/upload` - Upload document (requires authentication)
- `GET /api/documents` - Get user's documents (requires authentication)
- `DELETE /api/documents/{id}` - Delete document (requires authentication)

## Project Structure

```
GenAI/
├── backend/
│   ├── src/main/java/com/chatbot/
│   │   ├── config/          # Security & JWT configuration
│   │   ├── controller/      # REST controllers
│   │   ├── dto/             # Data transfer objects
│   │   ├── entity/          # JPA entities
│   │   ├── repository/      # Database repositories
│   │   └── service/         # Business logic
│   └── src/main/resources/
│       └── application.properties
├── frontend/
│   ├── public/
│   └── src/
│       ├── App.js           # Main component
│       ├── Login.js         # Login/Register
│       ├── Chat.js          # Chat interface
│       ├── DocumentUpload.js # Document management
│       └── api.js           # API client
├── embedding_service.py      # Local embedding service
├── start_chroma.py          # ChromaDB startup
├── start-embedding-service.bat
└── README.md
```

## Features in Detail

### Document Processing
- Supports PDF, DOCX, and TXT files
- Automatic text extraction
- Text chunking for better context retrieval
- Vector embeddings for semantic search

### Chat System
- Real-time AI responses
- Context-aware answers from uploaded documents
- Response limited to 117 words for conciseness
- Chat history saved to database

### Security
- JWT-based authentication
- Password encryption
- Protected API endpoints
- CORS configuration

## Troubleshooting

### Backend won't start
- Check PostgreSQL is running
- Verify database credentials in application.properties
- Ensure port 8080 is available

### Embedding service errors
- Make sure port 5000 is available
- Check Flask is installed: `pip install flask flask-cors`

### ChromaDB connection failed
- Verify ChromaDB is running on port 8000
- Check Python dependencies: `pip install chromadb uvicorn`

### Frontend can't connect
- Ensure backend is running on port 8080
- Check CORS configuration
- Verify API URLs in frontend/src/api.js

## Development

### Running Tests
```bash
cd backend
mvn test
```

### Building for Production
```bash
# Backend
cd backend
mvn clean package

# Frontend
cd frontend
npm run build
```

## Configuration Notes

- **Response Length:** AI responses are limited to 117 words (configurable in ChatService.java)
- **Max File Size:** 10MB (configurable in application.properties)
- **JWT Expiration:** 24 hours (configurable in application.properties)
- **Embedding Dimensions:** 384 (matches sentence-transformers/all-MiniLM-L6-v2)

## License

This project is for educational purposes.

## Support

For issues or questions, please check the troubleshooting section or review the code documentation.
