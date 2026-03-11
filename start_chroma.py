import chromadb
import uvicorn
from chromadb.config import Settings

print("Starting ChromaDB server on http://localhost:8000")
print("Press Ctrl+C to stop")

# Start ChromaDB server using uvicorn
if __name__ == "__main__":
    uvicorn.run(
        "chromadb.app:app",
        host="localhost",
        port=8000,
        log_level="info"
    )
