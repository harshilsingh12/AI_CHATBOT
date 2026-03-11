# Complete Setup Guide

## System Requirements

- **Operating System:** Windows 10/11, macOS, or Linux
- **Java:** JDK 17 or higher
- **Node.js:** Version 16 or higher
- **Python:** Version 3.8 or higher
- **PostgreSQL:** Version 12 or higher
- **Maven:** Version 3.6 or higher
- **RAM:** Minimum 4GB (8GB recommended)
- **Disk Space:** At least 2GB free

## Step-by-Step Installation

### 1. Install Prerequisites

#### Java 17
- Download from: https://www.oracle.com/java/technologies/downloads/
- Verify: `java -version`

#### Node.js
- Download from: https://nodejs.org/
- Verify: `node -v` and `npm -v`

#### Python
- Download from: https://www.python.org/downloads/
- Verify: `python --version`

#### PostgreSQL
- Download from: https://www.postgresql.org/download/
- During installation, remember your password
- Verify: `psql --version`

#### Maven
- Download from: https://maven.apache.org/download.cgi
- Add to PATH
- Verify: `mvn -version`

### 2. Database Setup

Open PostgreSQL command line (psql) or pgAdmin:

```sql
-- Create database
CREATE DATABASE chatbot_db;

-- Verify
\l  -- Lists all databases
```

### 3. Get API Keys

#### Groq API Key (Required)
1. Go to https://console.groq.com/
2. Sign up for free account
3. Navigate to API Keys section
4. Create new API key
5. Copy the key (starts with `gsk_`)

### 4. Backend Configuration

1. Navigate to `backend/src/main/resources/`
2. Open `application.properties`
3. Update the following:

```properties
# Update with your PostgreSQL credentials
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD

# Update with your Groq API key
groq.api.key=YOUR_GROQ_API_KEY

# Generate a secure JWT secret (any random string)
jwt.secret=your-secure-random-secret-key-here
```

### 5. Install Python Dependencies

Open terminal in project root:

```bash
# Install Flask for embedding service
pip install flask flask-cors

# Install ChromaDB
pip install chromadb uvicorn
```

### 6. Install Frontend Dependencies

```bash
cd frontend
npm install
cd ..
```

### 7. First Time Build

```bash
cd backend
mvn clean install
cd ..
```

## Running the Application

### Start Order (Important!)

You must start services in this exact order:

#### Terminal 1: Embedding Service
```bash
# Windows
start-embedding-service.bat

# Mac/Linux
python embedding_service.py
```
**Wait for:** "Starting local embedding service on http://localhost:5000"

#### Terminal 2: ChromaDB
```bash
python start_chroma.py
```
**Wait for:** Server running on http://localhost:8000

#### Terminal 3: Backend
```bash
cd backend
mvn spring-boot:run
```
**Wait for:** "Started ChatbotApplication in X seconds"

#### Terminal 4: Frontend
```bash
cd frontend
npm start
```
**Wait for:** Browser opens at http://localhost:3000

## Verification Checklist

After starting all services, verify:

- [ ] Embedding Service: http://localhost:5000/health returns `{"status":"ok"}`
- [ ] ChromaDB: http://localhost:8000/api/v1/heartbeat returns heartbeat
- [ ] Backend: http://localhost:8080/actuator/health returns status (if actuator enabled)
- [ ] Frontend: http://localhost:3000 shows login page

## Common Issues & Solutions

### Issue: Port Already in Use

**Solution:**
```bash
# Windows - Find and kill process
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Mac/Linux
lsof -ti:8080 | xargs kill -9
```

### Issue: PostgreSQL Connection Failed

**Solutions:**
1. Verify PostgreSQL is running
2. Check username/password in application.properties
3. Ensure database `chatbot_db` exists
4. Check PostgreSQL is listening on port 5432

### Issue: Maven Build Fails

**Solutions:**
1. Clear Maven cache: `mvn clean`
2. Delete `~/.m2/repository` folder
3. Run: `mvn clean install -U`

### Issue: Frontend Won't Start

**Solutions:**
1. Delete `node_modules` folder
2. Delete `package-lock.json`
3. Run: `npm install`
4. Run: `npm start`

### Issue: Embedding Service Won't Start

**Solutions:**
1. Check Python is installed: `python --version`
2. Install dependencies: `pip install flask flask-cors`
3. Check port 5000 is available

### Issue: ChromaDB Won't Start

**Solutions:**
1. Install ChromaDB: `pip install chromadb uvicorn`
2. Check port 8000 is available
3. Try: `pip install --upgrade chromadb`

## Testing the Application

### 1. Register a User
- Open http://localhost:3000
- Click "Register"
- Enter username and password
- Click "Register"

### 2. Login
- Enter your credentials
- Click "Login"

### 3. Test Chat
- Type: "Hello"
- Should receive AI response

### 4. Upload Document
- Click "Upload Document"
- Select a PDF, DOCX, or TXT file
- Wait for upload confirmation

### 5. Test Document Q&A
- Ask a question about your uploaded document
- AI should answer based on document content

## Performance Optimization

### For Better Performance:

1. **Increase JVM Memory:**
```bash
# In backend/pom.xml or when running
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xmx2048m"
```

2. **Database Connection Pool:**
Add to application.properties:
```properties
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
```

3. **Frontend Build Optimization:**
```bash
cd frontend
npm run build
# Serve with a production server
```

## Security Recommendations

### For Production:

1. **Change JWT Secret:**
   - Use a strong, random 256-bit key
   - Never commit to version control

2. **Use Environment Variables:**
```bash
# Instead of hardcoding in application.properties
export GROQ_API_KEY=your_key
export DB_PASSWORD=your_password
```

3. **Enable HTTPS:**
   - Use SSL certificates
   - Configure Spring Security for HTTPS

4. **Database Security:**
   - Use strong passwords
   - Limit database user permissions
   - Enable SSL for database connections

## Backup & Restore

### Backup Database:
```bash
pg_dump -U postgres chatbot_db > backup.sql
```

### Restore Database:
```bash
psql -U postgres chatbot_db < backup.sql
```

## Updating the Application

### Update Backend:
```bash
cd backend
git pull  # If using git
mvn clean install
mvn spring-boot:run
```

### Update Frontend:
```bash
cd frontend
git pull  # If using git
npm install
npm start
```

## Monitoring

### Check Logs:

**Backend Logs:**
- Console output shows all Spring Boot logs
- Check for errors in red

**Frontend Logs:**
- Browser console (F12)
- Check for network errors

**Database Logs:**
- PostgreSQL logs location varies by OS
- Check PostgreSQL documentation

## Next Steps

After successful setup:

1. Customize the UI in `frontend/src/`
2. Add more AI models in `ChatService.java`
3. Implement additional features
4. Deploy to production server

## Support Resources

- Spring Boot Docs: https://spring.io/projects/spring-boot
- React Docs: https://react.dev/
- Groq API Docs: https://console.groq.com/docs
- ChromaDB Docs: https://docs.trychroma.com/

## Troubleshooting Contacts

For issues:
1. Check this guide first
2. Review error messages carefully
3. Check application logs
4. Verify all services are running
