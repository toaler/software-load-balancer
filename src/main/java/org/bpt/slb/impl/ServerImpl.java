package org.bpt.slb.impl;

import org.bpt.slb.api.Server;

public class ServerImpl implements Server {
	private String name;
	private String address;
	private String port;
	
	public ServerImpl(String name, String address, String port) {
		this.name = name;
		this.address = address;
		this.port = port;
	}

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String getAddress() {
		return address;
	}
	
	@Override
	public String getPort() {
		return port;
	}
}
