# Sử dụng một hình ảnh cơ sở Java
FROM openjdk:17

# Tạo thư mục /app trong hệ thống tệp Docker và đặt nó làm thư mục làm việc mặc định
WORKDIR /app/user-service

# Sao chép tệp JAR của ứng dụng Spring Boot vào thư mục /app trong hệ thống tệp Docker
COPY target/user-service.jar user-service.jar

# Khởi chạy ứng dụng Spring Boot khi container được khởi động
ENTRYPOINT ["java", "-jar", "user-service.jar"]