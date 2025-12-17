# OneBase Plugin Test Script
$ErrorActionPreference = "Stop"

function Test-PluginMode {
    param([string]$Mode, [int]$WaitSeconds = 8)
    
    Write-Host "" 
    Write-Host "========================================"
    Write-Host "Testing Mode: $Mode"
    Write-Host "========================================"
    
    $jar = "target\onebase-plugin-host-simulator-1.0.0-SNAPSHOT.jar"
    $logFile = "test-$Mode.log"
    $errFile = "test-$Mode-err.log"
    
    Remove-Item $logFile -ErrorAction SilentlyContinue
    Remove-Item $errFile -ErrorAction SilentlyContinue
    
    Write-Host "Starting application..."
    $process = Start-Process java -ArgumentList '-jar', $jar, "--spring.profiles.active=$Mode" -PassThru -NoNewWindow -RedirectStandardOutput $logFile -RedirectStandardError $errFile
    
    $processId = $process.Id
    Write-Host "Process PID: $processId"
    Write-Host "Waiting $WaitSeconds seconds for startup..."
    Start-Sleep $WaitSeconds
    
    $running = Get-Process -Id $processId -ErrorAction SilentlyContinue
    if (-not $running) {
        Write-Host "FAILED: Application exited"
        if (Test-Path $errFile) {
            Write-Host "Error log:"
            Get-Content $errFile
        }
        return $false
    }
    
    Write-Host "OK: Application is running"
    Write-Host "Testing HTTP interface..."
    
    try {
        $response = curl.exe -s 'http://localhost:8080/plugin/demo-plugin/hello?name=AutoTest'
        Write-Host "Response: $response"
        Write-Host "OK: HTTP test passed"
        $success = $true
    }
    catch {
        Write-Host "FAILED: HTTP test failed: $_"
        $success = $false
    }
    finally {
        Write-Host "Stopping application..."
        Stop-Process -Id $processId -Force
        Write-Host "Application stopped"
    }
    
    return $success
}

Write-Host "OneBase Plugin Automated Tests"
Write-Host "=================================="

$results = @{}
$results['dev'] = Test-PluginMode -Mode 'dev'
$results['staging'] = Test-PluginMode -Mode 'staging'
$results['prod'] = Test-PluginMode -Mode 'prod'

Write-Host ""
Write-Host "========================================"
Write-Host "Test Results Summary"
Write-Host "========================================"

foreach ($mode in $results.Keys) {
    $status = if ($results[$mode]) { "PASSED" } else { "FAILED" }
    Write-Host "$($mode.ToUpper()): $status"
}

$allPassed = ($results.Values | Where-Object { -not $_ }).Count -eq 0
if ($allPassed) {
    Write-Host ""
    Write-Host "All tests passed!"
    exit 0
}
else {
    Write-Host ""
    Write-Host "Some tests failed, please check logs"
    exit 1
}

