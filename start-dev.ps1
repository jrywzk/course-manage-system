param(
    [switch]$OpenBrowser
)

$ErrorActionPreference = "Stop"

$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$frontendDir = Join-Path $projectRoot "front"
$backendDir = Join-Path $projectRoot "backend"

function Test-CommandExists {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Name
    )

    return $null -ne (Get-Command $Name -ErrorAction SilentlyContinue)
}

function Test-PortListening {
    param(
        [Parameter(Mandatory = $true)]
        [int]$Port
    )

    $connection = Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue | Select-Object -First 1
    return $null -ne $connection
}

function Start-ServiceWindow {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Title,
        [Parameter(Mandatory = $true)]
        [string]$WorkingDirectory,
        [Parameter(Mandatory = $true)]
        [string]$Command
    )

    $escapedDirectory = $WorkingDirectory.Replace("'", "''")
    $escapedCommand = $Command.Replace("'", "''")
    $windowCommand = "Set-Location -LiteralPath '$escapedDirectory'; `$Host.UI.RawUI.WindowTitle = '$Title'; $escapedCommand"

    Start-Process -FilePath "powershell.exe" -ArgumentList @(
        "-NoExit",
        "-ExecutionPolicy", "Bypass",
        "-Command", $windowCommand
    ) | Out-Null
}

if (-not (Test-Path $frontendDir)) {
    throw "Frontend directory not found: $frontendDir"
}

if (-not (Test-Path $backendDir)) {
    throw "Backend directory not found: $backendDir"
}

if (-not (Test-CommandExists "npm.cmd")) {
    throw "npm.cmd was not found. Make sure Node.js is installed and available in PATH."
}

if (-not (Test-CommandExists "mvn.cmd")) {
    throw "mvn.cmd was not found. Make sure Maven is installed and available in PATH."
}

$frontendRunning = Test-PortListening -Port 5173
$backendRunning = Test-PortListening -Port 9090

if ($backendRunning) {
    Write-Host "[skip] Backend is already listening on port 9090."
} else {
    Write-Host "[start] Backend: http://localhost:9090/api"
    Start-ServiceWindow -Title "course-manage-system backend" -WorkingDirectory $backendDir -Command "mvn.cmd spring-boot:run"
}

Start-Sleep -Seconds 2

if ($frontendRunning) {
    Write-Host "[skip] Frontend is already listening on port 5173."
} else {
    Write-Host "[start] Frontend: http://localhost:5173"
    Start-ServiceWindow -Title "course-manage-system front" -WorkingDirectory $frontendDir -Command "npm.cmd run dev -- --host 127.0.0.1"
}

Write-Host ""
Write-Host "Frontend URL: http://localhost:5173"
Write-Host "Backend URL: http://localhost:9090/api"
Write-Host "API Docs: http://localhost:9090/api/doc.html"

if ($OpenBrowser) {
    Start-Process "http://localhost:5173" | Out-Null
}
