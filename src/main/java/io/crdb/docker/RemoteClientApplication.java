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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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

        // these values/parameters correspond to Cockroach Connection Environment variables
        final String host = env.getProperty(COCKROACH_HOST);
        final Integer port = env.getProperty(COCKROACH_PORT, Integer.class);
        final String user = env.getProperty(COCKROACH_USER);
        final Boolean insecure = env.getProperty(COCKROACH_INSECURE, Boolean.class);
        final String certsDir = env.getProperty(COCKROACH_CERTS_DIR);

        // these values/parameters do not have corresponding conneciton
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

        if (initCluster) {
            ProcessBuilder builder = new ProcessBuilder("/cockroach",  "init",  "--disable-cluster-name-verification");
            //Map<String, String> environment = builder.environment();
            //environment.forEach((key, value) -> log.debug(key + value));
            handleProcess(builder);

//            log.debug("application will sleep briefly...");
//            TimeUnit.SECONDS.sleep(20);
        }

        if (StringUtils.hasText(databaseName)) {
            ProcessBuilder builder = new ProcessBuilder("/cockroach",  "sql",  "--execute", String.format("CREATE DATABASE IF NOT EXISTS %s", databaseName));
            handleProcess(builder);
        }

    }

    private void handleProcess(ProcessBuilder builder) {

        builder.inheritIO();

        log.debug("starting command");

        try {

            Process process = builder.start();
            int exitCode = process.waitFor();
            log.debug("command exited with value [{}]", exitCode);

        } catch (IOException | InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }
}
