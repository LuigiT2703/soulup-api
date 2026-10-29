# Etapa 1: compila o projeto com Maven + JDK 21
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q dependency:go-offline
COPY src ./src
RUN mvn -q package -DskipTests

# Etapa 2: imagem leve so com o JRE 21 para rodar o .jar
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/soulup-api.jar app.jar
# O Render informa a porta pela variavel PORT (o application.properties le ela)
EXPOSE 8080
# Limita a memoria para caber no plano gratis (512 MB)
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]
