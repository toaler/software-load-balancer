package org.bpt.slb;

import java.util.Properties;

import org.bpt.slb.api.SoftwareLoadBalancerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
	private static final Logger logger = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {
		logger.info(
		    "\n/ ___|| |   | __ )\n" + 
		    "\\___ \\| |   |  _ \\\n" +
		    " ___) | |___| |_) |\n" +
		    " ____/|_____|____/");

		ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
		Properties properties = (Properties) context.getBean("Properties");

		properties.entrySet().stream()
				.forEach((entry) -> System.setProperty(entry.getKey().toString(), entry.getValue().toString()));

		SoftwareLoadBalancerManager slb = (SoftwareLoadBalancerManager) context.getBean("SoftwareLoadBalancerManager");

		slb.start();
	}

}
