@echo off
chcp 65001 >nul
echo ========================================
echo   Steam 数据分析系统
echo ========================================
echo.

REM 检查 Java 是否已安装
java -version >nul 2>&1
if errorlevel 1 (
    echo [错误] 未检测到 Java 环境，请先安装 JDK 8 或更高版本。
    pause
    exit /b 1
)

echo [信息] Java 环境检测通过
echo [信息] 正在启动程序...
echo.

REM 运行 JAR
java -jar SteamSQL.jar

echo.
echo ========================================
echo   程序已退出
echo ========================================
pause