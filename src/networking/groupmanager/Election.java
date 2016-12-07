package networking.groupmanager;

import networking.groupmanager.groupCommands.ElectionCommand;
import networking.groupmanager.groupCommands.LeaderCommand;

public class Election {
	public GroupManager gm;
	public String current_leader = "";
	public Thread leader;
	public Object electionLock = new Object();
	public boolean newLeaderFound = false;
	
	public Election() {
		gm = GroupManager.getInstance();
	}

	public void waitForLeader(final long time) {
		leader = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(time);
					if(current_leader.equals(gm.ip)) {
						System.out.println("I'm leader");
						gm.leaderIP = gm.ip;
						gm.multicast(new LeaderCommand(gm.ip));
						gm.makePrimary();
					} else {
						System.out.println("No leader found, restart election");
						restart();
					}
				} catch (InterruptedException e) {
					// New current leader or new leader found
				}
			}
		});
		leader.start();
	}
	
	public String elect(String ip) {
		synchronized(electionLock) {
			System.out.println("ELECT " + ip + " gm.ip:" + gm.ip + " current_leader: " + current_leader);
			newLeaderFound = false;
			if(ip.compareTo(current_leader) > 0) {
				if(leader != null) {
					leader.interrupt();
				}
				if(ip.compareTo(gm.ip) > 0) {
					current_leader = ip;
					waitForLeader(10000);
				} else {
					current_leader = gm.ip;
				}
			}
			if(current_leader.equals(gm.ip)) {
				if(leader != null) {
					leader.interrupt();
				}
				waitForLeader(5000);
			}
			return current_leader;
		}
	}

	public void restart() {
		synchronized(electionLock) {
			current_leader = "";
			elect(gm.ip);
			gm.multicast(new ElectionCommand(gm.ip));
		}
	}
	
	public void newLeader(String ip) {
		synchronized(electionLock) {
			if(ip.equals(gm.ip)) {
				return;
			}
			if(newLeaderFound) {
				return;
			}
			newLeaderFound = true; 
			if(leader != null) {
				leader.interrupt();
			}
			System.out.println("New leader: " + ip);
			gm.leaderIP = ip;
			gm.isLeaderAlive();
			gm.makeBackup();
		}
	}
}
