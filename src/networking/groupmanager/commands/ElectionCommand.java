package networking.groupmanager.commands;

import networking.groupmanager.GroupManager;

public class ElectionCommand extends GroupCommand {
	public String ip;
	
	public ElectionCommand(String ip) {
		this.ip = ip;
	}
	
	@Override
	public void read() {
		GroupManager gm = GroupManager.getInstance();
		if(gm.isLeaderAlive != null) {
			gm.isLeaderAlive.interrupt();
		}
		String current = gm.election.current_leader;
		ip = gm.election.elect(ip);
		
		if(ip.equals(current)) {
			multicast_count--;
		} else {
			multicast_count = 1;
		}
		if(multicast_count < 0) {
			return;
		}
		gm.multicast(this);
	}
}
