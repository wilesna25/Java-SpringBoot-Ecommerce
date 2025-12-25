# PowerShell script to load .env file
# Usage: . .\load-env.ps1

if (Test-Path .env) {
    Write-Host "Loading environment variables from .env file..." -ForegroundColor Green
    
    Get-Content .env | ForEach-Object {
        # Skip comments and empty lines
        if ($_ -and $_ -notmatch '^\s*#') {
            if ($_ -match '^([^#][^=]+)=(.*)$') {
                $name = $matches[1].Trim()
                $value = $matches[2].Trim()
                
                # Remove quotes if present
                if ($value -match '^["''](.*)["'']$') {
                    $value = $matches[1]
                }
                
                [System.Environment]::SetEnvironmentVariable($name, $value, "Process")
                Write-Host "  ✓ Loaded: $name" -ForegroundColor Gray
            }
        }
    }
    
    Write-Host "Environment variables loaded successfully!" -ForegroundColor Green
    Write-Host ""
    Write-Host "To verify, run: Get-ChildItem Env: | Where-Object Name -like 'DATABASE*'" -ForegroundColor Yellow
} else {
    Write-Host ".env file not found!" -ForegroundColor Red
    Write-Host "Please create .env file from .env.example template" -ForegroundColor Yellow
    exit 1
}

