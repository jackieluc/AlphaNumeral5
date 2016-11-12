package networking;

import java.net.*;
import java.io.*;
import java.util.*;
import java.text.NumberFormat;
import java.text.DecimalFormat;






public class FrontEnd {

	ServerInfo status = ServerInfo.getInstance();
  
  static Hashtable serverCount = new Hashtable();
  static int primaryCount = 0;    
	private Socket _socket = null;

 	public FrontEnd() {
 		System.out.println ("syncing...");
 	}

	public void sync() {
		try {
			double randNo = (new Random((Calendar.getInstance()).getTime().getTime())).nextDouble();
	 		double s = (new Double((Double.toString(randNo)).substring(0,5))).doubleValue();
			
						
		(new DeclareSelfThread()).start();
    	//wait(s);
    //	(new CheckServersThread()).start();
  	//	wait(s);
	//		if (status.isPrimary())  {
	//			(new DeclarePrimaryThread()).start();
	//    	wait(s);
    //	}
    //	(new CheckPrimaryThread()).start();
  	//	wait(s);  
    //	(new PingThread()).start();
  	//	wait(s);  					

		}
		catch (Exception e) {
			System.err.println("### Synchronizer error : Could not create a new sync thread.");
		}
	}		
	
	
	
	/**
	 Sleep for a number of seconds
	*/
	public static void wait(double seconds) {
	 	try {
	
	 	  seconds =  seconds * 1000;
	 	  long s = (new Double(seconds)).longValue();
	 		Thread.sleep(s);
	 	}
	 	catch (InterruptedException e) {
	 		System.out.println("### Sleep interrupted.");
	 	}
	}
		
} // end of Sychchronizer class

/**
 Finds out if any of the servers in the current cluster status is 
 down. if so, remove it from this current cluster status
*/

class PingThread extends Thread {

	ServerInfo status = ServerInfo.getInstance();
	
	Socket _socket = null;

	public void run() {		
		Hashtable servers = status.getServers();
		if (servers != null) {	
		 	Writer out = null;
		 	Enumeration hosts = servers.keys();

		 	while (hosts.hasMoreElements()) {
		 		String host = (String)hosts.nextElement();
				try {
					_socket = new Socket(host, ((Integer)servers.get(host)).intValue());
					FrontEnd.serverCount.put(host, new Integer(0));
				}
				catch (IOException e) {
					int count = 0;
					try {
						count = ((Integer)FrontEnd.serverCount.get(host)).intValue();
					}
					catch (NullPointerException ne) {}
					
					count++;
				/**	int maxRetries = config.getMaxServerRetry();
					if (count > config.getMaxServerRetry()) {
						status.removeServer(host);
						FrontEnd.serverCount.remove(host);
					}
					else {
						FrontEnd.serverCount.put(host, new Integer(count));
					}**/
				}
			}
		}
	}
} // end PingThread

/**
 Send out multicast datagrams containing the server information to broadcast
 its own presence.
*/

class DeclareSelfThread extends Thread {

	ServerInfo status = ServerInfo.getInstance();
	//Config config = Config.getInstance();

	public void run() {
	//	int portOut = config.getDiscoveryPort();
		byte ttl = (byte) 1; // 1 byte ttl for subnet only
		InetAddress iaOut = null;
		try {
		//	iaOut = InetAddress.getByName(config.getDiscoveryServer());
		}
		catch (Exception e) {
			System.err.println("### Error : error contacting multicast port. Shutting down server.");
			e.printStackTrace();
			System.exit(-1);
		}
		
		String clusterStatus = status.getServersAsString();
		System.out.println("status >> " + clusterStatus);
			
		byte [] dataOut = (status.getRunningServer() + ":" + status.getRunningPort()).getBytes();		
	//	DatagramPacket dpOut = new DatagramPacket(dataOut, dataOut.length, iaOut, portOut);
	
	/**	try {
			MulticastSocket msOut = new MulticastSocket(portOut);
			
			msOut.joinGroup(iaOut);
			msOut.send(dpOut, ttl);
			
			msOut.leaveGroup(iaOut);
			msOut.close();
			}
		catch (SocketException e) {
			System.err.println(e);
			e.printStackTrace();
		}
		catch (IOException e) {
			System.err.println(e);
			e.printStackTrace();
		}**/
	}
} // end of DeclareSelfThread


/**
 Check the multicast socket for any datagrams. If there are, take 
 and extract the server  info from it. Each server will multicast
 its IP address and port this way.
*/
	
class CheckServersThread extends Thread {

	ServerInfo status = ServerInfo.getInstance();
	//Config config = Config.getInstance();

	public void run() {
	
		MulticastSocket socket = null;
		try {
		//  socket = new MulticastSocket(config.getDiscoveryPort());
	//	  InetAddress address = InetAddress.getByName(config.getDiscoveryServer());
		  DatagramPacket packet;
			byte[] buf = new byte[256];
		  packet = new DatagramPacket(buf, buf.length);
	//	  socket.setSoTimeout(config.getTimeout());
	//		socket.joinGroup(address);
			socket.receive(packet);
		  String data = new String(packet.getData());
	
			StringTokenizer st = new StringTokenizer(data, ":");
			String host = st.nextToken();
			int port = Integer.parseInt((st.nextToken()).trim());

			status.addServer(host, port);

		//	socket.leaveGroup(address);
			socket.close();		
		}
		catch (InterruptedIOException e) {
			System.out.println("### timed out.");		
			socket.close();	
		}
		catch (IOException e) {
			System.out.println("### Error creating multicast socket.");		
			socket.close();	
		}
	}
}

/**
 Send out multicast datagrams to broadcast that it is the primary server
*/

class DeclarePrimaryThread extends Thread {

	ServerInfo status = ServerInfo.getInstance();
	//Config config = Config.getInstance();

	public DeclarePrimaryThread() {
	}
	
	public void run() {
		
	//	int portOut = config.getPrimaryPort();
		byte ttl = (byte) 1; // 1 byte ttl for subnet only
		InetAddress iaOut = null;
	/**	try {
			iaOut = InetAddress.getByName(config.getPrimaryServer());
		}
		catch (Exception e) {
			System.err.println("### Synchronizer error : contact multicast port. Shutting down server.");
			e.printStackTrace();
			System.exit(-1);
		}**/

		String primary = status.getRunningServer().trim();
		byte [] dataOut = primary.getBytes();	

	//	DatagramPacket dpOut = new DatagramPacket(dataOut, dataOut.length, iaOut, portOut);

		try {
			MulticastSocket msOut = new MulticastSocket();
			msOut.joinGroup(iaOut);
		//	msOut.send(dpOut, ttl);
			msOut.leaveGroup(iaOut);
			msOut.close();
		}
		catch (SocketException e) {
			System.err.println(e);
			e.printStackTrace();
		}
		catch (IOException e) {
			System.err.println(e);
			e.printStackTrace();
		}
	}
	
} // end of DeclarePrimaryThread


/**
 Check the multicast socket for any datagrams. If there are, take 
 and check for the server that sends. Only the primary sends out datagrams
 to this socket. If there are no datagrams, this means that the primary server
 is down.
*/
	
class CheckPrimaryThread extends Thread {

	ServerInfo status = ServerInfo.getInstance();
//	Config config = Config.getInstance();

	public CheckPrimaryThread() {
	}
	
	public void run() {
		InetAddress group = null;
	//	int portIn = config.getPrimaryPort();
					
	/**	try {
			group = InetAddress.getByName(config.getPrimaryServer());
		}
		catch (Exception e) {
			System.err.println("### Synchronizer error: cannot create multicast socket. Shutting down server.");
			e.printStackTrace();
			System.exit(-1);	
		}**/
		
		MulticastSocket msIn = null;						
		try {	
			
		//	msIn = new MulticastSocket(portIn);
		//	msIn.setSoTimeout(config.getTimeout());
			msIn.joinGroup(group);
			byte [] bufferIn = new byte[512];
			DatagramPacket dpIn = new DatagramPacket(bufferIn, bufferIn.length);
			
			try {
				msIn.receive(dpIn);
				
				String primary = (new String(dpIn.getData())).trim();
				if (status.isPrimary() && !primary.equals(status.getRunningServer())) {
					System.out.println("### More than 1 primary found. Demoting this server.");
					status.demote();
				}
				FrontEnd.primaryCount = 0;
			}
			catch (InterruptedIOException e) {
				FrontEnd.primaryCount++;
				System.out.println("### Timing out the primary server : " + FrontEnd.primaryCount);				
			//	if (FrontEnd.primaryCount > config.getMaxPrimaryRetry()) {
			//		status.promote();
			//	}
			}		
		}
		catch (IOException e) {
			System.err.println ("## Synchronizer error : Cannot get current cluster status. Shutting down server.");
			e.printStackTrace();
			System.exit(-1);
		}	
	}	
	

} // end of CheckPrimaryThread



