# Test POST Employee
$newEmp = @{
    code = "EMP999"
    firstName = "Test"
    lastName = "User"
    role = "Developer"
    dept = "Engineering"
    phone = "9876543210"
    address = "Bangalore, India"
} | ConvertTo-Json
$resAdd = Invoke-RestMethod -Uri "http://localhost:8080/api/employees" -Method POST -Body $newEmp -ContentType "application/json"
Write-Output "Add Employee Result: $($resAdd | ConvertTo-Json)"

# Test Toggle Status
$toggle = @{ code = "EMP999" } | ConvertTo-Json
$resToggle = Invoke-RestMethod -Uri "http://localhost:8080/api/employees/status" -Method POST -Body $toggle -ContentType "application/json"
Write-Output "Toggle Status Result: $($resToggle | ConvertTo-Json)"

# Test Leave Application
$leave = @{
    code = "EMP999"
    type = "Casual Leave"
    start = "2026-10-10"
    end = "2026-10-11"
    days = "2.0"
    reason = "Family function"
} | ConvertTo-Json
$resLeave = Invoke-RestMethod -Uri "http://localhost:8080/api/leaves" -Method POST -Body $leave -ContentType "application/json"
Write-Output "Submit Leave Result: $($resLeave | ConvertTo-Json)"

# Test Delete Employee (cleanup)
$del = @{ code = "EMP999" } | ConvertTo-Json
$resDel = Invoke-RestMethod -Uri "http://localhost:8080/api/employees/delete" -Method POST -Body $del -ContentType "application/json"
Write-Output "Delete Employee Result: $($resDel | ConvertTo-Json)"
