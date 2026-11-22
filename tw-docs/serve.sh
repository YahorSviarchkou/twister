#!/usr/bin/env sh
# Serves tw-docs on http://127.0.0.1:8765 using the JDK built-in web server (JDK 18+), or Python as a fallback.
cd "$(dirname "$0")" || exit 1
echo "Twister docs: http://127.0.0.1:8765/"
if command -v jwebserver >/dev/null 2>&1; then
  exec jwebserver -p 8765 -b 127.0.0.1
else
  exec python3 -m http.server 8765 --bind 127.0.0.1
fi
