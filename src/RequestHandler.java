import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class RequestHandler implements Runnable {

	private Server parent = null;
	private Socket socket = null;
	
	public RequestHandler(Server p, Socket s) { 
		this.parent = p;
		this.socket	= s; 
	}
	
	@Override
	public void run() {
		
		if (socket == null || parent == null)
			return;
		
		try {
			DataInputStream ois = new DataInputStream( socket.getInputStream() );
			DataOutputStream oos = new DataOutputStream( socket.getOutputStream() );
			
			String incoming	= ois.readUTF();
			
			if (incoming.startsWith("Hello ")) {
				
			} // if 
			
		} catch (IOException e) {
			System.err.println("ERROR: Cannot open input/output streams on this socket" + e.getMessage());
			return;
		}
	} // run
} // RequestHandler
