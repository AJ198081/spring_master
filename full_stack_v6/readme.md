Full Stack V6 – Local Setup and API Test Guide

API Contract
- https://aj198081.github.io/full_stack_v6/src/main/resources/api-specs

This guide walks you through running the full_stack_v6 app locally, including its dependencies execute the API tests.

Prerequisites
- Docker Desktop installed and running
- Java 22+ (project compiles for Java 22 and sets toolchain to 23; use JDK 22 or 23)
- Maven (or use the provided mvnw/mvnw.cmd)
- GitHub account with a Personal Access Token (PAT) to let the Config Server fetch properties from GitHub
  - Ask the Admin (AJ in this case) for these credentials.
  - And add them as environment variables in your terminal/IDE.

Why these steps matter
- The application loads its properties from the centralized Config Server. 
  - Without the Config Server running and authenticated to GitHub, the app will not have the required configuration to start.
- Postgres and Mailhog are required at runtime:
  - Postgres: Liquibase needs the database up to apply migrations.
  - Mailhog: Captures outbound emails for local testing (no real emails are sent).

1) Start the Config Server first
The Config Server lives in the config_server module and fetches configuration from GitHub.

- Configure environment variables for GitHub credentials (recommended: a PAT instead of a raw password):
  - Open a PowerShell window and run:
    - setx GIT_USERNAME "<aj_github_username>"
    - setx GIT_PASSWORD "<aj_github_pat>"
  - Close and reopen terminals/IDEs so the new environment variables take effect.

- Verify the Config Server settings (for reference):
  - Module: config_server
  - Port: 8012
  - Git Repo: https://github.com/AJ198081/config_server
  - Env vars used: GIT_USERNAME, GIT_PASSWORD

Tip: Keep the Config Server running throughout local development so client apps (like full_stack_v6) can fetch configuration.

2) Start the compose.yaml file in the full_stack_v6 module, which defines Postgres and Mailhog services.

- Review the .env file at full_stack_v6/.env (defaults):
  - APPLICATION_PORT=11000 (app port)
  - POSTGRES_PORT=11001 (host port mapped to Postgres 5432)
  - POSTGRES_DB=full_stack_v6
  - POSTGRES_USER=admin
  - POSTGRES_PASSWORD=password
  - SMTP: host=localhost, port=1025

- Bring up containers (ensure Docker Desktop is running):
  - cd full_stack_v6
  - docker compose up -d

- Verify services are up:
  - Postgres: localhost:11001 (db=full_stack_v6, user=admin, password=password)
  - Mailhog UI: http://localhost:8025 (SMTP listens on 1025)

Why this order? Liquibase runs when the application starts and needs the DB ready, and email flows expect Mailhog to be accessible.

3) Run the full_stack_v6 application
- The app will start on http://localhost:11000 (from full_stack_v6/.env).

4) Running API tests
The tests are configured to use Spring’s Docker Compose support. The test profile points to the module’s compose.yaml.

- Ensure Docker Desktop is running. If you already executed docker compose up in step 2, that’s fine; otherwise Spring can also start required services when tests run.
- You can also run individual tests from your IDE (look under src/test/java/dev/aj/full_stack_v6/... ).

5) Troubleshooting
- Config fetch failures on app startup:
  - Ensure the Config Server is running on port 8012.
  - Recheck GIT_USERNAME and GIT_PASSWORD environment variables. Use a valid GitHub PAT with repo read access.
  - After setting setx variables, restart your IDE/terminal so they’re visible to the process.

- Database connection errors or Liquibase failures:
  - Make sure docker compose is up in full_stack_v6 and Postgres is healthy (localhost:11001).
  - Confirm credentials and DB name in full_stack_v6/.env.

- Emails not received:
  - Open Mailhog at http://localhost:8025. Emails are captured there for local testing.
  - Ensure SMTP_HOST=localhost and SMTP_PORT=1025 in full_stack_v6/.env.

- Ports already in use:
  - Adjust values in full_stack_v6/.env (APPLICATION_PORT, POSTGRES_PORT) and restart containers/app.

- Docker not starting from tests:
  - Confirm spring.docker.compose.file is set in src/test/resources/application-test.properties (already configured to @project.basedir@/compose.yaml).
  - Start containers manually with docker compose up -d in full_stack_v6, then re-run tests.

Quick reference
- Config Server: .\mvnw -pl config_server spring-boot:run (port 8012)
- Containers: cd full_stack_v6 && docker compose up -d
- App: .\mvnw -pl full_stack_v6 spring-boot:run (port 11000)
- Mailhog UI: http://localhost:8025
- Postgres: localhost:11001 (db=full_stack_v6, user=admin, password=password)
