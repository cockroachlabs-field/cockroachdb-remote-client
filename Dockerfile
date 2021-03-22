FROM adoptopenjdk:15-jdk as builder
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM cockroachdb/cockroach:latest as cockroach

FROM adoptopenjdk:15-jdk
COPY --from=builder dependencies/ ./
COPY --from=builder snapshot-dependencies/ ./
COPY --from=builder spring-boot-loader/ ./
COPY --from=builder application/ ./
COPY --from=cockroach /cockroach/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]