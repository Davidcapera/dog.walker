#ESTE ARCHIVO DOCKERFILE ES PARA CREAR LA IMAGEN
# Etapa 1: Build - compilar el proyecto usando Maven + JDK 17
FROM maven:3.9.4-eclipse-temurin-17 AS build

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar pom.xml y descargar dependencias primero (para cachear)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiar el código fuente completo
COPY src ./src

# Construir el proyecto (skip tests para acelerar)
RUN mvn clean package -DskipTests

# Etapa 2: Runtime - crear imagen más ligera para ejecutar el .jar
FROM eclipse-temurin:17-jre

# Definir puerto en que correrá la aplicación (informativo)
EXPOSE 8080

# Copiar el archivo jar desde la etapa de build
COPY --from=build /app/target/dog.walker-0.0.1-SNAPSHOT.jar /app/dog.walker.jar

# Definir directorio de trabajo
WORKDIR /app

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "dog.walker.jar"]