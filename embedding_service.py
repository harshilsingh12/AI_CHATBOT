from flask import Flask, request, jsonify
from flask_cors import CORS
import requests
import hashlib
import json

app = Flask(__name__)
CORS(app)

print("Embedding service ready!")
print("Using deterministic hash-based embeddings (384 dimensions)")

@app.route('/embed', methods=['POST'])
def embed():
    try:
        data = request.json
        text = data.get('text', '')
        if not text:
            return jsonify({'error': 'No text provided'}), 400
        
        # Generate deterministic embedding from text hash
        # This creates consistent embeddings for the same text
        text_hash = hashlib.sha256(text.encode()).hexdigest()
        
        # Convert hash to 384-dimensional vector
        embedding = []
        for i in range(384):
            # Use different parts of hash for each dimension
            seed = int(text_hash[i % len(text_hash):(i % len(text_hash)) + 8], 16)
            # Normalize to [-1, 1] range
            value = (seed % 10000) / 5000.0 - 1.0
            embedding.append(value)
        
        return jsonify({'embedding': embedding})
        
    except Exception as e:
        return jsonify({'error': str(e)}), 500

@app.route('/health', methods=['GET'])
def health():
    return jsonify({'status': 'ok'})

if __name__ == '__main__':
    print("Starting local embedding service on http://localhost:5000")
    print("Using deterministic embeddings - same text always gets same embedding")
    print("Press Ctrl+C to stop")
    app.run(host='0.0.0.0', port=5000, debug=False)
