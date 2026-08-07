#!/bin/bash

# NaiLong Platform 启动脚本

echo "=========================================="
echo "🚀 启动 NaiLong Platform"
echo "=========================================="

# 检查Java版本
echo "📋 检查Java环境..."
java -version

# 检查Maven
echo "📋 检查Maven环境..."
mvn -version

# 编译项目
echo "🔨 编译项目..."
mvn clean compile -DskipTests

if [ $? -ne 0 ]; then
    echo "❌ 编译失败，请检查代码"
    exit 1
fi

# 启动应用
echo "🚀 启动应用..."
echo "选择启动模式："
echo "1. 开发模式 (dev)"
echo "2. 生产模式 (prod)"
echo "3. 最小模式 (minimal)"
read -p "请输入选择 (1-3): " choice

case $choice in
    1)
        echo "🔧 启动开发模式..."
        mvn spring-boot:run -Dspring-boot.run.profiles=dev
        ;;
    2)
        echo "🏭 启动生产模式..."
        mvn spring-boot:run -Dspring-boot.run.profiles=prod
        ;;
    3)
        echo "⚡ 启动最小模式..."
        mvn spring-boot:run -Dspring-boot.run.profiles=minimal
        ;;
    *)
        echo "❌ 无效选择，默认启动开发模式"
        mvn spring-boot:run -Dspring-boot.run.profiles=dev
        ;;
esac