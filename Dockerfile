# 第一阶段：使用Maven环境来构建JAR包
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
# 先下载依赖（利用Docker缓存层，依赖不变时不重复下载）
RUN mvn dependency:go-offline -B
COPY src ./src
# 编译并打包，跳过测试
RUN mvn clean package -DskipTests

# 第二阶段：运行（关键修复在这里！）
FROM swr.cn-north-4.myhuaweicloud.com/ddn-k8s/docker.io/openjdk:25-jre-slim
WORKDIR /app

# 正确：从第一阶段（builder）复制构建好的JAR包
# 注意：这里使用的是 --from=builder，路径是第一阶段容器内的路径
COPY --from=builder /app/target/jingdongmall-*.jar app.jar

# 设置时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]