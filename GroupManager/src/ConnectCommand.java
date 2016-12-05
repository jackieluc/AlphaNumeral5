
public class ConnectCommand extends GroupCommand {
	public String ip;
	
	public ConnectCommand(String ip) {
		this.ip = ip;
	}
	
	@Override
	public void read() {
		GroupManager gm = GroupManager.getInstance();
		gm.add(ip);
		multicast_count--;
		if(multicast_count < 0) {
			return;
		}
		gm.multicast(this);
	}
}
