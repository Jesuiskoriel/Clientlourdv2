#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")"

open -a "Docker"

echo "Waiting for Docker Desktop..."
for _ in $(seq 1 60); do
  if docker info >/dev/null 2>&1; then
    echo "Docker is ready."
    break
  fi
  sleep 2
done

docker compose up -d --build

sleep 2
open "http://localhost:6080/vnc.html?autoconnect=1&resize=scale"
