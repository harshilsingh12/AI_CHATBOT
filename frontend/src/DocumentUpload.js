import React, { useState, useEffect } from 'react';
import { uploadDocument, getDocuments, deleteDocument } from './api';

function DocumentUpload() {
  const [file, setFile] = useState(null);
  const [documents, setDocuments] = useState([]);
  const [uploading, setUploading] = useState(false);

  useEffect(() => {
    loadDocuments();
  }, []);

  const loadDocuments = async () => {
    try {
      const { data } = await getDocuments();
      setDocuments(data);
    } catch (err) {
      console.error('Failed to load documents');
    }
  };

  const handleUpload = async () => {
    if (!file) return;
    setUploading(true);
    try {
      await uploadDocument(file);
      setFile(null);
      loadDocuments();
      alert('✅ Document uploaded successfully!');
    } catch (err) {
      alert('❌ Upload failed: ' + (err.response?.data || 'Unknown error'));
    }
    setUploading(false);
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this document?')) return;
    try {
      await deleteDocument(id);
      loadDocuments();
    } catch (err) {
      alert('Delete failed');
    }
  };

  return (
    <div className="doc-upload-section">
      <h3>📄 Document Upload (RAG)</h3>
      <p className="doc-info">Upload documents (.txt, .pdf, .docx) to enable context-aware responses</p>
      
      <div className="upload-box">
        <input
          type="file"
          accept=".txt,.pdf,.docx"
          onChange={(e) => setFile(e.target.files[0])}
          className="file-input"
        />
        <button onClick={handleUpload} disabled={!file || uploading} className="btn btn-upload">
          {uploading ? '⏳ Uploading...' : '📤 Upload'}
        </button>
      </div>

      <div className="doc-list">
        <h4>Uploaded Documents:</h4>
        {documents.length === 0 ? (
          <p className="doc-empty">No documents uploaded yet</p>
        ) : (
          documents.map((doc) => (
            <div key={doc.id} className="doc-item">
              <span>📄 {doc.filename}</span>
              <button onClick={() => handleDelete(doc.id)} className="btn btn-delete">
                🗑️ Delete
              </button>
            </div>
          ))
        )}
      </div>
    </div>
  );
}

export default DocumentUpload;
