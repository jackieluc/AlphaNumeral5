package networking.commands;

import networking.Client;
import networking.Server;

public class QuitCommand extends Command {

	@Override
	public boolean valid() {
		return true;
	}
	
	@Override
	public void updateServer(Server server, Server.ClientManager clientManager) {
		
	}
	
	@Override
	public void updateClient(Client client) {
		
	}
}
