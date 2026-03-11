# System Architecture Documentation

## Overview

The GenAI Chatbot is a full-stack application with a microservices-inspired architecture consisting of:
- Frontend (React)
- Backend (Spring Boot)
- Embedding Service (Python Flask)
- Vector Database (ChromaDB)
- Relational Database (PostgreSQL)
- External AI API (Groq)

---

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                         Frontend                             │
│                    React (Port 3000)                         │
│                                                              │
│  ┌──────────┐  ┌──────────┐  ┌──────────────┐             │
│  │  Login   │  │   Chat   │  │   Document   │             │
│  │Component │  │Component │  │   Upload     │             │
│  └──────────┘  └──────────┘  └──────────────┘             │
└────────────────────┬─────────────────────────────────────────┘
                     │ HTTP/REST
                     ▼
┌─────────────────────────────────────────────────────────────┐
│                    Backend API Layer                         │
│              Spring Boot (Port 8080)                         │
│                                                              │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │    Auth      │  │     Chat     │  │   Document   │     │
│  │  Controller  │  │  Controller  │  │  Controller  │     │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘     │
│         │                  │                  │              │
│  ┌──────▼───────┐  ┌──────▼───────┐  ┌──────▼───────┐     │
│  │    Auth      │  │     Chat     │  │   Document   │     │
│  │   Service    │  │   Service    │  │   Service    │     │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘     │
│         │                  │                  │              │
│         │                  │                  │              │
│  ┌──────▼──────────────────▼──────────────────▼───────┐    │
│  │              JPA Repositories                       │    │
│  └──────────────────────┬──────────────────────────────┘    │
└─────────────────────────┼───────────────────────────────────┘
                          │
                          ▼
        ┌─────────────────────────────────────┐
        │      PostgreSQL Database            │
        │          (Port 5432)                │
        │                                     │
        │  ┌─────────┐  ┌──────────────┐    │
        │  │  Users  │  │  Documents   │    │
        │  └─────────┘  └──────────────┘    │
        │  ┌─────────────────────────┐      │
        │  │    Chat Messages        │      │
        │  └─────────────────────────┘      │
        └─────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│              Embedding Service                               │
│           Python Flask (Port 5000)                           │
│                                                              │
│  ┌──────────────────────────────────────────────────┐      │
│  │  Hash-based Deterministic Embedding Generator    │      │
│  │  (384 dimensions)                                │      │
│  └──────────────────────────────────────────────────┘      │
└─────────────────────────────────────────────────────────────┘
                          ▲
                          │ HTTP
                          │
┌─────────────────────────┴───────────────────────────────────┐
│                    ChromaDB                                  │
│              Vector Database (Port 8000)                     │
│                                                              │
│  ┌──────────────────────────────────────────────────┐      │
│  │  Document Embeddings Collection                  │      │
│  │  - Vector storage                                │      │
│  │  - Similarity search                             │      │
│  └──────────────────────────────────────────────────┘      │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                    External Services                         │
│                                                              │
│  ┌──────────────────────────────────────────────────┐      │
│  │           Groq API (LLM Service)                 │      │
│  │     Model: llama-3.3-70b-versatile               │      │
│  └──────────────────────────────────────────────────┘      │
└─────────────────────────────────────────────────────────────┘
```

---

## Component Details

### 1. Frontend (React)

**Technology:** React 18, Axios, CSS

**Components:**
- `App.js` - Main application component, routing logic
- `Login.js` - Authentication UI (login/register)
- `Chat.js` - Chat interface with message history
- `DocumentUpload.js` - Document management UI
- `api.js` - API client with Axios interceptors

**Key Features:**
- JWT token management
- Real-time chat interface
- File upload with progress
- Responsive design

**Port:** 3000

---

### 2. Backend (Spring Boot)

**Technology:** Spring Boot 3.2.0, Java 17, Spring Security, JPA/Hibernate

**Layers:**

#### Controllers
- `AuthController` - User registration and login
- `ChatController` - Chat message handling
- `DocumentController` - Document CRUD operations

#### Services
- `AuthService` - User authentication logic
- `ChatService` - Chat processing, Groq API integration
- `DocumentService` - Document processing, chunking
- `EmbeddingService` - Embedding generation via local service
- `ChromaService` - ChromaDB integration

#### Repositories
- `UserRepository` - User data access
- `ChatMessageRepository` - Chat history access
- `DocumentRepository` - Document metadata access
- `DocumentChunkRepository` - Document chunks access

#### Security
- `JwtUtil` - JWT token generation and validation
- `JwtAuthFilter` - Request authentication filter
- `SecurityConfig` - Security configuration

**Port:** 8080

---

### 3. Embedding Service (Python Flask)

**Technology:** Python 3.11, Flask, Flask-CORS

**Purpose:** Generate deterministic embeddings for text

**Algorithm:**
- Uses SHA-256 hash of input text
- Converts hash to 384-dimensional vector
- Deterministic: same text → same embedding
- Fast: No ML model loading required

**Endpoints:**
- `POST /embed` - Generate embedding
- `GET /health` - Health check

**Port:** 5000

---

### 4. ChromaDB (Vector Database)

**Technology:** ChromaDB, Python

**Purpose:** Store and query document embeddings

**Features:**
- Vector similarity search
- Metadata filtering
- Collection management

**Collections:**
- `document_embeddings` - Stores document chunks with embeddings

**Port:** 8000

---

### 5. PostgreSQL (Relational Database)

**Technology:** PostgreSQL 12+

**Schema:**

```sql
-- Users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);

-- Documents table
CREATE TABLE documents (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    filename VARCHAR(255) NOT NULL,
    content TEXT,
    uploaded_at TIMESTAMP NOT NULL
);

-- Chat messages table
CREATE TABLE chat_messages (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    user_message TEXT NOT NULL,
    bot_response TEXT NOT NULL,
    timestamp TIMESTAMP NOT NULL
);

-- Document chunks table (optional, not actively used)
CREATE TABLE document_chunks (
    id BIGSERIAL PRIMARY KEY,
    document_id BIGINT REFERENCES documents(id),
    content TEXT,
    embedding TEXT,
    chunk_index INTEGER
);
```

**Port:** 5432

---

## Data Flow

### 1. User Registration/Login Flow

```
User → Frontend → Backend → PostgreSQL
                    ↓
                JWT Token
                    ↓
                Frontend (localStorage)
```

### 2. Chat Message Flow (Without Documents)

```
User Message → Frontend → Backend → Groq API
                            ↓
                    PostgreSQL (save)
                            ↓
                    AI Response → Frontend
```

### 3. Chat Message Flow (With Documents)

```
User Message → Frontend → Backend
                            ↓
                    Embedding Service (generate query embedding)
                            ↓
                    ChromaDB (similarity search)
                            ↓
                    Retrieve relevant chunks
                            ↓
                    Groq API (with context)
                            ↓
                    PostgreSQL (save)
                            ↓
                    AI Response → Frontend
```

### 4. Document Upload Flow

```
File → Frontend → Backend
                    ↓
            Extract text (PDF/DOCX/TXT)
                    ↓
            Chunk text (500 chars)
                    ↓
            For each chunk:
                ↓
            Embedding Service (generate embedding)
                ↓
            ChromaDB (store chunk + embedding)
                ↓
            PostgreSQL (save document metadata)
                ↓
            Success → Frontend
```

---

## Security Architecture

### Authentication Flow

```
1. User submits credentials
2. Backend validates against PostgreSQL
3. Backend generates JWT token (24h expiration)
4. Frontend stores token in localStorage
5. All subsequent requests include token in Authorization header
6. JwtAuthFilter validates token on each request
```

### Security Features

- **Password Encryption:** BCrypt hashing
- **JWT Tokens:** HS256 algorithm
- **CORS:** Configured for localhost:3000
- **SQL Injection Prevention:** JPA/Hibernate parameterized queries
- **XSS Prevention:** React auto-escaping

---

## Scalability Considerations

### Current Limitations

1. **Single Instance:** All services run on single machine
2. **No Load Balancing:** Direct connections
3. **No Caching:** Every request hits database
4. **Synchronous Processing:** Document upload blocks

### Scaling Strategies

#### Horizontal Scaling
- Deploy multiple backend instances behind load balancer
- Use Redis for session management
- Implement database read replicas

#### Vertical Scaling
- Increase JVM heap size
- Optimize database queries
- Add database indexes

#### Async Processing
- Use message queue (RabbitMQ/Kafka) for document processing
- Implement background jobs for embedding generation

---

## Performance Metrics

### Expected Response Times

- **Login/Register:** < 500ms
- **Chat (no documents):** 1-3 seconds (Groq API latency)
- **Chat (with documents):** 2-5 seconds (includes vector search)
- **Document Upload:** 5-30 seconds (depends on file size)
- **Document List:** < 200ms

### Resource Usage

- **Backend:** ~500MB RAM
- **Frontend:** ~100MB RAM (browser)
- **PostgreSQL:** ~200MB RAM
- **ChromaDB:** ~300MB RAM
- **Embedding Service:** ~50MB RAM

---

## Monitoring & Logging

### Current Logging

- **Backend:** Console logs (Spring Boot default)
- **Frontend:** Browser console
- **Embedding Service:** Flask console logs
- **ChromaDB:** Server logs

### Recommended Additions

- **Application Monitoring:** Spring Boot Actuator
- **Log Aggregation:** ELK Stack (Elasticsearch, Logstash, Kibana)
- **Error Tracking:** Sentry
- **Performance Monitoring:** New Relic or Datadog

---

## Deployment Architecture

### Development
```
All services on localhost
- Frontend: localhost:3000
- Backend: localhost:8080
- Embedding: localhost:5000
- ChromaDB: localhost:8000
- PostgreSQL: localhost:5432
```

### Production (Recommended)
```
- Frontend: CDN (Cloudflare, AWS CloudFront)
- Backend: Container (Docker) on cloud (AWS, Azure, GCP)
- Databases: Managed services (AWS RDS, Azure Database)
- Load Balancer: AWS ALB, Nginx
- HTTPS: Let's Encrypt certificates
```

---

## Technology Choices Rationale

### Why Spring Boot?
- Mature ecosystem
- Built-in security
- Easy database integration
- Production-ready features

### Why React?
- Component-based architecture
- Large community
- Rich ecosystem
- Fast development

### Why ChromaDB?
- Easy to use
- Good for prototyping
- Built-in vector search
- Python integration

### Why PostgreSQL?
- Reliable and mature
- ACID compliance
- Good performance
- Wide adoption

### Why Groq?
- Fast inference
- Free tier available
- Good model quality
- Simple API

---

## Future Enhancements

1. **Real-time Chat:** WebSocket integration
2. **Multi-modal:** Image and audio support
3. **Advanced Search:** Full-text search with Elasticsearch
4. **Analytics:** User behavior tracking
5. **Mobile App:** React Native version
6. **API Gateway:** Centralized routing and rate limiting
7. **Microservices:** Split into smaller services
8. **Kubernetes:** Container orchestration
