# WEBAIRBNBMEJORADO - Backend (Demo)

Este es el backend de un proyecto tipo Airbnb (demo) construido con Spring Boot 3 y Java 17.
Esta guía describe de qué se trata el proyecto, cómo levantarlo con Docker Compose, cómo ejecutar la prueba `JwtUtilsTest`, y dónde encontrar/configurar la aplicación.

## Qué es este proyecto
- API REST para gestionar usuarios, propiedades, reservas, pagos, reseñas, notificaciones y favoritos.
- Seguridad basada en JWT (login / register generan tokens JWT).
- La API está organizada en controladores como `AuthController`, `PropertyController`, `ReservationController`, etc.

Estructura relevante (simplificada):

- `src/main/java/com/example/demo/controller` — controladores REST (`AuthController`, `PropertyController`, ...)
- `src/main/java/com/example/demo/config` — configuraciones (JWT, seguridad)
- `src/main/resources/application.yml` — configuración de la app
- `pom.xml` — dependencias y build

## Rutas y prefijo base
La aplicación declara un context-path en `application.yml`. El prefijo base de la API es:

`/api`

Por tanto, los endpoints se exponen así:

- Autenticación: `POST /api/auth/login`, `POST /api/auth/register`
- Propiedades: `GET /api/properties`, `GET /api/properties/{id}`, `POST /api/properties` (requiere rol `HOST`), etc.
- Otros controladores: `ReservationController`, `ReviewController`, `UserController`, `PaymentController`, `NotificationController`, `FavoriteController`, `BlockedDateController` — todos bajo rutas tipo `/api/<recurso>` (ver clases en `controller/`).

Comprueba los controladores en `src/main/java/com/example/demo/controller` para ver los mappings exactos.

## Archivo de configuración: `application.yml`
Resumen de las claves importantes (ver también `src/main/resources/application.yml`):

- `spring.profiles.active`: actualmente `dev-docker` (perfil por defecto).
- `server.port`: puerto en el que corre la app (por defecto `8080`).
- `server.servlet.context-path`: definido como `/api` — por eso la API empieza con `/api`.
- `jwt.secret`: clave secreta usada para firmar tokens JWT.
- `jwt.expiration`: tiempo en ms para expiración de tokens (ej. `86400000` = 1 día).

Ejemplo (extracto):

```yaml
spring:
  profiles:
    active: dev-docker

jwt:
  secret: "..."
  expiration: 86400000

server:
  port: 8080
  servlet:
    context-path: /api
```

Si necesitas cambiar el perfil o el puerto, modifica `application.yml` o exporta la variable de entorno correspondiente.

## Ejecutar con Docker Compose

Requisitos previos:

- Docker y Docker Compose instalados.

Levantar los servicios (imagen local / compose):

Desde la carpeta del proyecto (`demo`) en PowerShell:

```powershell
# Levanta containers (usa las imágenes definidas en docker-compose.yml)
docker-compose up

# Para levantar en segundo plano
docker-compose up -d

# Reconstruir imágenes y forzar rebuild sin cache (útil si cambias dependencias o Dockerfile)
docker-compose build --no-cache
docker-compose up -d
```

Si quieres ver logs:

```powershell
docker-compose logs -f
```

Nota: el contenedor expone el backend normalmente en el puerto mapeado en `docker-compose.yml` (revisa el archivo para confirmar el mapeo al host).

## Ejecutar localmente sin Docker

Desde la carpeta `demo`:

```powershell
# Empaquetar
.\mvnw.cmd -DskipTests package

# Ejecutar desde el wrapper maven
.\mvnw.cmd spring-boot:run

# O ejecutar el jar creado
java -jar target\demo-0.0.1-SNAPSHOT.jar
```

La aplicación arrancará en `http://localhost:8080` y las rutas de la API estarán bajo `http://localhost:8080/api/...`.

## Ejecutar la prueba `JwtUtilsTest`

La clase de test se encuentra en `src/test/java/com/example/demo/config/JwtUtilsTest.java`.

Para ejecutar solo esa prueba desde PowerShell:

```powershell
cd .\demo
.\mvnw.cmd -Dtest=com.example.demo.config.JwtUtilsTest test

# O por nombre de clase
.\mvnw.cmd -Dtest=JwtUtilsTest test
```

Para ejecutar todas las pruebas:

```powershell
.\mvnw.cmd test
```

Si usas tu IDE (IntelliJ/Eclipse/VSCode) puedes ejecutar la clase de test directamente desde el navegador de tests.

## Autenticación y uso de JWT

1. Regístrate o haz login para obtener un token JWT:

```http
POST /api/auth/register
Content-Type: application/json

{ "name": "Test", "email": "user@example.com", "password": "pwd" }
```

o

```http
POST /api/auth/login
{ "email": "user@example.com", "password": "pwd" }
```

La respuesta contiene el `token`. Para llamar endpoints protegidos añade el header:

```
Authorization: Bearer <token>
```

Ejemplo curl (PowerShell):

```powershell
$token = (curl -Method POST -Uri http://localhost:8080/api/auth/login -Body (@{email='user@example.com';password='pwd'} | ConvertTo-Json) -ContentType 'application/json').Content
# extraer y usar token según la respuesta real
curl -Uri http://localhost:8080/api/properties -Headers @{ Authorization = "Bearer $token" }
```

Protecciones típicas en los controladores:
- `@PreAuthorize("hasRole('HOST')")` en endpoints de creación/actualización/borrado de propiedades.

## Estado de la documentación (Swagger/OpenAPI)

He retirado la configuración de `springdoc` que había añadido anteriormente. Si quieres volver a exponer documentación OpenAPI, puedo añadir `springdoc-openapi` y la clase de configuración condicionada por perfil (`dev`) para que solo esté disponible en desarrollo.

## Troubleshooting rápido

- Si ves errores relacionados con dependencias o `NoSuchMethodError`, intenta limpiar y reconstruir:

```powershell
.\mvnw.cmd clean package -DskipTests
```

- Asegúrate de que no hay versiones conflictivas en tu `pom.xml`. Si usas Docker, reconstruye la imagen con `--no-cache` para que los cambios se reflejen.
