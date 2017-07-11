package org.sw.comm;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ServerBean {
	private String hostname;
	private int port;
	private InetAddress address;
	
	public ServerBean(String hostname, int port){
		this.hostname = hostname;
		this.port = port;
		try {
			this.address=InetAddress.getByName(hostname);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public InetAddress getAddress() {
		return address;
	}

	public int getPort() {
		return port;
	}
	
	public String getHostname() {
		return hostname;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	
	public void setAddress(InetAddress address) {
		this.address = address;
	}

	public String toString() {
		return this.hostname + ":" + this.port;
	}
 
	public boolean equals(Object obj) {
		if (!(obj instanceof ServerBean)) return false;
		ServerBean serverBean = (ServerBean) obj;
		return this.address.equals(serverBean.getAddress()) && this.port == serverBean.getPort();
	}
}