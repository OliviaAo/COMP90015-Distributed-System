package Client;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;/Users/admin/Desktop/Distributed System/assignment/Assignment1/ClientServer/src/Client/Client.java

public class Client {
	
	// IP and port
	private static String ip = "localhost";
	private static int port = 3000;
	
	public static void main(String[] args) {
		try(Socket socket = new Socket(ip, port)){
			// Output and Input Stream
			DataInputStream input = new DataInputStream(socket.
					getInputStream());
		    DataOutputStream output = new DataOutputStream(socket.
		    		getOutputStream());
		    
	    	output.writeUTF("I want to connect!");
	    	output.flush();
	    	
		    while(true){
                        if(input.available() > 0) {
                            String message = input.readUTF();
                            System.out.println(message);
                        }
	    		
		    }
		    
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			
		}

	}

}
