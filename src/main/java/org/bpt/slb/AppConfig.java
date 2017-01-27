package org.bpt.slb;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.bpt.slb.api.SoftwareLoadBalancerManager;
import org.bpt.slb.impl.SoftwareLoadBalancerManagerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;

public class AppConfig {
	private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);

	private static final String PROPERTY_FILE = "slb.properties";

	@Bean("SoftwareLoadBalancerManager")
	public SoftwareLoadBalancerManager softwareLoadBalancerManager() {
		return new SoftwareLoadBalancerManagerImpl();
	}

	@Bean("Properties")
	public Properties properties() {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream input = classLoader.getResourceAsStream(PROPERTY_FILE);

		if (input == null) {
			logger.error("Couldn't find " + PROPERTY_FILE);
		}
		Properties properties = new Properties();
		try {
			properties.load(input);
			return properties;
		} catch (IOException e) {
			logger.error("Failed to load " + PROPERTY_FILE, e);
			throw new RuntimeException(e);
		}
	}
}