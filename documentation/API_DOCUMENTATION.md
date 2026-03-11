# API Documentation

## Base URL
```
http://localhost:8080/api
```

## Authentication

All endpoints except `/auth/register` and `/auth/login` require JWT authentication.

### Headers
```
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

---

## Authentication Endpoints

### Register User
**POST** `/auth/register`

**Request Body:**
```json
{
  "username": "john_doe",
  "password": "securePassword123"
}
```

**Response:** `200 OK`
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "john_doe"
}
```

**Error Responses:**
- `400 Bad Request` - Username already exists
- `500 Internal Server Error` - Server error

---

### Login User
**POST** `/auth/login`

**Request Body:**
```json
{
  "username": "john_doe",
  "password": "securePassword123"
}
```

**Response:** `200 OK`
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "john_doe"
}
```

**Error Responses:**
- `401 Unauthorized` - Invalid credentials
- `500 Internal Server Error` - Server error

---

## Chat Endpoints

### Send Message
**POST** `/chat`

**Headers:**
```
Authorization: Bearer <JWT_TOKEN>
```

**Request Body:**
```json
{
  "message": "What is artificial intelligence?"
}
```

**Response:** `200 OK`
```json
{
  "response": "Artificial intelligence (AI) is the simulation of human intelligence processes by machines, especially computer systems. These processes include learning, reasoning, and self-correction..."
}
```

**Error Responses:**
- `401 Unauthorized` - Missing or invalid token
- `400 Bad Request` - Empty message
- `500 Internal Server Error` - Server error

---

## Document Endpoints

### Upload Document
**POST** `/documents/upload`

**Headers:**
```
Authorization: Bearer <JWT_TOKEN>
Content-Type: multipart/form-data
```

**Request Body:**
```
file: <PDF/DOCX/TXT file>
```

**Response:** `200 OK`
```json
{
  "message": "Document uploaded and processed with 15 chunks"
}
```

**Supported File Types:**
- PDF (.pdf)
- Word Document (.docx)
- Text File (.txt)

**Max File Size:** 10MB

**Error Responses:**
- `401 Unauthorized` - Missing or invalid token
- `400 Bad Request` - Invalid file type or empty file
- `413 Payload Too Large` - File exceeds 10MB
- `500 Internal Server Error` - Server error

---

### Get User Documents
**GET** `/documents`

**Headers:**
```
Authorization: Bearer <JWT_TOKEN>
```

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "filename": "research_paper.pdf",
    "uploadedAt": "2026-03-11T10:30:00",
    "content": "Full document text content..."
  },
  {
    "id": 2,
    "filename": "notes.txt",
    "uploadedAt": "2026-03-11T11:45:00",
    "content": "Full document text content..."
  }
]
```

**Error Responses:**
- `401 Unauthorized` - Missing or invalid token
- `500 Internal Server Error` - Server error

---

### Delete Document
**DELETE** `/documents/{id}`

**Headers:**
```
Authorization: Bearer <JWT_TOKEN>
```

**Path Parameters:**
- `id` (Long) - Document ID

**Response:** `200 OK`
```json
{
  "message": "Document deleted successfully"
}
```

**Error Responses:**
- `401 Unauthorized` - Missing or invalid token
- `404 Not Found` - Document not found
- `403 Forbidden` - Document belongs to another user
- `500 Internal Server Error` - Server error

---

## Error Response Format

All error responses follow this format:

```json
{
  "timestamp": "2026-03-11T12:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Detailed error message",
  "path": "/api/chat"
}
```

---

## Rate Limiting

Currently, no rate limiting is implemented. For production:
- Recommended: 100 requests per minute per user
- Implement using Spring Security or API Gateway

---

## CORS Configuration

Allowed Origins:
- `http://localhost:3000` (Frontend development)

Allowed Methods:
- GET, POST, PUT, DELETE, OPTIONS

Allowed Headers:
- Authorization, Content-Type

---

## JWT Token

### Token Structure
```
Header.Payload.Signature
```

### Token Expiration
- Default: 24 hours (86400000 ms)
- Configurable in `application.properties`

### Token Refresh
Currently not implemented. User must login again after expiration.

---

## Example Usage

### Using cURL

**Register:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"john","password":"pass123"}'
```

**Login:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"john","password":"pass123"}'
```

**Send Chat Message:**
```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"message":"Hello AI"}'
```

**Upload Document:**
```bash
curl -X POST http://localhost:8080/api/documents/upload \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -F "file=@document.pdf"
```

**Get Documents:**
```bash
curl -X GET http://localhost:8080/api/documents \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Delete Document:**
```bash
curl -X DELETE http://localhost:8080/api/documents/1 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## Using JavaScript (Axios)

```javascript
import axios from 'axios';

const API_URL = 'http://localhost:8080/api';

// Register
const register = async (username, password) => {
  const response = await axios.post(`${API_URL}/auth/register`, {
    username,
    password
  });
  return response.data;
};

// Login
const login = async (username, password) => {
  const response = await axios.post(`${API_URL}/auth/login`, {
    username,
    password
  });
  localStorage.setItem('token', response.data.token);
  return response.data;
};

// Send Message
const sendMessage = async (message) => {
  const token = localStorage.getItem('token');
  const response = await axios.post(`${API_URL}/chat`, 
    { message },
    { headers: { Authorization: `Bearer ${token}` } }
  );
  return response.data;
};

// Upload Document
const uploadDocument = async (file) => {
  const token = localStorage.getItem('token');
  const formData = new FormData();
  formData.append('file', file);
  
  const response = await axios.post(`${API_URL}/documents/upload`, 
    formData,
    { 
      headers: { 
        Authorization: `Bearer ${token}`,
        'Content-Type': 'multipart/form-data'
      } 
    }
  );
  return response.data;
};

// Get Documents
const getDocuments = async () => {
  const token = localStorage.getItem('token');
  const response = await axios.get(`${API_URL}/documents`, {
    headers: { Authorization: `Bearer ${token}` }
  });
  return response.data;
};

// Delete Document
const deleteDocument = async (id) => {
  const token = localStorage.getItem('token');
  await axios.delete(`${API_URL}/documents/${id}`, {
    headers: { Authorization: `Bearer ${token}` }
  });
};
```

---

## Status Codes

- `200 OK` - Request successful
- `201 Created` - Resource created successfully
- `400 Bad Request` - Invalid request data
- `401 Unauthorized` - Missing or invalid authentication
- `403 Forbidden` - Insufficient permissions
- `404 Not Found` - Resource not found
- `413 Payload Too Large` - File too large
- `500 Internal Server Error` - Server error

---

## Best Practices

1. **Always include Authorization header** for protected endpoints
2. **Store JWT token securely** (localStorage or httpOnly cookies)
3. **Handle token expiration** gracefully
4. **Validate file types** before upload
5. **Check file size** before upload (max 10MB)
6. **Handle errors** appropriately in your client
7. **Use HTTPS** in production
