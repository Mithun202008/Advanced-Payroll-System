@echo off
cd /d "%~dp0"
echo Starting Enterprise Payroll Web SaaS Portal on http://localhost:8080 ...
start http://localhost:8080
java -cp ".;lib/ucanaccess-uber.jar" PayrollWebServer
pause
