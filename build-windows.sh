#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")"

mvn -DskipTests -Djavafx.platform=win clean package
cp target/clientlourdv2-1.0.0-all.jar target/billeterie-windows.jar
rm -f target/clientlourdv2-1.0.0-all.jar target/clientlourdv2-1.0.0.jar

echo "Jar Windows généré: target/billeterie-windows.jar"
