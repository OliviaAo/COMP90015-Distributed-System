package org.sw.comm; 

import java.io.File; 

public class Message {
	private MessageType type;
	private String message;
	private byte[] bytes;
	private File file;
	
	public Message(MessageType type, String message, byte[] bytes, File file){
		this.type = type;
		this.message = message;
		this.bytes = bytes;
		this.file = file;
	}
	
	public Message(String message){
		this.type = MessageType.STRING;
		this.message = message;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}
 

	public MessageType getType() {
		return type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

}
