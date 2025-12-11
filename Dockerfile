# 第一阶段：从你的阿里云ACR拉取Maven镜像
# 注意：镜像名和标签必须与你推送的完全一致
FROM crpi-r79hyv8r4ok5ytib.cn-qingdao.personal.cr.aliyuncs.com/sszfy/base-maven:3.9-corretto-25 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# 第二阶段：从你的阿里云ACR拉取JDK镜像
FROM crpi-r79hyv8r4ok5ytib.cn-qingdao.personal.cr.aliyuncs.com/sszfy/base-openjdk:25-jdk-slim
WORKDIR /app
COPY --from=builder /app/target/jingdongmall-*.jar app.jar
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]