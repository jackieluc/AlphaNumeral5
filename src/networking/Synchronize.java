package networking;
/** this does nothing so far **/
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

public class Synchronize {

  ServerInfo state = ServerInfo.getInstance();
 	public Synchronize() {
 		System.out.println ("syncing...");
 	}

	public void sync() {
		try {
			(new DeclareSelfThread()).start();
			(new CheckServersThread()).start();
			}
		catch (Exception e) {
			System.err.println("error in sync method");
		}
	}		
		
} 

//	multicast presence to other server
class DeclareSelfThread extends Thread {

	ServerInfo state = ServerInfo.getInstance();

	public void run() {	
		
	/**
		System.out.println(">>inside declaresselfthread");
		try {
			Socket socket = new Socket("127.0.0.1", 5555);
			 OutputStream outToServer = socket.getOutputStream();
	         DataOutputStream out = new DataOutputStream(outToServer);     
	         InputStream inFromServer = socket.getInputStream();
	         DataInputStream in = new DataInputStream(inFromServer);
	         
	         out.writeInt(state.getPort());
	         out.writeUTF(state.getServer());
	         out.writeBoolean(state.isPrimary());
	         
	      
	         
	         socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	**/
			
		
	
	}
}
//  DeclareSelfThread



/**
Check the multicast socket for any datagrams. If there are, take 
and extract the server  info from it. Each server will multicast
its IP address and port this way.
*/
	
class CheckServersThread extends Thread {

	ServerInfo status = ServerInfo.getInstance();
	//Config config = Config.getInstance();

	public void run() {
	
		
	}
}




//check to see if primary exists
class CheckPrimaryThread extends Thread {

	ServerInfo state = ServerInfo.getInstance();
	
	public void run() {	
		String allServer = state.getAllServers();
		System.out.println("state >> " + allServer);
	}

} // CheckPrimaryThread


//declare yourself as primary if it doesnt exits
class DeclarePrimary extends Thread {

	ServerInfo state = ServerInfo.getInstance();
	
	public void run() {		
		String allServer = state.getAllServers();
		System.out.println("state >> " + allServer);
	}

} // DeclarePrimary


//ping to server it see if alive
class pingServer extends Thread {

	ServerInfo state = ServerInfo.getInstance();
	
	public void run() {		
		String allServer = state.getAllServers();
		System.out.println("state >> " + allServer);
	}

} // pingServer







