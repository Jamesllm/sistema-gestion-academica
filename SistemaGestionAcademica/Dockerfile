# ---------------------------------------------------------------
# Imagen de ejecución de SistemaGestionAcademica
# Requiere el artefacto generado previamente por Maven:
#   ./mvnw clean package   ->   target/SistemaGestionAcademica.jar
# Construir:  docker build -t sistema-gestion-academica:1.0 .
# Ejecutar:   docker run -p 8080:8080 sistema-gestion-academica:1.0
# ---------------------------------------------------------------
FROM eclipse-temurin:17-jre

LABEL org.opencontainers.image.title="SistemaGestionAcademica" \
      org.opencontainers.image.description="Gestión de estudiantes universitarios (Spring Boot)" \
      org.opencontainers.image.version="1.0.0"

WORKDIR /app

# Usuario sin privilegios: el contenedor no se ejecuta como root
RUN groupadd --system app && useradd --system --gid app --no-create-home app

COPY target/SistemaGestionAcademica.jar app.jar
RUN chown app:app app.jar
USER app

EXPOSE 8080

# Opciones de la JVM ajustadas a los límites de memoria del contenedor
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0"

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
