Set-Location $PSScriptRoot
Write-Host "Starting Enterprise Payroll Web SaaS Portal on http://localhost:8080 ..." -ForegroundColor Cyan
Start-Process "http://localhost:8080"
java -cp ".;lib/ucanaccess-uber.jar" PayrollWebServer
