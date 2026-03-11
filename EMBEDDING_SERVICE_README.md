# Local Embedding Service Setup

## Quick Start

1. **Start the Embedding Service:**
   - Double-click `start-embedding-service.bat`
   - Wait for "Model loaded successfully!" message
   - Keep this window open

2. **Start ChromaDB:**
   - Run `python start_chroma.py` in another terminal
   - Keep this window open

3. **Start Backend:**
   - Run `mvn spring-boot:run` in backend folder
   - Keep this window open

4. **Start Frontend:**
   - Run `npm start` in frontend folder

## What This Does

The local embedding service:
- Runs on http://localhost:5000
- Uses the same model (all-MiniLM-L6-v2) as before
- Generates 384-dimensional embeddings
- Works completely offline after initial model download
- No API keys or rate limits needed

## First Time Setup

The first time you run `start-embedding-service.bat`:
1. It will install required Python packages (~2 minutes)
2. It will download the embedding model (~500MB, one-time)
3. Then it will start the service

## Troubleshooting

**If you see "Local embedding service not available":**
- Make sure you started `start-embedding-service.bat`
- Check that port 5000 is not in use
- The backend will use dummy embeddings as fallback

**If model download is slow:**
- Be patient, it's a one-time download
- The model will be cached for future use
