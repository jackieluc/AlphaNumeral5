package networking.commands;

import debug.Logger;
import networking.Client;
import networking.Server;

/**
 * Created by Ahmed on 11/7/2016.
 */
public class MoveCommand extends Command
{
    @Override
    public void updateServer(Server server)
    {
        Logger.log("VALID MOVE COMMAND");

        server.sendAll(this);
    }

    @Override
    public void updateClient(Client client)
    {
        Logger.log("MOVE COMMAND FROM SERVER");
    }
}
