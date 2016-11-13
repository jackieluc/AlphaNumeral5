package networking.commands;

import debug.Logger;
import networking.Client;
import networking.Server;
import networking.Server.ClientManager;

/**
 * Created by Ahmed on 11/7/2016.
 */
public class WelcomeCommand extends Command
{
    @Override
    public void updateClient(Client client)
    {
        Logger.log("WELCOME TO THE SERVER!");
    }

	@Override
	public boolean valid() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void updateServer(Server server, ClientManager clientManager) {
		// TODO Auto-generated method stub
		
	}
}
