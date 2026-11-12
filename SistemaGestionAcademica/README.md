# SistemaGestionAcademica

Aplicación web Java (Spring Boot 3.5 · Java 17 · Maven) para gestionar estudiantes universitarios, con un proceso de **Entrega Continua** basado en Jenkins, Docker, Kubernetes y Azure Pipelines.

## Funcionalidades

| # | Funcionalidad | Web | API REST |
|---|---------------|-----|----------|
| 1 | Registrar estudiante | `GET /estudiantes/nuevo` → `POST /estudiantes` | `POST /api/estudiantes` |
| 2 | Listar estudiantes | `GET /estudiantes` | `GET /api/estudiantes` |
| 3 | Buscar estudiante | `GET /estudiantes?q=texto` | `GET /api/estudiantes?q=texto` · `GET /api/estudiantes/{id}` |
| 4 | Actualizar estudiante | `GET /estudiantes/{id}/editar` → `POST /estudiantes/{id}` | `PUT /api/estudiantes/{id}` |
| 5 | Eliminar estudiante | `POST /estudiantes/{id}/eliminar` | `DELETE /api/estudiantes/{id}` |
| 6 | Validar datos | Bean Validation + código/correo únicos | HTTP 400 / 409 con errores por campo |
| 7 | Mensajes de éxito o error | Alertas verdes / rojas | Campo `mensaje` en JSON |

Salud de la aplicación: `GET /actuator/health` (usado por Kubernetes).

## Estructura

```
SistemaGestionAcademica/
├── src/main/java/...      # model, repository, service, controller, exception, config
├── src/main/resources/    # application.properties, templates Thymeleaf, CSS
├── src/test/java/...      # 37 pruebas (JUnit 5, Mockito, MockMvc, DataJpaTest)
├── pom.xml
├── mvnw / mvnw.cmd        # Maven Wrapper (no requiere instalar Maven)
├── Dockerfile
├── Jenkinsfile
├── azure-pipelines.yml
└── kubernetes/
    ├── deployment.yaml
    └── service.yaml
```

## Ejecución local

```bash
./mvnw clean package          # compila, ejecuta las pruebas y genera target/SistemaGestionAcademica.jar
java -jar target/SistemaGestionAcademica.jar
```

En Windows usar `mvnw.cmd`. Abrir http://localhost:8080

## Docker

```bash
./mvnw clean package
docker build -t sistema-gestion-academica:1.0 .
docker run -d --name gestion-academica -p 8080:8080 sistema-gestion-academica:1.0
docker ps
```

## Kubernetes (Docker Desktop o Minikube)

```bash
kubectl apply -f kubernetes/
kubectl get deployments
kubectl get pods
kubectl get services
```

La aplicación queda en http://localhost:30080 (Docker Desktop) o en `minikube service gestion-academica-service --url`.
Con Minikube, cargar antes la imagen: `minikube image load sistema-gestion-academica:1.0`.

Prueba de autorreparación: `kubectl delete pod <nombre-pod>` y luego `kubectl get pods -w`.
Escalado: `kubectl scale deployment gestion-academica --replicas=4`.

## Jenkins

Job tipo *Pipeline* → *Pipeline script from SCM* → Git → rama `*/main` → Script Path `SistemaGestionAcademica/Jenkinsfile`.

## Azure Pipelines

*New pipeline* → GitHub → *Existing YAML file* → `/SistemaGestionAcademica/azure-pipelines.yml`.
Crear las service connections `dockerhub-connection` y `k8s-connection` y el environment `gestion-academica-dev`.

## Perfil PostgreSQL (opcional)

```bash
SPRING_PROFILES_ACTIVE=postgres DB_HOST=localhost DB_USER=postgres DB_PASSWORD=*** java -jar target/SistemaGestionAcademica.jar
```
