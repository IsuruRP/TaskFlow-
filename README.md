# TaskFlow — Smart Project Management System

TaskFlow is a portfolio-ready project and task management application inspired by Jira, Trello, and Asana. It combines a secure Spring Boot API, Microsoft SQL Server, and a responsive React dashboard.

## Features

- Registration, login, BCrypt password hashing, and signed JWT access tokens
- Admin, Project Manager, and Team Member server-side authorization
- User profile and admin user management
- Project CRUD, ownership, membership, search, and deadlines
- Task CRUD, assignment, priority, filtering, deadlines, and Kanban status changes
- Task comments and user notifications
- Dashboard delivery metrics and responsive analytics UI
- Flyway versioned SQL Server schema
- Automated backend tests, frontend lint/build, Docker, and GitHub Actions CI

## Architecture

```text
React + Axios → REST Controllers → Services → Spring Data JPA → SQL Server
                           ↘ Spring Security + JWT
```

The repository contains `backend/`, `frontend/`, `.github/`, and `docker-compose.yml`.

## Prerequisites

- Java 21
- Node.js 22+
- Microsoft SQL Server 2022 / SQL Server Express
- Git

Docker Desktop is optional and can run the entire stack.

## Database setup with SQL Server Express

Your verified local instance is `ISURURANDIKA2001\SQLEXPRESS`. In SSMS, connect to it and execute:

```sql
CREATE DATABASE TaskFlowDB;
GO
```

The application uses JDBC over TCP. Enable TCP/IP for the instance in SQL Server Configuration Manager, restart `SQL Server (SQLEXPRESS)`, and configure a fixed port such as `1433`.

Create a SQL login with access to `TaskFlowDB`, or provide an existing development login. Never put its password in Git.

## Backend configuration and startup

Open PowerShell:

```powershell
cd D:\github\TaskFlow-\backend
$env:JAVA_HOME='D:\JDK'
$env:DB_URL='jdbc:sqlserver://localhost:1433;databaseName=TaskFlowDB;encrypt=true;trustServerCertificate=true'
$env:DB_USERNAME='taskflow_user'
$env:DB_PASSWORD='your-local-password'
$env:JWT_SECRET='use-a-random-secret-with-at-least-32-characters'
$env:ADMIN_EMAIL='admin@taskflow.local'
$env:ADMIN_PASSWORD='your-strong-admin-password'
.\mvnw.cmd spring-boot:run
```

Flyway creates and upgrades the tables automatically. The optional admin variables create the first admin only when that email does not already exist.

Run tests with `.\mvnw.cmd test`. The health endpoint is `http://localhost:8080/api/health`.

## Frontend startup

In a second PowerShell window:

```powershell
cd D:\github\TaskFlow-\frontend
Copy-Item .env.example .env
npm install
npm run dev
```

Open `http://localhost:5173`.

## Run with Docker

Create a local `.env` in the repository root:

```dotenv
MSSQL_SA_PASSWORD=YourStrong!Password123
JWT_SECRET=another-random-secret-with-at-least-32-characters
ADMIN_EMAIL=admin@taskflow.local
ADMIN_PASSWORD=AnotherStrong!Password123
```

Run `docker compose up --build`, then open `http://localhost:3000`. The initialization service creates `TaskFlowDB` automatically. The `.env` file is ignored by Git.

## Main API routes

| Method | Route | Purpose |
|---|---|---|
| POST | `/api/auth/register` | Register a team member |
| POST | `/api/auth/login` | Get a JWT |
| GET/PUT | `/api/users/me` | Read/update profile |
| GET | `/api/dashboard` | Current-user metrics |
| GET/POST | `/api/projects` | Search/list or create projects |
| GET/PUT/DELETE | `/api/projects/{id}` | Project details and management |
| POST/DELETE | `/api/projects/{id}/members` | Membership management |
| GET/POST | `/api/projects/{id}/tasks` | Filter/list or create tasks |
| PUT/DELETE | `/api/tasks/{id}` | Task management |
| PATCH | `/api/tasks/{id}/status` | Kanban status transition |
| GET/POST | `/api/tasks/{id}/comments` | Task discussion |
| GET/PATCH | `/api/notifications` | Notification inbox |

Protected requests need `Authorization: Bearer <token>`.

## Security notes

- Passwords are stored only as BCrypt hashes.
- JWT secrets, database credentials, and admin credentials come from environment variables.
- Authorization is enforced on the server, not trusted to the UI.
- Public registration cannot self-assign elevated roles.
- `.env` and local application property files are ignored.

## Git workflow

Work on `development`, keep `main` stable, and merge tested work through a pull request. Suggested commit:

```text
feat: implement full TaskFlow project management platform
```
