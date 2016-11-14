package networking;

import java.util.Set;
import java.util.Hashtable;
import java.util.Enumeration;
import java.net.*;


/**
have all the information about a server this will be used by front end to manage
 *
 */

public class ServerInfo {

	private static ServerInfo instance;
	private static boolean isPrimary = false;
	private String primaryServer = new String();
	private static int runningPort = 0;
	private static String runningServer = new String();
	private static Hashtable servers = new Hashtable();
	
 
//set the ip of the running server

 private ServerInfo() {    
	  try {
	    	System.out.println(">>>FE158 initi reunning server ");
		    runningServer = InetAddress.getLocalHost().getHostAddress();
			
	    }
	    catch (Exception e) {
	    	System.err.println( e.getMessage());
	    }
	  }

		/**
		Get the singleton instance
		*/
	  public static synchronized ServerInfo getInstance() {
		//  System.out.println(">>> getting instance");
	    if (instance == null) {
	      instance = new ServerInfo();
	    }
	    return instance;
	  }

	//return all servers 	
		public String getAllServers() {
		
			StringBuffer serv = new StringBuffer();		
			Enumeration serversEnum = servers.keys();
			
			Set <String> keys= servers.keySet();
			for(String key: keys)
			{
				serv.append( key + ":" + servers.get(key).toString() + "/");
			}
			
			return serv.toString();
			
		}

	//get primary's ip addres	
	public String getPrimaryServer() {
		return primaryServer;
	}


	//	make or delete primary
	public static void addPrimary() {
		isPrimary = true;
	}

	public void removePrimary() {
		isPrimary = false;
	}
	
	//check to see if this server is primary
	public boolean isPrimary() {
		return isPrimary;
	}
	
	//add a server to the hashtable
	public static void addServer(String ip, int port) {
		servers.put(ip, new Integer(port));
		setPort(port);
		setIp(ip);
	}
	
	//Remove a server from the hashtable	
	public void removeServer(String ip) {
		servers.remove(ip);	
	}
	
	
	//setIp address of running server that was added
	public static void setIp(String ip) {
		runningServer = ip;
	}

	//return the ip address of the running server
	public String getServer() {
		return runningServer;
	}

	//set the port of the added server
	public static void setPort(int port) {
		runningPort = port;
	}

	//return the port address of the running server
	public static int getPort() {
		return runningPort;
	}


}