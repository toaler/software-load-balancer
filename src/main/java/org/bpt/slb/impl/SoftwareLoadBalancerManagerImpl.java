package org.bpt.slb.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.bpt.slb.api.SoftwareLoadBalancerManager;
import org.bpt.slb.api.Server;
import org.stringtemplate.v4.ST;

public class SoftwareLoadBalancerManagerImpl implements SoftwareLoadBalancerManager {
	public void start() {

		try (Scanner scanner = new Scanner(ClassLoader.getSystemClassLoader().getResourceAsStream("haproxy.st"),
				"UTF-8")) {
			String template = scanner.useDelimiter("\\A").next();

			ST haProxy = new ST(template);

			haProxy.add("servers", getServers());
			System.out.println(haProxy.render());
		}
	}

	private List<Server> getServers() {
		return Arrays.asList(new ServerImpl("web01", "172.17.0.5", "8080"),
				new ServerImpl("web02", "172.17.0.6", "8080"), new ServerImpl("web03", "172.17.0.7", "8080"));
	}
}
