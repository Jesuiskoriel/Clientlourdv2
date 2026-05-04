@echo off
setlocal

cd /d "%~dp0"

set "JAR="
if exist "billeterie-windows.jar" set "JAR=billeterie-windows.jar"
if not defined JAR if exist "clientlourdv2-1.0.0-all.jar" set "JAR=clientlourdv2-1.0.0-all.jar"
if not defined JAR if exist "target\billeterie-windows.jar" set "JAR=target\billeterie-windows.jar"
if not defined JAR if exist "target\clientlourdv2-1.0.0-all.jar" set "JAR=target\clientlourdv2-1.0.0-all.jar"

if not defined JAR (
  echo JAR introuvable. Placez billeterie-windows.jar dans ce dossier.
  pause
  exit /b 1
)

java -jar "%JAR%"
if errorlevel 1 (
  echo.
  echo Erreur au lancement du JAR.
  pause
)

