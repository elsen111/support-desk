# 🎫 Support Desk

> A clean-architecture ticket management API built with Spring Boot 3, PostgreSQL, and JWT authentication.

![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.2-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?style=flat-square&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-ready-2496ED?style=flat-square&logo=docker&logoColor=white)
![Flyway](https://img.shields.io/badge/Flyway-migrations-CC0200?style=flat-square&logo=flyway&logoColor=white)
![License](https://img.shields.io/badge/license-MIT-blue?style=flat-square)

---

## 📖 Overview

**Support Desk** is a REST API for managing customer support tickets — think a lightweight Zendesk/Jira Service Desk core. It's built around a hexagonal (ports & adapters) architecture, with the domain model fully isolated from Spring and persistence concerns.

Three roles drive the workflow:

| Role | Can do |
|---|---|
| 🙋 `CUSTOMER` | Create tickets, view/comment on their own tickets |
| 🧑‍💻 `AGENT` | View/comment on tickets assigned to them, change status |
| 🛡️ `ADMIN` | Full access — view all tickets, assign agents, manage status |

---

## ✨ Features

- 🔐 **JWT authentication** with role-based access control
- 🎫 **Ticket lifecycle** as a finite state machine (`OPEN → IN_PROGRESS → WAITING_CUSTOMER → RESOLVED → CLOSED`)
- 💬 **Threaded comments** scoped per ticket, with access control
- 🚦 **Rate limiting** (Bucket4j) — separate limits for authenticated and anonymous traffic
- 🗄️ **Flyway-managed** PostgreSQL schema
- 📑 **OpenAPI / Swagger UI** out of the box
- 🐳 **Dockerized** with a multi-stage build and Compose setup
- 🏛️ **Hexagonal architecture** — domain layer has zero framework dependencies

---

## 🏗️ Architecture
com.supportdesk
├── domain/ # Entities, value objects, enums, domain exceptions — pure Java
├── application/ # Use cases (ports in), commands, orchestration
├── infrastructure/ # Persistence (JPA), security (JWT), rate limiting
└── web/ # REST controllers, DTOs, mappers, exception handling

---

## 🚀 Getting Started

### Prerequisites

- Docker & Docker Compose
- *(optional, for local dev without Docker)* JDK 21 and PostgreSQL 16

### Run with Docker

```bash
# clone the repo
git clone <your-repo-url>
cd support-desk

# copy the example env file and adjust as needed
cp .env.example .env

# build and start
docker compose up --build
```

The API will be available at **`http://localhost:8080`**.

### Run locally (without Docker)

```bash
./gradlew bootRun
```

Make sure `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, and `JWT_SECRET` are set as environment variables, pointing at a running PostgreSQL instance with a `support-desk` database.

### Environment variables

| Variable | Description | Example |
|---|---|---|
| `DB_URL` | JDBC connection string | `jdbc:postgresql://localhost:5432/support-desk` |
| `DB_USERNAME` | Database user | `postgres` |
| `DB_PASSWORD` | Database password | `********` |
| `JWT_SECRET` | Secret used to sign JWTs (min 32 chars) | `a-long-random-secret` |

---

## 📑 API Documentation

Once running, interactive docs are available at:

- **Swagger UI** → `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON** → `http://localhost:8080/v3/api-docs`

See [`API.md`](./API.md) for a full written reference of every endpoint.

---

## 🧪 Running Tests

```bash
./gradlew test
```

Tests run against an in-memory H2 database — no Docker or PostgreSQL required.

---

## 🗂️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.3.2 |
| Security | Spring Security + JJWT |
| Persistence | Spring Data JPA + PostgreSQL |
| Migrations | Flyway |
| Rate limiting | Bucket4j |
| API docs | springdoc-openapi |
| Build | Gradle |
| Containerization | Docker / Docker Compose |
