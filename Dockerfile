# 1. 使用 JDK 21 基础镜像
FROM eclipse-temurin:21-jdk-alpine

# 2. 作者信息
LABEL maintainer="OpenSaasCore"

# 3. 设置工作目录
WORKDIR /app

# 4. 这里的 jar 包名字要和你 target 目录下的生成名字一致
# 根据你的 pom.xml，artifactId 是 open-sas-core，version 是 0.0.1-SNAPSHOT
COPY target/open-sas-core-0.0.1-SNAPSHOT.jar app.jar

# 5. 暴露端口
EXPOSE 8080

# 6. 启动命令
ENTRYPOINT ["java", "-jar", "app.jar"]