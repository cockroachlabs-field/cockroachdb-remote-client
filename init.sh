#!/bin/bash

echo "You must supply connection parameters such as COCKROACH_HOST, COCKROACH_PORT and COCKROACH_USER"

if [[ -n "$COCKROACH_HOST" ]]; then
  echo "found COCKROACH_HOST [${COCKROACH_HOST}]"
fi

if [[ -n "$COCKROACH_PORT" ]]; then
  echo "found COCKROACH_PORT [${COCKROACH_PORT}]"
fi

if [[ -n "$COCKROACH_USER" ]]; then
  echo "found COCKROACH_USER [${COCKROACH_USER}]"
fi

if [[ -n "$COCKROACH_INSECURE" ]]; then
  echo "found COCKROACH_INSECURE [${COCKROACH_INSECURE}]"
fi

if [[ -n "$COCKROACH_CERTS_DIR" ]]; then
  echo "found COCKROACH_CERTS_DIR [${COCKROACH_CERTS_DIR}]"
fi

if [[ -n "$DATABASE_NAME" ]]; then
  echo "found DATABASE_NAME [${DATABASE_NAME}], creating..."
  ./cockroach sql --execute="CREATE DATABASE ${DATABASE_NAME};"
fi

if [[ -n "$COCKROACH_ORG" ]]; then
  echo "found COCKROACH_ORG [${COCKROACH_ORG}], updating cluster setting..."
  ./cockroach sql --execute="SET CLUSTER SETTING cluster.organization = '${COCKROACH_ORG}';"
fi

if [[ -n "$COCKROACH_LICENSE_KEY" ]]; then
  echo "found COCKROACH_LICENSE_KEY [${COCKROACH_LICENSE_KEY}], updating cluster setting..."
  ./cockroach sql --execute="SET CLUSTER SETTING enterprise.license = '${COCKROACH_LICENSE_KEY}';"
fi

./cockroach sql --execute="SET CLUSTER SETTING server.remote_debugging.mode = 'any';"

if [ -n "$DATABASE_USER" ] && [ -n "$DATABASE_PASSWORD" ] && [ -n "$DATABASE_NAME" ]; then
  echo "found DATABASE_USER [${DATABASE_USER}] and DATABASE_PASSWORD [${DATABASE_PASSWORD}], creating..."
  ./cockroach sql --execute="CREATE USER ${DATABASE_USER} WITH PASSWORD '${DATABASE_PASSWORD}';"
  ./cockroach sql --execute="GRANT CREATE, INSERT, DELETE, UPDATE, SELECT ON ${DATABASE_NAME} TO ${DATABASE_USER};"
fi
