package org.sw.server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

public class ServerInfo {
	public static int connectionInterval = 600;
	public static int exchangeInterval = 600;
	public static String secret = UUID.randomUUID().toString();
	public static String hostName = "";
	public static int port = 9888;
	public static boolean debug = false;
	static {
		try {
			hostName = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
}
