# cockroachdb-remote-client

## Building the Image
```bash
docker build --no-cache -t timveil-cockroach/cockroachdb-remote-client:latest .
```

## Publishing the Image
```bash
docker push timveil-cockroach/cockroachdb-remote-client:latest
```

## Running the Image
```bash
docker run -it timveil-cockroach/cockroachdb-remote-client:latest
```

docker run --env COCKROACH_HOST=localhost:5432 --env COCKROACH_INSECURE=true --env DATABASE_NAME=test -it timveil-cockroach/cockroachdb-remote-client:latest