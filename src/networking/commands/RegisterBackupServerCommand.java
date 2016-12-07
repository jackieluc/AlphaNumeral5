package networking.commands;

import networking.Server;

/**
 * Created by Ahmed on 11/14/2016.
 * Used by backup servers to ask for constant state updates
 */
public class RegisterBackupServerCommand extends Command
{
    @Override
    public void updateServer(Server server, Server.ClientConnection clientConnection)
    {
        // Close the client connection (this is not a client)
        clientConnection.prepareForTransfer();
        //
        Server.BackupServerConnection connection = new Server.BackupServerConnection(clientConnection);
        server.backupServers.add(connection);
    }
}
