package networking.commands;

import networking.Client;
import networking.Server;

import java.io.Serializable;

public abstract class Command implements Serializable
{
    /**
     * Check if this command is valid
     * @return Is a valid command
     */
    public boolean verify()
    {
        return true;
    }

    /**
     * Updates the game state (both client and server
     */
    public void updateState(){}

    /**
     * Updates the client
     * ONLY CALLED ON CLIENT
     */
    public void updateClient(Client client){}

    /**
     * Updates the server
     * ONLY CALLED ON SERVER
     */
    public void updateServer(Server server, Server.ClientConnection clientConnection){}
}
