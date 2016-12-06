package networking.groupmanager;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ProxyThread extends Thread{
	private Socket client;
	private Socket server;
	
	public ProxyThread(Socket client, Socket server) {
		this.client = client;
		this.server = server;
	}
	
	@Override
	public void run() {
		Object command;
		ObjectInputStream from_client = null, from_server = null;
		ObjectOutputStream to_client = null, to_server = null;
		
		// Connect
		try {
			from_client = new ObjectInputStream(client.getInputStream());
			from_server = new ObjectInputStream(server.getInputStream());
			to_client = new ObjectOutputStream(server.getOutputStream());
			to_server = new ObjectOutputStream(server.getOutputStream());
			while(true) {
				// Get request from client
				command = from_client.readObject();
					
				// Send request to server
				to_server.writeObject(command);
				to_server.flush();
					
				// Get response from server
				command = from_server.readObject();
					
				// Send response to client
				to_client.writeObject(command);
				to_client.flush();
			}
		} catch(IOException e) {
			// TODO: handle exception, for now, shutdown this connection
		} catch(ClassNotFoundException e) {
			// TODO: handle exception, for now, shutdown this connection
		}
		
		// Attempt to correctly shut down the sockets
		try {
			client.close();
			server.close();
		} catch (IOException e) {
			// TODO: handle exception, for now, fall through and return
		}
	}
}
