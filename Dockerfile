# 使用官方 Java 運行環境作為基礎鏡像
FROM openjdk:17-jdk-slim

# 安裝所需的依賴庫
RUN apt-get update \
    && apt-get install -y libfreetype6 fontconfig fonts-dejavu-core

# 設定容器內部工作目錄
WORKDIR /app

# 將應用的 jar 文件從構建上下文複製到容器內
COPY target/TINGLINEWS-0.0.1-SNAPSHOT.jar /app/tinglinews.jar

# 容器啟動時執行的命令，啟動 Spring Boot 應用
# CMD ["java", "-jar", "/app/tinglinews.jar"]
CMD ["java", "-Djava.awt.headless=true", "-jar", "/app/tinglinews.jar"]

# 暴露 8080 端口，供外部訪問
EXPOSE 8080