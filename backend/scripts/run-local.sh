#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BACKEND_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"
ENV_FILE="${BACKEND_DIR}/.env"

if [[ ! -f "${ENV_FILE}" ]]; then
	echo "Missing ${ENV_FILE}. Copy .env.example to .env and fill in local values."
	exit 1
fi

set -a
# shellcheck disable=SC1090
source "${ENV_FILE}"
set +a

: "${DB_URL:?DB_URL is required in .env}"
: "${DB_USERNAME:?DB_USERNAME is required in .env}"
: "${DB_PASSWORD:?DB_PASSWORD is required in .env}"
: "${JWT_SECRET:?JWT_SECRET is required in .env}"

export SPRING_PROFILES_ACTIVE="${SPRING_PROFILES_ACTIVE:-local}"
SERVER_PORT="${ASSETORY_SERVER_PORT:-8080}"

cd "${BACKEND_DIR}"
exec ./mvnw spring-boot:run "-Dspring-boot.run.arguments=--server.port=${SERVER_PORT}" "$@"
