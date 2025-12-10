FROM swr.cn-north-4.myhuaweicloud.com/ddn-k8s/docker.io/openjdk:25-jdk-slim
# 设置工作目录
WORKDIR /app

# 复制JAR文件
COPY target/jingdongmall-0.0.1-SNAPSHOT.jar app.jar

# 暴露端口（根据你的应用端口修改）
EXPOSE 8080

# 启动应用
ENTRYPOINT ["java", "-jar", "app.jar"]