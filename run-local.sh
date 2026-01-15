#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")"

set -a
source .env
set +a

if [ -z "${JAVA_HOME:-}" ]; then
  JAVA_HOME="/Library/Java/JavaVirtualMachines/jdk-25.jdk/Contents/Home"
fi

if [ ! -x "$JAVA_HOME/bin/java" ]; then
  echo "JAVA_HOME invalide: $JAVA_HOME" >&2
  exit 1
fi

ARGFILE=$(ls /var/folders/*/*/T/cp_*.argfile 2>/dev/null | head -n 1 || true)
if [ -z "$ARGFILE" ]; then
  echo "Argfile introuvable. Lance l'app depuis VS Code une fois pour le générer." >&2
  exit 1
fi

exec "$JAVA_HOME/bin/java" @"$ARGFILE" App
