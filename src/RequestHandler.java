import debug.Logger;
import networking.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class RequestHandler implements Runnable {

	private Server parent = null;
	private Socket socket = null;
	
	public RequestHandler(Server p, Socket s) { 
		this.parent = p;
		this.socket	= s; 
		Logger.debug = true;
	}
	
	@Override
	public void run() {
		
		if (socket == null || parent == null)
			return;
		
		try {
			Logger.log("Connected to: " + socket.getInetAddress().toString());
			DataInputStream dis = new DataInputStream(socket.getInputStream());
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			
			String incoming	= "";
			
			while(!incoming.equals("quit")) {
				incoming = dis.readUTF();
				
				Logger.log("Data at server: " + incoming);
				
				if (incoming.startsWith("signup ")) {
					
				} // if "signup"
				
				if (incoming.startsWith("login ")) {
					
				} // if "login"
				
				if (incoming.startsWith("move ")) {
					String[] data = incoming.split(" ");
					
					for(int i = 0; i < data.length; i++)
						Logger.log("data: " + i);
					
				} // if "move"
			}
		} catch (IOException e) {
			System.err.println("ERROR: Cannot open input/output streams on this socket: " + e.getMessage());
			return;
		}
	} // run
} // RequestHandler
