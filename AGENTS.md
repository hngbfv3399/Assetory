# Assetory Development Rules

## Architecture

- Use a layered architecture for the backend.
- Organize each feature under `controller`, `service`, `repository`, `domain`, and `dto` packages as needed.
- Controllers handle HTTP requests and responses only; they do not contain business logic.
- Services contain business logic and transaction boundaries.
- Repositories handle persistence only.
- Domain entities represent database state and domain behavior; do not expose them directly in API responses.
- Place shared response and exception code under `global`.

## API and Data Rules

- Follow the API contracts documented in Notion. Do not add or alter endpoints without a confirmed design change.
- Use the common response shape: `success`, `data`, `message`.
- Keep credentials and other sensitive values out of Git. Use environment variables for local configuration.
