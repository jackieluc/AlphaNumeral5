package networking.commands;

import networking.Server;

public class RegisterBackupServerCommand extends Command
{
    @Override
    public void updateServer(Server server, Server.ClientConnection clientConnection)
    {
        // Close the client connection (this is not a client)
        clientConnection.prepareForTransfer();

        Server.BackupServerConnection connection = new Server.BackupServerConnection(clientConnection);
        server.getBackupServers().add(connection);
    }
}
