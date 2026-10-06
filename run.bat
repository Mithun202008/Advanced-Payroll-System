@echo off
cd /d "%~dp0"
echo Starting Enterprise Payroll Management System [SaaS Edition v3.0]...
java -cp ".;lib/ucanaccess-uber.jar" LoginFrame
pause
