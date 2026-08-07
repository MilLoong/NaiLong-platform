# 多阶段构建 - 第一阶段：编译
FROM maven:3.8.6-eclipse-temurin-17 AS build
WORKDIR /app

# 使用阿里云Maven镜像加速下载
RUN mkdir -p /root/.m2 && \
    echo '<settings><mirrors><mirror><id>aliyunmaven</id><mirrorOf>*</mirrorOf><name>阿里云公共仓库</name><url>https://maven.aliyun.com/repository/public</url></mirror></mirrors></settings>' > /root/.m2/settings.xml

# 复制pom.xml并下载依赖（利用Docker缓存）
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 复制源代码并打包
COPY src ./src
# 添加编码参数以解决文件编码问题
RUN mvn clean package -DskipTests -Dmaven.test.skip=true -Dproject.build.sourceEncoding=UTF-8 -Dfile.encoding=UTF-8

# 第二阶段：运行
FROM openjdk:17-jdk-slim
WORKDIR /app

# 复制jar包
COPY --from=build /app/target/*.jar app.jar

# 暴露端口
EXPOSE 8080

# JVM参数优化
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]