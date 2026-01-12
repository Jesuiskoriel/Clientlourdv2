#!/usr/bin/env bash
set -euo pipefail

export DISPLAY=:0

Xvfb :0 -screen 0 1280x720x24 -ac +extension GLX +render -noreset &
sleep 1

x11vnc -display :0 -nopw -forever -shared -rfbport 5900 &
websockify --web=/usr/share/novnc/ 6080 localhost:5900 &

DB_HOST=${DB_HOST:-mysql}
DB_PORT=${DB_PORT:-3306}

if [ "${DB_WAIT:-1}" = "1" ]; then
  echo "Waiting for MySQL at ${DB_HOST}:${DB_PORT}..."
  for _ in $(seq 1 60); do
    if (echo > /dev/tcp/"${DB_HOST}"/"${DB_PORT}") >/dev/null 2>&1; then
      echo "MySQL is reachable."
      break
    fi
    sleep 2
  done
fi

exec java \
  -Dprism.order=sw \
  --module-path /app/javafx \
  --add-modules javafx.controls,javafx.fxml \
  -cp /app/app.jar \
  App
