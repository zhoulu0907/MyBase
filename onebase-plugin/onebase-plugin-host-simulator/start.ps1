# OneBase Plugin Host Simulator 启动脚本
param(
    [ValidateSet('dev', 'staging', 'prod')]
    [string]$Mode = 'dev'
)

$ErrorActionPreference = "Stop"
$jar = "target\onebase-plugin-host-simulator-1.0.0-SNAPSHOT.jar"

if (-not (Test-Path $jar)) {
    Write-Host "错误: 找不到JAR文件 $jar" -ForegroundColor Red
    Write-Host "请先执行: mvn clean package -DskipTests" -ForegroundColor Yellow
    exit 1
}

Write-Host "============================================" -ForegroundColor Cyan
Write-Host "OneBase Plugin Host Simulator" -ForegroundColor Cyan
Write-Host "模式: $Mode" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""

# 使用 Start-Process 启动应用，重定向输出到文件
$logFile = "app-$Mode.log"
$errFile = "app-$Mode-err.log"

Write-Host "启动应用..." -ForegroundColor Green
Write-Host "标准输出日志: $logFile" -ForegroundColor Gray
Write-Host "错误日志: $errFile" -ForegroundColor Gray
Write-Host ""
Write-Host "按 Ctrl+C 停止应用" -ForegroundColor Yellow
Write-Host ""

try {
    $process = Start-Process java `
        -ArgumentList '-jar', $jar, "--spring.profiles.active=$Mode" `
        -PassThru `
        -NoNewWindow `
        -RedirectStandardOutput $logFile `
        -RedirectStandardError $errFile
    
    Write-Host "应用已启动，PID: $($process.Id)" -ForegroundColor Green
    Write-Host "正在监控日志..." -ForegroundColor Gray
    Write-Host ""
    
    # 实时显示日志
    Get-Content $logFile -Wait -Tail 20
}
finally {
    if ($process -and -not $process.HasExited) {
        Write-Host ""
        Write-Host "停止应用..." -ForegroundColor Yellow
        Stop-Process -Id $process.Id -Force
        Write-Host "应用已停止" -ForegroundColor Gray
    }
}
