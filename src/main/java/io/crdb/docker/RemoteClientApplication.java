package io.crdb.docker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class RemoteClientApplication implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(RemoteClientApplication.class);

    private static final String COCKROACH_HOST = "COCKROACH_HOST";
    private static final String COCKROACH_PORT = "COCKROACH_PORT";
    private static final String COCKROACH_USER = "COCKROACH_USER";
    private static final String COCKROACH_INSECURE = "COCKROACH_INSECURE";
    private static final String COCKROACH_CERTS_DIR = "COCKROACH_CERTS_DIR";

    private static final String DATABASE_NAME = "DATABASE_NAME";
    private static final String DATABASE_USER = "DATABASE_USER";
    private static final String DATABASE_PASSWORD = "DATABASE_PASSWORD";
    private static final String COCKROACH_ORG = "COCKROACH_ORG";
    private static final String COCKROACH_LICENSE_KEY = "COCKROACH_LICENSE_KEY";
    private static final String COCKROACH_INIT = "COCKROACH_INIT";

    public static void main(String[] args) {
        SpringApplication.run(RemoteClientApplication.class, args);
    }

    @Autowired
    private Environment env;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        // these values/parameters correspond to CockroachDB Connection Environment variables
        final String host = env.getProperty(COCKROACH_HOST);
        final Integer port = env.getProperty(COCKROACH_PORT, Integer.class);
        final String user = env.getProperty(COCKROACH_USER);
        final Boolean insecure = env.getProperty(COCKROACH_INSECURE, Boolean.class);
        final String certsDir = env.getProperty(COCKROACH_CERTS_DIR);

        // these values/parameters do not have corresponding CockroachDB Environment variables
        final String databaseName = env.getProperty(DATABASE_NAME);
        final String databaseUser = env.getProperty(DATABASE_USER);
        final String databasePassword = env.getProperty(DATABASE_PASSWORD);
        final String licenseOrg = env.getProperty(COCKROACH_ORG);
        final String licenseKey = env.getProperty(COCKROACH_LICENSE_KEY);
        final boolean initCluster = env.getProperty(COCKROACH_INIT, Boolean.class, Boolean.FALSE);

        log.info("{} is [{}]", COCKROACH_HOST, host);
        log.info("{} is [{}]", COCKROACH_PORT, port);
        log.info("{} is [{}]", COCKROACH_USER, user);
        log.info("{} is [{}]", COCKROACH_INSECURE, insecure);
        log.info("{} is [{}]", COCKROACH_CERTS_DIR, certsDir);

        log.info("{} is [{}]", DATABASE_NAME, databaseName);
        log.info("{} is [{}]", DATABASE_USER, databaseUser);

        if (databasePassword != null) {
            log.info("{} is [{}]", DATABASE_PASSWORD, "********");
        } else {
            log.info("{} was not provided", DATABASE_PASSWORD);
        }

        log.info("{} is [{}]", COCKROACH_ORG, licenseOrg);
        log.info("{} is [{}]", COCKROACH_LICENSE_KEY, licenseKey);
        log.info("{} is [{}]", COCKROACH_INIT, initCluster);

        //Map<String, String> environment = new ProcessBuilder().environment();
        //environment.forEach((key, value) -> log.debug(key + value));

        if (initCluster) {
            List<String> commands = new ArrayList<>();
            commands.add("/cockroach");
            commands.add("init");
            commands.add("--disable-cluster-name-verification");

            ProcessBuilder builder = new ProcessBuilder(commands);
            handleProcess(builder);
        }

        if (StringUtils.hasText(databaseName)) {
            List<String> commands = new ArrayList<>();
            commands.add("/cockroach");
            commands.add("sql");
            commands.add("--execute");
            commands.add(String.format("CREATE DATABASE IF NOT EXISTS %s", databaseName));

            ProcessBuilder builder = new ProcessBuilder(commands);
            handleProcess(builder);
        }


        if (StringUtils.hasText(databaseName) && StringUtils.hasText(databaseUser) && StringUtils.hasText(databasePassword)) {
            List<String> commands = new ArrayList<>();
            commands.add("/cockroach");
            commands.add("sql");
            commands.add("--execute");
            commands.add(String.format("CREATE USER IF NOT EXISTS %s WITH PASSWORD '%s'", databaseUser, databasePassword));
            commands.add("--execute");
            commands.add(String.format("GRANT ALL ON DATABASE %s TO %s", databaseName, databaseUser));
            commands.add("--execute");
            commands.add(String.format("GRANT admin TO %s", databaseUser));

            ProcessBuilder builder = new ProcessBuilder(commands);
            handleProcess(builder);
        }


        if (StringUtils.hasText(licenseOrg) && StringUtils.hasText(licenseKey)) {
            List<String> commands = new ArrayList<>();
            commands.add("/cockroach");
            commands.add("sql");
            commands.add("--execute");
            commands.add(String.format("SET CLUSTER SETTING cluster.organization = '%s'", licenseOrg));
            commands.add("--execute");
            commands.add(String.format("SET CLUSTER SETTING enterprise.license = '%s'", licenseKey));

            ProcessBuilder builder = new ProcessBuilder(commands);
            handleProcess(builder);
        }

        handleProcess(new ProcessBuilder("/cockroach", "sql", "--execute", "SET CLUSTER SETTING server.remote_debugging.mode = 'any'"));

    }

    private void handleProcess(ProcessBuilder builder) throws IOException, InterruptedException {

        builder.inheritIO();

        String command = builder.command().toString();

        log.debug("starting command... {}", command);

        Process process = builder.start();
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException(String.format("the following command exited ABNORMALLY with code [%d]: %s", exitCode, command));
        } else {
            log.debug("command exited SUCCESSFULLY with code [{}]", exitCode);
        }

    }
}
