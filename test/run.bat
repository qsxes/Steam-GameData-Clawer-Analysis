@echo off
chcp 65001 >nul

echo ========================================
echo   Steam Data Analysis System
echo ========================================
echo.

java -version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] 未找到JAVA，请安装JAVA8以上版本
    pause
    exit /b 1
)

echo [INFO] 找到JAVA.
echo [INFO] 开始程序...
echo.

REM
cd /d "%~dp0"

REM 运行 JAR
java -Dfile.encoding=UTF-8 -Dsun.stdout.encoding=UTF-8 -jar SteamSQL.jar

echo.
echo ========================================
echo   Program exited.
echo ========================================
pause