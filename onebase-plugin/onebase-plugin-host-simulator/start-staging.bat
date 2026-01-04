@echo off
cd /d %~dp0
echo Starting OneBase Plugin Host Simulator in STAGING mode...
java -jar target\onebase-plugin-host-simulator-1.0.0-SNAPSHOT-exec.jar --spring.profiles.active=staging
pause
