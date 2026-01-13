#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")"

docker compose up -d --build

sleep 2
open "http://localhost:6080/vnc.html?autoconnect=1&resize=scale"
