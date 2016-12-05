
public class Election {
	public GroupManager gm;
	public String current_leader = "";
	public long time = 5000;
	public Thread leader;
	public Object electionLock = new Object();
	public boolean newLeaderFound = false;
	
	public Election() {
		gm = GroupManager.getInstance();
	}
	
	public void waitForLeader() {
		leader = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(time);
					if(current_leader.equals(gm.ip)) {
						System.out.println("I'm leader");
						gm.leader_ip = gm.ip;
						gm.multicast(new LeaderCommand(gm.ip));
						gm.connectToProxy();
					} else {
						System.out.println("No leader found, restart election");
						restart();
					}
					return;
				} catch (InterruptedException e) {
					// New current leader or new leader found
				}
			}
		});
		leader.start();
	}
	
	public String elect(String ip) {
		synchronized(electionLock) {
			newLeaderFound = false;
			if(ip.compareTo(current_leader) > 0) {
				if(leader != null) {
					leader.interrupt();
				}
				current_leader = ip;
				waitForLeader();
			}
			return current_leader;
		}
	}
	
	public void restart() {
		synchronized(electionLock) {
			current_leader = "";
			elect(gm.ip);
		}
	}
	
	public void newLeader(String ip) {
		synchronized(electionLock) {
			if(newLeaderFound) {
				return;
			}
			newLeaderFound = true; 
			if(leader != null) {
				leader.interrupt();
			}
			gm.leader_ip = ip;
			gm.isLeaderAlive();
		}
	}
}
