@echo off
echo ========================================
echo Local Embedding Service Setup
echo ========================================
echo.
echo Installing required packages...
pip install flask flask-cors requests
echo.
echo ========================================
echo Starting Embedding Service
echo ========================================
echo.
python embedding_service.py
pause
