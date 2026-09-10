# TinyURL Service

A URL-shortening service built with **Spring Boot**, following **Hexagonal Architecture** (Ports & Adapters), with database migrations managed by **Flyway**, and full observability (traces, metrics, logs) exported via **OTLP** to a local **Prometheus / Loki / Tempo / Grafana** stack.

---

## Tech Stack

| Concern | Technology |
|---|---|
| Language / Framework | Java, Spring Boot |
| Architecture | Hexagonal (Ports & Adapters) |
| Persistence | Spring Data JPA, PostgreSQL |
| Schema migrations | Flyway |
| API contract | OpenAPI (`contract_tinyurl.yaml`) |
| Observability | Micrometer + OpenTelemetry (`spring-boot-starter-opentelemetry`) |
| Traces backend | Grafana Tempo |
| Metrics backend | Prometheus (native OTLP receiver) |
| Logs backend | Grafana Loki (native OTLP endpoint) |
| Dashboards | Grafana |
| Local infra | Docker Compose |

---

## Project Structure

```
com.learn.tinyurl
├── adapters
│   ├── inbound
│   │   └── TinyUrlController          # REST entrypoint
│   └── outbound
│       ├── impl
│       │   └── TinyUrlRepoPortImpl    # implements the outbound port
│       └── jpa
│           ├── TinyUrlRepo            # Spring Data JPA repository
│           └── persistence
│               └── TinyUrlEntity      # JPA entity
├── domain
│   ├── model
│   │   └── TinyUrl                    # domain model (record)
│   └── service
│       └── TinyUrlService             # domain/business logic
├── otlp
│   └── InstallOpenTelemetryAppender   # wires Logback -> OpenTelemetry SDK
├── ports
│   ├── inbound
│   │   └── TinyUrlPort                # inbound port (use case interface)
│   └── outbound
│       └── TinyUrlRepoPort            # outbound port (repository interface)
├── util
│   └── DomainService                  # supporting domain annotation/util
└── TinyurlApplication                 # main entrypoint

resources
├── db.migration
│   └── V1__init_schema.sql            # Flyway migration
├── docker.local
│   ├── docker-compose.yml             # local infra: Postgres, pgAdmin, Tempo, Prometheus, Loki, Grafana
│   ├── grafana-datasources.yml        # provisions Tempo/Prometheus/Loki as Grafana datasources
│   ├── loki.yml
│   ├── prometheus.yml
│   └── tempo.yml
├── openapi
│   └── contract_tinyurl.yaml          # API contract
├── application.properties
└── logback-spring.xml                 # wires the OTel log appender
```

### Why Hexagonal Architecture

- **`ports.inbound`** defines the use case boundary (`TinyUrlPort`) — how the outside world drives the application.
- **`ports.outbound`** defines what the domain needs from the outside world (`TinyUrlRepoPort`) — e.g. persistence — without depending on any concrete technology.
- **`adapters.inbound`** implements the driving side (HTTP controller).
- **`adapters.outbound`** implements the driven side (JPA repository), keeping persistence details (entities, Spring Data) out of the domain.
- **`domain`** contains pure business logic (`TinyUrlService`, `TinyUrl` model) with no framework dependencies.

This keeps the domain independent of Spring, JPA, and HTTP — those are all adapters plugged in at the edges.

---

## Prerequisites

- Java (JDK matching your `pom.xml` target)
- Maven
- Docker & Docker Compose

---

## Running Locally

### 1. Start the infrastructure

From `resources/docker.local`:

```bash
docker compose up -d
```

This brings up:

| Service | Purpose | Port(s) |
|---|---|---|
| `postgres` | Application database | `5432` |
| `pgadmin` | DB admin UI | `5050` |
| `tempo` | Trace storage & query (OTLP receiver) | `4317` (gRPC), `4318` (HTTP), `3200` (API) |
| `prometheus` | Metrics storage & query (OTLP receiver) | `9090` |
| `loki` | Log storage & query (OTLP endpoint) | `3100` |
| `grafana` | Dashboards, querying all three backends | `3000` |

Grafana is pre-provisioned (via `grafana-datasources.yml`) with Tempo, Prometheus, and Loki as datasources — no manual setup needed. Open **http://localhost:3000**.

### 2. Run the application

Flyway runs automatically on startup (`spring.flyway.enabled=true`) and applies `V1__init_schema.sql` against the `postgres` database. Hibernate is set to `validate` only — it will **not** alter the schema; Flyway is the single source of truth for schema changes.

```bash
./mvnw spring-boot:run
```

The app starts on port `8080` (default).

---

## Configuration Reference (`application.properties`)

### Database & Flyway
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=admin
spring.datasource.password=tinyurl

spring.jpa.hibernate.ddl-auto=validate

spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=true
spring.flyway.baseline-version=0
```

### Logging pattern
```properties
logging.pattern.level=%5p [${spring.application.name:tinyurl-service},%X{traceId:-},%X{spanId:-}]
```
Micrometer Tracing populates the MDC with `traceId` / `spanId` for every request, so every log line is automatically correlated to its trace. Note: `tinyurl-service` here is only a **fallback default** — since `spring.application.name=tinyurl` is set, the actual service name used everywhere (logs, traces, metrics resource attribute) is `tinyurl`, not `tinyurl-service`.

### Traces → Tempo
```properties
management.tracing.sampling.probability=1.0
management.opentelemetry.tracing.export.otlp.enabled=true
management.opentelemetry.tracing.export.otlp.endpoint=http://localhost:4318/v1/traces
```

### Metrics → Prometheus
```properties
management.otlp.metrics.export.enabled=true
management.otlp.metrics.export.step=10s
management.otlp.metrics.export.url=http://localhost:9090/api/v1/otlp/v1/metrics
management.otlp.metrics.export.resource-attributes.service.name=${spring.application.name}
management.endpoints.web.exposure.include=health,metrics,prometheus
```
Prometheus's OTLP receiver is disabled by default and must be explicitly enabled on the Prometheus side too — see `--web.enable-otlp-receiver` in `docker-compose.yml`. Endpoint: `POST /api/v1/otlp/v1/metrics`.

### Logs → Loki
```properties
management.opentelemetry.logging.export.otlp.enabled=true
management.opentelemetry.logging.export.otlp.endpoint=http://localhost:3100/otlp/v1/logs
```
Loki's native OTLP ingestion endpoint is `POST /otlp/v1/logs`.

Unlike traces and metrics, Spring Boot does **not** auto-install a log appender — this had to be wired manually:

1. Dependency (version-aligned via the OpenTelemetry instrumentation BOMs, see below):
   ```xml
   <dependency>
       <groupId>io.opentelemetry.instrumentation</groupId>
       <artifactId>opentelemetry-logback-appender-1.0</artifactId>
   </dependency>
   ```
2. `logback-spring.xml` installs the `OpenTelemetryAppender` alongside the console appender.
3. `otlp/InstallOpenTelemetryAppender.java` connects that appender to the actual `OpenTelemetry` SDK instance at startup.

### Debugging
```properties
logging.level.io.opentelemetry=DEBUG
logging.level.io.micrometer.tracing=DEBUG
logging.level.io.micrometer=DEBUG
```

---

## Dependency Version Alignment (important)

`spring-boot-starter-opentelemetry` pulls in a specific OpenTelemetry SDK version. Adding the (alpha) Logback appender separately can pull in a *different*, incompatible `opentelemetry-api-incubator` version, causing a runtime `NoSuchMethodError` on startup. Fix by importing **both** BOMs so every OTel jar resolves to matching versions:

```xml
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>io.opentelemetry.instrumentation</groupId>
      <artifactId>opentelemetry-instrumentation-bom</artifactId>
      <version>2.28.1</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
    <dependency>
      <groupId>io.opentelemetry.instrumentation</groupId>
      <artifactId>opentelemetry-instrumentation-bom-alpha</artifactId>
      <version>2.28.1-alpha</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>
```
Do **not** pin the appender's version manually — let the BOMs resolve it.

> Note: `spring-boot-starter-opentelemetry` is designed to work **without** `spring-boot-starter-actuator`. The `management.otlp.*` / `management.opentelemetry.*` / `management.tracing.*` properties are handled by this starter's own auto-configuration. Actuator is only needed if you additionally want HTTP endpoints like `/actuator/metrics` or `/actuator/prometheus` for manual inspection.

---

## Verifying Observability in Grafana

Open **http://localhost:3000** (anonymous access, Admin role, pre-provisioned datasources).

**Traces (Tempo):** Explore → Tempo datasource → Search tab → pick `tinyurl` from the **Service Name** dropdown.

**Metrics (Prometheus):** Explore → Prometheus datasource → run:
```
target_info
```
to confirm the resource/service name Prometheus recorded, then query specific metrics, e.g.:
```
{job="tinyurl"}
```
Note: OTLP metric names are translated to Prometheus convention (dots → underscores, unit suffixes appended), e.g. `jvm.memory.used` becomes `jvm_memory_used_bytes`.

**Logs (Loki):** Explore → Loki datasource → Label browser → find the `service_name` label → query:
```
{service_name="tinyurl"}
```

**Quick reachability checks** (from the host, since compose maps ports to `localhost`):
```bash
curl -i http://localhost:9090/api/v1/otlp/v1/metrics   # expect 405 (POST-only) if receiver is up
curl -s http://localhost:9090/api/v1/status/flags | grep otlp   # confirm web.enable-otlp-receiver: true
```

---

## API Contract

See `resources/openapi/contract_tinyurl.yaml` for the full API specification.

---

## Database Migrations

Managed by Flyway, versioned under `resources/db.migration`. `V1__init_schema.sql` creates the initial schema. Add new migrations as `V2__...sql`, `V3__...sql`, etc. — never edit an already-applied migration file.
