FROM maven:3-openjdk-11-slim
EXPOSE 8080
WORKDIR /app
COPY ./ /app
RUN mvn clean package -DskipTests
ENTRYPOINT java -jar /app/target/es2-microsservico-equipamento.jar