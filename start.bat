@echo off
chcp 65001 >nul

echo ==========================================
echo 🚀 启动 NaiLong Platform
echo ==========================================

echo 📋 检查Java环境...
java -version
if %errorlevel% neq 0 (
    echo ❌ Java未安装或未配置环境变量
    pause
    exit /b 1
)

echo 📋 检查Maven环境...
mvn -version
if %errorlevel% neq 0 (
    echo ❌ Maven未安装或未配置环境变量
    pause
    exit /b 1
)

echo 🔨 编译项目...
mvn clean compile -DskipTests
if %errorlevel% neq 0 (
    echo ❌ 编译失败，请检查代码
    pause
    exit /b 1
)

echo 🚀 启动应用...
echo 选择启动模式：
echo 1. 开发模式 (dev,local)
echo 2. 生产模式 (prod)
echo 3. 最小模式 (minimal)
set /p choice=请输入选择 (1-3): 

if "%choice%"=="1" (
    echo 🔧 启动开发模式...
    mvn spring-boot:run -Dspring-boot.run.profiles=dev,local
) else if "%choice%"=="2" (
    echo 🏭 启动生产模式...
    mvn spring-boot:run -Dspring-boot.run.profiles=prod
) else if "%choice%"=="3" (
    echo ⚡ 启动最小模式...
    mvn spring-boot:run -Dspring-boot.run.profiles=minimal
) else (
    echo ❌ 无效选择，默认启动开发模式
    mvn spring-boot:run -Dspring-boot.run.profiles=dev,local
)

pause