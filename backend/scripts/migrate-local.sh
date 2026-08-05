#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BACKEND_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"
ENV_FILE="${BACKEND_DIR}/.env"
MIGRATION_FILE="${BACKEND_DIR}/db/migration/V20260805_01__remove_user_roles_and_add_product_sales_model.sql"

if [[ ! -f "${ENV_FILE}" ]]; then
	echo "Missing ${ENV_FILE}."
	exit 1
fi

set -a
# shellcheck disable=SC1090
source "${ENV_FILE}"
set +a

: "${DB_URL:?DB_URL is required in .env}"
: "${DB_USERNAME:?DB_USERNAME is required in .env}"
: "${DB_PASSWORD:?DB_PASSWORD is required in .env}"

db_target="${DB_URL#jdbc:mysql://}"
db_target="${db_target%%\?*}"
host_port="${db_target%%/*}"
database="${db_target#*/}"
db_host="${host_port%%:*}"
db_port="${host_port##*:}"

if [[ "${db_host}" == "${db_port}" ]]; then
	db_port="3306"
fi

MYSQL_PWD="${DB_PASSWORD}" mysql --host="${db_host}" --port="${db_port}" --user="${DB_USERNAME}" "${database}" < "${MIGRATION_FILE}"
echo "Applied $(basename "${MIGRATION_FILE}") to ${database}."
