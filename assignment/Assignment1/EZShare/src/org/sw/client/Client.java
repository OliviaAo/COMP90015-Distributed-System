package org.sw.client;

import java.io.DataInputStream;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils; 
import org.apache.log4j.Logger; 
import org.apache.wink.json4j.JSONArray; 
import org.apache.wink.json4j.JSONObject; 
import org.sw.comm.Message;
import org.sw.comm.Resource;
import org.sw.comm.ServerBean;
import org.apache.wink.json4j.OrderedJSONObject;
import org.apache.wink.json4j.compat.JSONException;

/**
 * This is the main class of client. A client is used to instruct the server to share the files.
 * For example, clients can request a shared file to be downloaded to them. Communications are via 
 * TCP. All messages are in JSON format, except file contents, one JSON message per line. File 
 * contents are transmitted as byte sequences, mixed between JSON messages. Interactions are 
 * synchronous request-reply, with a single request per connection.
 * @author Sheng Wu
 *
 */
public class Client {
	private static Logger logger = Logger.getLogger(Client.class);
	private ServerBean targetServer;

	public static void main(String[] args) { 
		Options options = new Options();
		options.addOption("channel", true, "channel");
		options.addOption("debug", false, "print debug information");
		options.addOption("description",true,"resource description");
		options.addOption("exchange",false, "exchange server list with server");
		options.addOption("fetch",false, "fetch resources from server");
		options.addOption("host",true,"server host, a domain name or IP address");
		options.addOption("name",true,"resource name");
		options.addOption("owner",true,"owner");
		options.addOption("port",true,"server port, an integer");
		options.addOption("publish",false, "publish resource on server");
		options.addOption("query",false, "query for resources from server");
		options.addOption("remove",false, "remove resource from server");
		options.addOption("secret",true,"secret");
		options.addOption("servers",true,"server list, host1:port1,host2:port2,...");
		options.addOption("share",false, "share resource on server");
		options.addOption("tags",true,"resource tags, tag1,tag2,tag3,...");
		options.addOption("uri",true,"resource URI");

		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = null;

		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			e.printStackTrace();
			return;
		}

		Client client = new Client();
		client.processCommand(cmd);
	}

	public void processCommand(CommandLine cmd) {
		if (cmd.hasOption("debug")) {
			logger.info("setting debug on");
		}
		if (!cmd.hasOption("host") || !cmd.hasOption("port")) {
			logger.error("require host and port");
			return;
		}
		targetServer = new ServerBean(cmd.getOptionValue("host"), Integer.valueOf(cmd.getOptionValue("port")));
		if (cmd.hasOption("publish")) {
			publish(cmd); 
		} else if (cmd.hasOption("remove")) {
			remove(cmd);
		} else if (cmd.hasOption("share")) {
			share(cmd);
		} else if (cmd.hasOption("query")) {
			query(cmd);
		} else if (cmd.hasOption("fetch")) {
			fetch(cmd);
		} else if (cmd.hasOption("exchange")) {
			exchange(cmd);
		}
	}

	private Resource parseResourceCmd(CommandLine cmd, boolean requireURI) {
		Resource resource = new Resource();
		if(!requireURI) {
			URI uri = null;
			resource.setUri(uri);
		} else if (requireURI && (!cmd.hasOption("uri") || cmd.getOptionValue("uri").equals(""))) {
			logger.error("require uri");
			return null;
		} else { 
			URI uri = null;
			try {
				uri = new URI(cmd.getOptionValue("uri"));
			} catch (URISyntaxException e) { 
				e.printStackTrace();
			}
			resource.setUri(uri);
		}
		if (cmd.hasOption("owner")) {
			resource.setOwner(cmd.getOptionValue("owner").trim());
		} else {
			resource.setOwner("");
		}
		if (cmd.hasOption("name")) {
			resource.setName(cmd.getOptionValue("name").trim());
		} else {
			resource.setName("");
		}
		if (cmd.hasOption("channel")) {
			resource.setChannel(cmd.getOptionValue("channel").trim());
		} else {
			resource.setChannel("");
		}
		if (cmd.hasOption("description")) {
			resource.setDescription(cmd.getOptionValue("description").trim());
		} else {
			resource.setDescription("");
		}
		List<String> tagList = new ArrayList<>();
		if (cmd.hasOption("tags")) {
			String[] tags = cmd.getOptionValue("tags").split(",");
			for (int i = 0; i < tags.length; i++) {
				tagList.add(tags[i].trim());
			}
		}
		resource.setTags(tagList);
		return resource;
	}

	private void publish(CommandLine cmd) {
		Resource resource = parseResourceCmd(cmd, true);
		if (resource == null) return;
		OrderedJSONObject jsonObject = new OrderedJSONObject();
		try {
			jsonObject.put("command", "PUBLISH");
			jsonObject.put("resource", Resource.toJson(resource));
		} catch (org.apache.wink.json4j.JSONException e) { 
			e.printStackTrace();
		} 
		String publishCommand = "publishing to " + cmd.getOptionValue("host") + ":" + cmd.getOptionValue("port");
		logger.info(publishCommand);
		String sentmsg = "SENT: " + jsonObject.toString();
		logger.info(sentmsg);
		List<Message> messages = ClientConnection.establishConnection(targetServer, new Message(jsonObject.toString()));
		if (messages != null) {
			for (Message message : messages) {
				String revmsg = "RECEIVED: " + message.getMessage();
				logger.info(revmsg);
			} 
		}		
	}

	private void remove(CommandLine cmd) {
		Resource resource = parseResourceCmd(cmd, true);
		if (resource == null) return;
		OrderedJSONObject jsonObject = new OrderedJSONObject();
		try {
			jsonObject.put("command", "REMOVE");
			jsonObject.put("resource", Resource.toJson(resource));
		} catch (org.apache.wink.json4j.JSONException e) { 
			e.printStackTrace();
		}  
		String msg = "Removing " + jsonObject.toString();
		logger.info(msg);
		List<Message> messages = ClientConnection.establishConnection(targetServer, new Message(jsonObject.toString()));
		if (messages != null) {
			for (Message message : messages) {
				String revmsg = "RECEIVED: " + message.getMessage();
				logger.info(revmsg);
			} 
		}		
	}

	private void share(CommandLine cmd) {
		if (!cmd.hasOption("secret")) {
			logger.error("require secret");
			return;
		}
		Resource resource = parseResourceCmd(cmd, true);
		if (resource == null) return;
		OrderedJSONObject jsonObject = new OrderedJSONObject();
		try {
			jsonObject.put("command", "SHARE");
			jsonObject.put("secret", cmd.getOptionValue("secret"));
			jsonObject.put("resource", Resource.toJson(resource));
		} catch (org.apache.wink.json4j.JSONException e) { 
			e.printStackTrace();
		}
		String shareCommand = "sharing to " + cmd.getOptionValue("host") + ":" + cmd.getOptionValue("port");
		logger.info(shareCommand);
		String sentmsg = "SENT: " + jsonObject.toString();
		logger.info(sentmsg);
		List<Message> messages = ClientConnection.establishConnection(targetServer, new Message(jsonObject.toString()));
		if (messages != null) {
			for (Message message : messages) {
				String revmsg = "RECEIVED: " + message.getMessage();
				logger.info(revmsg);
			} 
		}		
	}

	private void fetch(CommandLine cmd) {
		Resource resource = parseResourceCmd(cmd, true);
		if (resource == null) return;
		OrderedJSONObject jsonObject = new OrderedJSONObject();
		try {
			jsonObject.put("command", "FETCH"); 
			jsonObject.put("resourceTemplate", Resource.toJson(resource));  
		} catch (org.apache.wink.json4j.JSONException e1) { 
			e1.printStackTrace();
		} 
		String fetchCommand = "downloading ";
		logger.info(fetchCommand);
		String sentmsg = "SENT: " + jsonObject.toString();
		logger.info(sentmsg);
		Socket socket = null;
		try {
			socket = new Socket(targetServer.getHostname(), targetServer.getPort());
			DataInputStream inputStream = new DataInputStream(socket.getInputStream());
			DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
			outputStream.writeUTF(jsonObject.toString());
			outputStream.flush();
			do {
				String response = inputStream.readUTF();
				logger.info(response);
				if (response.contains("error")) return;
				JSONObject responseInfo;
				Long size = (long)0;
				try {
					responseInfo = new JSONObject(response);
					size = responseInfo.getLong("resourceSize");
				} catch (org.apache.wink.json4j.JSONException e) { 
					e.printStackTrace();
				}
				String fileName = resource.getUri().getPath().split("/")[resource.getUri().getPath().split("/").length - 1];
				File file = new File(fileName);
				file.createNewFile();
				FileUtils.writeByteArrayToFile(file, IOUtils.toByteArray(inputStream, size));				
			}while(inputStream.available() > 0) ;
			inputStream.close();
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void query(CommandLine cmd) {
		Resource resource = parseResourceCmd(cmd, false); 
		if (resource == null) return;
		OrderedJSONObject jsonObject = new OrderedJSONObject();
		try {
			jsonObject.put("command", "QUERY");
			jsonObject.put("relay", true);
			jsonObject.put("resourceTemplate", Resource.toJson(resource));
		} catch (org.apache.wink.json4j.JSONException e) { 
			e.printStackTrace();
		}
		String queryCommand = "querying ";
		logger.info(queryCommand);
		String sentmsg = "SENT: " + jsonObject.toString();
		logger.info(sentmsg);
		List<Message> messages = ClientConnection.establishConnection(targetServer, new Message(jsonObject.toString()));
		if (messages != null) {
			for (Message message : messages) {
				String revmsg = "RECEIVED: " + message.getMessage();
				logger.info(revmsg);
			}
		}		
	}

	private void exchange(CommandLine cmd) {
		if (!cmd.hasOption("servers")) {
			logger.error("require servers");
			return;
		}
		String[] serverList = cmd.getOptionValue("servers").split(",");
		OrderedJSONObject jsonObject = new OrderedJSONObject();
		try {
			jsonObject.put("command", "EXCHANGE");
		} catch (org.apache.wink.json4j.JSONException e1) { 
			e1.printStackTrace();
		}
		JSONArray serverArray = new JSONArray();
		for (int i = 0; i < serverList.length; i++) {
			OrderedJSONObject serverObject = new OrderedJSONObject();
			String hostname = serverList[i].split(":")[0].trim();
			int port = Integer.valueOf(serverList[i].split(":")[1].trim());
			try {
				serverObject.put("hostname", hostname);
				serverObject.put("port", port);
				serverArray.put(serverObject);
			} catch (org.apache.wink.json4j.JSONException e) { 
				e.printStackTrace();
			}		
		} 
		try {
			jsonObject.put("serverList", serverArray);
		} catch (org.apache.wink.json4j.JSONException e) { 
			e.printStackTrace();
		}
		String sentmsg = "SENT: " + jsonObject.toString();
		logger.info(sentmsg);
		List<Message> messages = ClientConnection.establishConnection(targetServer, new Message(jsonObject.toString()));
		if (messages != null) {
			for (Message message : messages) {
				String revmsg = "RECEIVED: " + message.getMessage();
				logger.info(revmsg);
			} 
		}
	}
}	
