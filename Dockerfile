FROM maven:3-openjdk-17-slim
EXPOSE 8080
WORKDIR /app
COPY ./ /app
RUN mvn clean package
ENTRYPOINT java -jar /app/target/es2-microsservico-equipamento.jar