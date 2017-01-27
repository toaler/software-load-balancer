package org.bpt.slb.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.bpt.slb.api.SoftwareLoadBalancerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.x.discovery.ServiceCache;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.details.ServiceCacheListener;
import org.bpt.slb.Main;
import org.bpt.slb.api.Server;
import org.stringtemplate.v4.ST;

public class SoftwareLoadBalancerManagerImpl implements SoftwareLoadBalancerManager {
	private static final Logger logger = LoggerFactory.getLogger(SoftwareLoadBalancerManagerImpl.class);

	public void start() {

		CuratorFramework curatorFramework = null;
		try {
			curatorFramework = CuratorFrameworkFactory.newClient("172.17.0.2:2181,172.17.0.3:2181,172.17.0.4:2181",
					new RetryNTimes(5, 1000));
			curatorFramework.start();

			try {
				ServiceDiscovery<String> sd = ServiceDiscoveryBuilder.<String>builder(String.class)
						.basePath("/service-discovery").client(curatorFramework).build();

				sd.start();

				sd.queryForInstances("wts").stream().forEach((e) -> System.out.println(e));

				ServiceCache<String> cache = sd.serviceCacheBuilder().name("wts").build();

				cache.addListener(new ServiceCacheListener() {

					@Override
					public void stateChanged(CuratorFramework client, ConnectionState newState) {
						try {
							rewriteConfiguration(getServers(sd));
							reloadHaProxy();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					@Override
					public void cacheChanged() {
						try {
							rewriteConfiguration(getServers(sd));
							reloadHaProxy();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					private List<Server> getServers(ServiceDiscovery<String> sd) throws Exception {
						List<Server> servers = new ArrayList<>();
						sd.queryForInstances("wts").stream()
								.forEach((e) -> servers.add(new ServerImpl(e.getId(), e.getName(), e.getAddress(), e.getPort())));
						return servers;
					}

					private void rewriteConfiguration(List<Server> servers) {
						try (Scanner scanner = new Scanner(
								ClassLoader.getSystemClassLoader().getResourceAsStream("haproxy.st"), "UTF-8")) {
							String template = scanner.useDelimiter("\\A").next();

							ST haProxy = new ST(template);

							haProxy.add("servers", servers);
							try {
								Files.write(Paths.get("/tmp/haproxy.cfg"), haProxy.render().getBytes());
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							System.out.println(haProxy.render());
						}
					}

					private void reloadHaProxy() {
						logger.info("Reloading HAProxy");
						ProcessBuilder ps = new ProcessBuilder("sudo", "/home/toal/tps/haproxy/haproxy-1.7.2/hap.sh", "reload");

						// From the DOC: Initially, this property is false,
						// meaning that the
						// standard output and error output of a subprocess
						// are sent to two
						// separate streams
						ps.redirectErrorStream(true);

						Process pr;
						try {
							pr = ps.start();

							BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
							String line;
							while ((line = in.readLine()) != null) {
								logger.info(line);
							}
							pr.waitFor();
							logger.info("ok!");

							in.close();
						} catch (IOException | InterruptedException e) {
							logger.warn("Failed to reload due to " + e.getMessage(), e);
						}
					}
				});

				cache.start();

				Thread.sleep(2000000);

				sd.close();

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} finally {
			if (curatorFramework != null) {
				curatorFramework.close();
			}
		}
	}
}
