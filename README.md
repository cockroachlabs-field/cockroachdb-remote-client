# CockroachDB Remote Client
Docker image used to perform simple tasks against a CockroachDB cluster then disappear.  For example, this can be useful for creating a database or setting configuration parameters, especially when used in Kubernetes or Docker Compose.  The following `docker-compose.yml` snippet highlights how it may be used: 

```yaml
services:

  crdb-0:
    container_name: crdb-0
    hostname: crdb-0
    image: cockroachdb/cockroach:latest
    command: start --logtostderr --insecure

  crdb-1:
    container_name: crdb-1
    hostname: crdb-1
    image: cockroachdb/cockroach:latest
    command: start --logtostderr --insecure --join=crdb-0
    depends_on:
      - crdb-0

  crdb-2:
    container_name: crdb-2
    hostname: crdb-2
    image: cockroachdb/cockroach:latest
    command: start --logtostderr --insecure --join=crdb-0
    depends_on:
      - crdb-0

  lb:
    container_name: lb
    hostname: lb
    build: haproxy
    ports:
      - "5432:5432"
      - "8080:8080"
      - "8081:8081"
    links:
      - crdb-0
      - crdb-1
      - crdb-2

  crdb-init:
    container_name: crdb-init
    hostname: crdb-init
    image: timveil/cockroachdb-remote-client:latest
    environment:
      - COCKROACH_HOST=lb:5432
      - COCKROACH_INSECURE=true
      - DATABASE_NAME=test
    depends_on:
      - lb
```

The following `environment` variables are supported.  See https://www.cockroachlabs.com/docs/stable/use-the-built-in-sql-client.html#client-connection for more details.
* `COCKROACH_HOST` - CockroachDB host and port number to connect to
* `COCKROACH_PORT` - CockroachDB port if not specified by host
* `COCKROACH_USER` - CockroachDB user that will own the remote client session
* `COCKROACH_INSECURE` - Use an insecure connection.  Value must be `true`
* `DATABASE_NAME` - Name of `database` to create

## Building the Image
```bash
docker build --no-cache -t timveil/cockroachdb-remote-client:latest .
```

## Publishing the Image
```bash
docker push timveil/cockroachdb-remote-client:latest
```

## Running the Image
```bash
docker run -it timveil/cockroachdb-remote-client:latest
```

running the image with environment variables
```bash
docker run --env COCKROACH_HOST=localhost:5432 --env COCKROACH_INSECURE=true --env DATABASE_NAME=test -it timveil-cockroach/cockroachdb-remote-client:latest
```