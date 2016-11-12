package networking;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;
import java.util.Iterator;
import java.util.List;
import java.util.Hashtable;
import java.util.Enumeration;
import java.net.*;


/**
 * <code>Status</code> represents the current cluster status of the servers. It is
 * a trascient object, meaning that the state of this object is not written to any persistent
 * storage. This is because the state of the server is retrieved from the current primary server.
 * 
 * last modified - 19 March 2001<br>
 * version 1.0<br>
 * author Chang Sau Sheong
 *
 */

public class ServerInfo {

	private static boolean isPrimary = false;
	private static Hashtable servers = new Hashtable();
	private String primaryServer = new String();
	private static String runningServer = new String();
	private static int runningPort = 0;

	private static ServerInfo _instance;
	
  /**
  Gets a list of servers
  @returns Hashtable Hashtable containing a list of server IP address/port pairs
  */
	public Hashtable getServers() {
		return servers;
	}

	/**
	Gets a list of servers and ports in a semi-colon delimited string
	@returns String A list of servers and ports in a semi-colon (;) delimited string
	*/
	
	public String getServersAsString() {
	/**	StringBuffer servers = new StringBuffer();		
		Enumeration serversEnum = _servers.keys();
		
		while (serversEnum.hasMoreElements()) {
			String serverName = (String)serversEnum.nextElement();
			servers.append( serverName + ":" + _servers.get(serverName).toString() + ";");
		}
		return servers.toString();**/
		StringBuffer serv = new StringBuffer();		
		Enumeration serversEnum = servers.keys();
		
		Set <String> keys= servers.keySet();
		for(String key: keys)
		{
			serv.append( key + ":" + servers.get(key).toString() + "/");
		}
		
		return serv.toString();
		
	}
	
	/**
	Set the servers list in the current cluster status
	@parameter A hashtable containing a list of server IP addresses/ports pair
	*/
	
	public void setServers(Hashtable servers) {
		servers = servers;
	}
	
	/**
	Gets the primary NotifyServer IP address
	*/
	public String getPrimaryServer() {
		return primaryServer;
	}

	/**
	Makes this the Primary Notify Server
	*/
	public static void promote() {
		isPrimary = true;
	}

	public void demote() {
		isPrimary = false;
	}
	

  /**
  Gets the port corresponding to the IP address. Returns a null if there is no such
  server.
  */	
	public int getPort(String ip) {
		return ((Integer)servers.get(ip)).intValue();	
	}
	
	/**
	Check if this server is the Primary Notify Server (PNS). Returns true if it is, false if it
	is not.
	*/
	public boolean isPrimary() {
		return isPrimary;
	}
	
	/**
	Adds a Notify Server to the list
	*/
	public static void addServer(String ip, int port) {
		servers.put(ip, new Integer(port));
		setRunningPort(port);
		setRunningServer(ip);
	}
	
	/**
	Remove a server from the list
	*/
	public void removeServer(String ip) {
		servers.remove(ip);	
	}
	
	/**
	Sets the currently running server IP. This could be different from the localhost IP
	if there are more than 1 IP addresses in the same machine
	*/
	public static void setRunningServer(String ip) {
		runningServer = ip;
	}

	/**
	Gets the currently running server IP
	*/
	public String getRunningServer() {
		return runningServer;
	}

	/**
	Sets the currently running server port. This could be different from the localhost port
	if there are more than 1 IP addresses in the same machine
	*/
	public static void setRunningPort(int port) {
		runningPort = port;
	}

	/**
	Gets the currently running server port
	*/
	public int getRunningPort() {
		return runningPort;
	}

	
  /**
  Creates the status class to store the Xander server status.
  */
  private ServerInfo() {    
    try {
    	System.out.println(">>>FE158 initi reunning server ");
	    runningServer = InetAddress.getLocalHost().getHostAddress();
			servers.clear();
    }
    catch (Exception e) {
    	System.err.println("Cannot create a status object." + e.getMessage());
    }
  }

	/**
	Get the singleton instance
	*/
  public static synchronized ServerInfo getInstance() {
	//  System.out.println(">>> getting instance");
    if (_instance == null) {
      _instance = new ServerInfo();
    }
    return _instance;
  }
	

}