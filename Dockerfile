FROM cockroachdb/cockroach:latest

LABEL maintainer="tjveil@gmail.com"

ADD init.sh /cockroach/
RUN chmod a+x /cockroach/init.sh

WORKDIR /cockroach/

ENTRYPOINT ["/cockroach/init.sh"]