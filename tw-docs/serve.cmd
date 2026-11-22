@echo off
rem Serves tw-docs on http://127.0.0.1:8765 using the JDK built-in web server (JDK 18+).
cd /d "%~dp0"
where jwebserver >nul 2>nul
if %errorlevel%==0 (
  echo Twister docs: http://127.0.0.1:8765/
  jwebserver -p 8765 -b 127.0.0.1
) else (
  echo jwebserver not found, falling back to Python.
  python -m http.server 8765 --bind 127.0.0.1
)
