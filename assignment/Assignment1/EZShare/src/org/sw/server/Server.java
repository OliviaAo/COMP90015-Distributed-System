package org.sw.server;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.*;

public class Server {

	public static void main(String[] args) {
		Options options = new Options();
		options.addOption("advertisedhostname", true, "advertised hostname");
		options.addOption("connectionintervallimit", true, "connection interval limit in seconds");
		options.addOption("exchangeinterval", true, "exchange interval in seconds");
		options.addOption("port", true, "server port, an integer");
		options.addOption("secret", true, "secret");
		options.addOption("debug", false, "print debug information");
		
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			e.printStackTrace();
			return;
		}
		if (cmd.hasOption("advertisedhostname")) {
			ServerInfo.hostName = cmd.getOptionValue("advertisedhostname");
		}
		if (cmd.hasOption("connectionintervallimit")) {
			ServerInfo.connectionInterval = Integer.parseInt(cmd.getOptionValue("connectionintervallimit"));
		}
		if (cmd.hasOption("exchangeinterval")) {
			ServerInfo.exchangeInterval = Integer.parseInt(cmd.getOptionValue("exchangeinterval"));
		}
		if (cmd.hasOption("port")) {
			ServerInfo.port = Integer.parseInt(cmd.getOptionValue("port"));
		}
		if (cmd.hasOption("secret")) {
			ServerInfo.secret = cmd.getOptionValue("secret");
		}
		if (cmd.hasOption("debug")) {
			ServerInfo.debug = Boolean.parseBoolean(cmd.getOptionValue("debug"));
		}
		 
	}

	public static Map<Object,Object> getAMap(Object key,Object value) {
		Map<Object,Object> map=new HashMap<Object,Object>();
		map.put(key,value);
		return map;
	}

}
