package io.crdb.docker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
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

		log.debug("{} is [{}], CockroachDB will supply a default value if none is provided", COCKROACH_HOST, host);
		log.debug("{} is [{}], CockroachDB will supply a default value if none is provided", COCKROACH_PORT, port);
		log.debug("{} is [{}], CockroachDB will supply a default value if none is provided", COCKROACH_USER, user);
		log.debug("{} is [{}], CockroachDB will supply a default value if none is provided", COCKROACH_INSECURE, insecure);
		log.debug("{} is [{}], CockroachDB will supply a default value if none is provided", COCKROACH_CERTS_DIR, certsDir);

		log.debug("{} is [{}]", DATABASE_NAME, databaseName);
		log.debug("{} is [{}]", DATABASE_USER, databaseUser);

		if (databasePassword != null) {
			log.debug("{} is [{}]", DATABASE_PASSWORD, "********");
		} else {
			log.debug("{} was not provided", DATABASE_PASSWORD);
		}

		log.debug("{} is [{}]", COCKROACH_ORG, licenseOrg);
		log.debug("{} is [{}]", COCKROACH_LICENSE_KEY, licenseKey);
		log.debug("{} is [{}]", COCKROACH_INIT, initCluster);

		if (initCluster) {
			Runtime rt = Runtime.getRuntime();
			Process ps = rt.exec("path to my executable.exe");
		}

	}
}
