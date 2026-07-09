# RateMe

RateMe is a small web application for rating restaurants, cafés, and pubs ("Kneipen") in and around Zweibrücken. Users register or log in, pick a location from an OpenStreetMap-based map, and leave a rating (1-5 stars, a comment, and an optional photo).

Backend: Spring Boot REST API (Java 21, JPA via `EntityManager`, no Spring Data/Spring Security). Frontend: plain HTML/CSS/JavaScript with Leaflet for the map, served by the backend itself as a static resource. Database: MySQL, schema and POI data pre-loaded on first start.

Only the database and the REST backend run in Docker containers, as specified. The frontend lives inside the backend's (`Application/`) `src/main/resources/static` folder, so Spring Boot serves it automatically on the same origin as the API — no separate container, port, or CORS workaround needed for normal use.

## Prerequisites

- [Docker](https://www.docker.com/) and Docker Compose (bundled with Docker Desktop)

No local Java or Maven installation is required — everything runs inside containers.

## Running the app

From the project root:

```bash
docker compose up --build
```

This builds and starts two containers:

| Service | Container | URL |
| --- | --- | --- |
| App (frontend + REST API) | `rateme-backend` | http://localhost:8080 |
| Database | `rateme-db` | `localhost:3307` (MySQL) |

The backend waits for the database healthcheck before starting, and the schema/seed data (POIs) load automatically on the database's first start.

Open **http://localhost:8080** in a browser — this serves the frontend and is also the base URL the frontend calls for the REST API.

To stop:

```bash
docker compose down
```

To stop and also delete the database volume (fresh state on next start):

```bash
docker compose down -v
```

### Rebuilding after code changes

`docker compose up` reuses the previously built image. Since the frontend is bundled into the backend jar, both backend *and* frontend changes require a rebuild:

```bash
docker compose up -d --build
```

## API documentation

With the backend running, the OpenAPI/Swagger UI is available at:

http://localhost:8080/swagger-ui.html

## Logs

The backend writes `rateme-backend.log` to the `logs/` folder on the host (mounted into the container), not inside the container itself.

## Running backend tests

The DAO tests connect to the real database on `localhost:3307`, so start the database container first:

```bash
docker compose up -d db
cd Application
./mvnw test
```

## Project structure

```text
Application/
  src/main/java/...            Spring Boot REST API (controller / service / dao / entity / dto / auth)
  src/main/resources/static/   Frontend: HTML/CSS/JS, served automatically by Spring Boot
db/                             MySQL Docker image with schema + seed data (db/initdb)
docker-compose.yml
```
