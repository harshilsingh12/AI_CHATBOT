-- Run this in PostgreSQL before starting the application

-- Enable pgvector extension
CREATE EXTENSION IF NOT EXISTS vector;

-- The application will auto-create tables with vector columns
