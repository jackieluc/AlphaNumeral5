import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Random;

public class RequestHandler implements Runnable {

	private Server parent = null;
	private Socket socket = null;
	private Random randomNumber = new Random();
	
	public RequestHandler(Server p, Socket s) { 
		this.parent = p;
		this.socket	= s; 
		Logger.debug = true;
	}
	
	@Override
	public void run() {
		
		if (socket == null || parent == null) {
			System.err.println("Cannot connect to server socket or accept client socket.");
			return;
		}
		
		try {
			Logger.log("Connected to: " + socket.getInetAddress().toString());
			DataInputStream incomingData = new DataInputStream(socket.getInputStream());
			DataOutputStream outgoingData = new DataOutputStream(socket.getOutputStream());
			
			String incoming	= "";
			String outgoing = "";
			
			// we first accept the initial request of the client's name
			incoming = incomingData.readUTF();
			if(incoming.startsWith("hello ")) {
				outgoingData.writeUTF("Hello " + incoming.substring(6));
			}
			
			String clientName = incoming.substring(6);
			
			while(!incoming.equals("quit")) {
				incoming = incomingData.readUTF();
				
				Logger.log("Data from " + clientName + " : " + incoming);
				
				if(!incoming.equals("quit"))
					outgoingData.writeUTF("To " + clientName + ", here's a random number: " + randomNumber.nextInt(1000));
			}
			
			System.out.println("Client: " + clientName + " decided to quit.");
			socket.close();
			
		} catch (IOException IOError) {
			System.err.println("ERROR: Cannot open input/output streams on this socket: ");
			System.err.println(IOError.getMessage());
			return;
		}
	} // run
} // RequestHandler
