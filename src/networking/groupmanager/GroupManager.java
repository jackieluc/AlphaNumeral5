package networking.groupmanager;

import networking.groupmanager.commands.ElectionCommand;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class GroupManager {
	private static GroupManager instance = new GroupManager();
	public final static int port = 5000;
	public ConcurrentHashMap<String, GroupCommunicator> comms = new ConcurrentHashMap<String, GroupCommunicator>();
	public Object hashLock = new Object();
	public Object electionLock = new Object();
	public ServerSocket socket;
	public String ip;
	public Election election;
	public String leader_ip;
	public String proxy_ip;
	public Thread isLeaderAlive;
	//public Server server;
	
	public static GroupManager getInstance() {
		return instance;
	}
	
	private GroupManager() {
		try {
			ip = InetAddress.getLocalHost().getHostAddress();
			socket = new ServerSocket(port);
			System.out.println("Group manager ip: " + ip);
		} catch (IOException e) {
			System.out.println("Failed to create server socket");
			System.exit(0);
		}
	}
	
	public void isLeaderAlive() {
		isLeaderAlive = new Thread(new Runnable() {
			private long timeout = 2000;
			
			@Override
			public void run() {
				try {
					// Wait for connections to be made first
					Thread.sleep(timeout*5);
					if(leader_ip == null) {
						System.out.println("New election started");
						election.current_leader = "";
						election.elect(ip);
						multicast(new ElectionCommand(ip));
						return;
					}
					GroupCommunicator leader;
					synchronized(hashLock) {
						leader = comms.get(leader_ip);
					}
					while(true) {
						Thread.sleep(timeout);
						if(!leader.isAlive()) {
							System.out.println("New election started");
							election.current_leader = "";
							election.elect(ip);
							multicast(new ElectionCommand(ip));
							return;
						}
					}
				} catch (InterruptedException e) {
					// New election started from an election command
				}
			}
		});
		isLeaderAlive.start();
	}
	
	public void initialize(String ip) {
		GroupCommunicator gc = new GroupCommunicator(ip);
		comms.put(ip, gc);
		new Thread(gc).start();
	}
	
	public void add(String ip) {
		synchronized(hashLock) {
			if(ip.equals(this.ip)) {
				return;
			}
			GroupCommunicator gc;
			if(!comms.containsKey(ip)) {
				gc = new GroupCommunicator(ip);
				comms.put(ip, gc);
			} else {
				gc = comms.get(ip);
			}
			if(gc.connected) { // Might have to be isRunning instead of connected
				return;
			}
			new Thread(gc).start();
		}
	}
	
	public void run() {
		Socket temp;
		String ip;
		while(true) {
			try {
				System.out.println("Waiting for connections...");
				temp = socket.accept();
				ip = temp.getInetAddress().getHostAddress();
				
				synchronized(hashLock) {
					GroupCommunicator gc;
					
					// If a new group communicator connected, add it
					if((gc = comms.get(ip)) == null) {
						gc = new GroupCommunicator(ip);
						comms.put(ip, gc);
					}
					gc.socket = temp;
					new Thread(gc).start();
				}
			} catch (IOException e) {
				// TODO: recreate server socket?
			}
		}
	}
	
	public void multicast(Object command) {
		synchronized(hashLock) {
			for(GroupCommunicator gc : comms.values()) {
				gc.write(command);
			}
		}
	}
	
	public void connectToProxy() {
		// server.connectToProxy();
	}
}
