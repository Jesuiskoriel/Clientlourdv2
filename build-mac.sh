#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")"

mvn -DskipTests -Djavafx.platform=mac-aarch64 clean package
cp target/clientlourdv2-1.0.0-all.jar target/billeterie-mac.jar
rm -f target/clientlourdv2-1.0.0-all.jar target/clientlourdv2-1.0.0.jar

echo "Jar macOS généré: target/billeterie-mac.jar"
