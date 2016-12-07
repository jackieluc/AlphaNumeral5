package networking.groupmanager.groupCommands;

import java.io.Serializable;

public abstract class GroupCommand implements Serializable {
	public int multicast_count = 1;
	
	public abstract void read();
}
