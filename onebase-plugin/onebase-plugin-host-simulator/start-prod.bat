@echo off
cd /d %~dp0
echo Starting OneBase Plugin Host Simulator in PROD mode...
java -jar target\onebase-plugin-host-simulator-1.0.0-SNAPSHOT.jar --spring.profiles.active=prod
pause
