#!/bin/sh

echo "cockroach host - ${COCKROACH_HOST}"
echo "cockroach port - ${COCKROACH_PORT}"
echo "cockroach user - ${COCKROACH_USER}"
echo "cockroach insecure - ${COCKROACH_INSECURE}"
echo "cockroach insecure - ${DATABASE_NAME}"

./cockroach sql --execute="CREATE DATABASE ${DATABASE_NAME};";