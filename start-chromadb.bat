@echo off
echo Installing ChromaDB...
pip install chromadb

echo Starting ChromaDB server...
chroma run --host localhost --port 8000

pause
