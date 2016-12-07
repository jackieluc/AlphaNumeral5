package networking.groupmanager.commands;

import networking.groupmanager.GroupManager;

public class LeaderCommand extends GroupCommand {
	public String ip;
	
	public LeaderCommand(String ip) {
		this.ip = ip;
	}

	@Override
	public void read() {
		GroupManager gm = GroupManager.getInstance();
		gm.election.newLeader(ip);
		multicast_count--;
		if(multicast_count < 0) {
			return;
		}
		gm.multicast(this);
	}
}
