$endpoints = @('/api/dashboard', '/api/employees', '/api/attendance?date=2026-10-04', '/api/leaves', '/api/payroll?month=October&year=2026', '/api/reports?type=salary', '/api/settings', '/api/audit')
foreach ($ep in $endpoints) {
    try {
        $resp = Invoke-WebRequest -Uri ("http://localhost:8080" + $ep) -UseBasicParsing
        $len = $resp.Content.Length
        $sample = $resp.Content.Substring(0, [Math]::Min(100, $len))
        Write-Output "$ep => Status $($resp.StatusCode) (Length $len): $sample"
    } catch {
        Write-Output "$ep => ERROR: $($_.Exception.Message)"
    }
}
