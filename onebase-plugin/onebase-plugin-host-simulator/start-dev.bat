@echo off
cd /d %~dp0
echo Starting OneBase Plugin Host Simulator in DEV mode...
java -jar target\onebase-plugin-host-simulator-1.0.0-SNAPSHOT.jar --spring.profiles.active=dev
pause
