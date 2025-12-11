# 第一阶段：使用Maven环境来构建JAR包
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
# 先下载依赖（利用Docker缓存层，依赖不变时不重复下载）
RUN mvn dependency:go-offline -B
COPY src ./src
# 编译并打包，跳过测试
RUN mvn clean package -DskipTests

FROM swr.cn-north-4.myhuaweicloud.com/ddn-k8s/docker.io/openjdk:25-jdk-slim
# 设置工作目录
WORKDIR /app

# 复制JAR文件
COPY target/jingdongmall-0.0.1-SNAPSHOT.jar app.jar

# 暴露端口（根据你的应用端口修改）
EXPOSE 8080

# 启动应用
ENTRYPOINT ["java", "-jar", "app.jar"]