Set-Location $PSScriptRoot
Write-Host "Starting Enterprise Payroll Management System [SaaS Edition v3.0]..." -ForegroundColor Cyan
java -cp ".;lib/ucanaccess-uber.jar" LoginFrame
