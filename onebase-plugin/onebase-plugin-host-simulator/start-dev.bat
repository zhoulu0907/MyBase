@echo off
REM ========================================
REM OneBase Plugin Host Simulator (Dev Mode)
REM ========================================
REM
REM 本脚本用于在开发模式下启动插件模拟器，并启用远程调试功能
REM 开发者可以在 IDE 中连接到 localhost:5005 进行断点调试
REM

echo ========================================
echo OneBase Plugin Host Simulator (Dev Mode)
echo ========================================
echo.
echo Starting with remote debugging on port 5005...
echo You can attach your IDE debugger to localhost:5005
echo.
echo Press Ctrl+C to stop the server
echo.

REM 启动参数说明：
REM -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005
REM   - transport=dt_socket: 使用 socket 传输
REM   - server=y: JVM 作为调试服务器
REM   - suspend=n: 启动时不暂停，等待调试器连接
REM   - address=*:5005: 监听所有网络接口的 5005 端口
REM
REM --spring.profiles.active=dev: 激活 dev 配置文件

java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 ^
     -jar target\onebase-plugin-host-simulator-1.0.0-SNAPSHOT-exec.jar ^
     --spring.profiles.active=dev

pause
