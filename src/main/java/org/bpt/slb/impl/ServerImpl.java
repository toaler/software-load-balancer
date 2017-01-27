package org.bpt.slb.impl;

import org.bpt.slb.api.Server;

public class ServerImpl implements Server {
	private String name;
	private String address;
	private int port;
	private String id;
	
	public ServerImpl(String id, String name, String address, int port) {
		this.name = name;
		this.address = address;
		this.port = port;
		this.id = id;
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
	public int getPort() {
		return port;
	}

	@Override
	public String getId() {
		return id;
	}
}
