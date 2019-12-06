#!/bin/sh

echo "found COCKROACH_HOST - [${COCKROACH_HOST}]"
echo "found COCKROACH_PORT - [${COCKROACH_PORT}]"
echo "found COCKROACH_USER - [${COCKROACH_USER}]"
echo "found COCKROACH_INSECURE - [${COCKROACH_INSECURE}]"
echo "found DATABASE_NAME - [${DATABASE_NAME}]"

./cockroach sql --execute="CREATE DATABASE ${DATABASE_NAME};";