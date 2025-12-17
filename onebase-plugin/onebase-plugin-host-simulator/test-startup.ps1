# 测试应用启动脚本
$jarPath = "target\onebase-plugin-host-simulator-1.0.0-SNAPSHOT.jar"
$profile = "dev"

Write-Host "启动应用: $jarPath with profile=$profile" -ForegroundColor Green

# 启动Java进程并获取进程对象
$process = Start-Process -FilePath "java" `
    -ArgumentList "-jar", $jarPath, "--spring.profiles.active=$profile" `
    -PassThru `
    -NoNewWindow `
    -RedirectStandardOutput "startup-output.log" `
    -RedirectStandardError "startup-error.log"

Write-Host "应用已启动，进程ID: $($process.Id)" -ForegroundColor Cyan

# 等待应用启动
Write-Host "等待8秒让应用完全启动..." -ForegroundColor Yellow
Start-Sleep -Seconds 8

# 检查进程是否还在运行
if (Get-Process -Id $process.Id -ErrorAction SilentlyContinue) {
    Write-Host "OK 应用进程正在运行" -ForegroundColor Green
    
    # 测试HTTP接口
    Write-Host "测试HTTP接口..." -ForegroundColor Yellow
    $response = curl.exe "http://localhost:8080/plugin/demo-plugin/hello?name=Test" 2>&1
    Write-Host "OK HTTP响应: $response" -ForegroundColor Green
    
    # 停止进程
    Write-Host "停止应用..." -ForegroundColor Yellow
    Stop-Process -Id $process.Id -Force
    Write-Host "应用已停止" -ForegroundColor Cyan
} else {
    Write-Host "ERROR 应用进程已退出!" -ForegroundColor Red
    Write-Host ""
    Write-Host "=== 标准输出 ===" -ForegroundColor Yellow
    if (Test-Path "startup-output.log") {
        Get-Content "startup-output.log"
    }
    Write-Host ""
    Write-Host "=== 错误输出 ===" -ForegroundColor Yellow
    if (Test-Path "startup-error.log") {
        Get-Content "startup-error.log"
    }
}
