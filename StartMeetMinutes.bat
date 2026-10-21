
@echo off
setlocal

REM Launch the Backend Window
start "Backend Server" powershell -NoExit -ExecutionPolicy Bypass -Command "Set-Location -LiteralPath '%~dp0backend\meet-minutes-backend'; Get-Content .env | ForEach-Object { if ($_ -match '^\s*([^#=\s]+)\s*=(.*)$') { $name = $matches[1]; $value = $matches[2].Trim(); Set-Item -Path ('Env:' + $name) -Value $value } }; .\mvnw.cmd spring-boot:run"

REM Launch the Frontend Window
start "Frontend Server" powershell -NoExit -ExecutionPolicy Bypass -Command "Set-Location -LiteralPath '%~dp0frontend'; npm run dev"

endlocal
