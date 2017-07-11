package org.sw.client; 

import org.apache.log4j.Logger;
import org.sw.comm.Message;
import org.sw.comm.MessageType;
import org.sw.comm.ServerBean;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientConnection {
	//private static Logger logger = Logger.getLogger(ClientConnection.class);
	
	public static List<Message> establishConnection(ServerBean serverBean, Message message) {
		Socket socket = null;
		Message response = null;
		List<Message> messages = new ArrayList<>();
		try {
			socket = new Socket(serverBean.getAddress(), serverBean.getPort());
			socket.setSoTimeout(1000);
			DataInputStream inputStream = new DataInputStream(socket.getInputStream());
			DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
			outputStream.writeUTF(message.getMessage()); 
			outputStream.flush();
			String data = null;
			while ((data = inputStream.readUTF()) != null) {
				response = new Message(MessageType.STRING, data, null, null);
				messages.add(response);
			}
		} catch (IOException e) {
			//logger.info("Lost connection: " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
		} finally { 
				if (socket != null) {
					try {
						socket.close();
					} catch (IOException e) { 
						e.printStackTrace();
					}
					//logger.info("Close connection: " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
				}
			}  
			return messages;
		}
}