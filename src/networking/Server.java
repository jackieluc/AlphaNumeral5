package networking;

import game.GameState;
import game.Player;
import networking.commands.Command;
import networking.commands.MoveCommand;
import networking.commands.RegisterBackupServerCommand;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import FileIO.WriteFile;

import static debug.Logger.log;


public class Server implements Runnable
{
    private int port = -1;
    private ServerSocket serverSocket;

    private ExecutorService executorService;

    // All clients in game, key is username
	private HashMap<String,ClientConnection> inGameClients;

	// List of players
    private ArrayList<ClientConnection> clients;

    // List of backup servers
    private ArrayList<BackupServerConnection> backupServers;

    /**
     * Manages a client (Listens on another thread, sends commands)
     */
	public class ClientConnection extends Connection implements Runnable
	{
		public String username;
		private Server server;
        private boolean isTransferred;

		private ClientConnection(Server server, Socket socket)
		{
            this.socket = socket;

            // Notifies client that this is the primary server
            sendPrimarySignal();

            serializer = new Serializer(socket);
			this.username = null;
			this.server = server;

			// Add to list of clients
			clients.add(this);

			log("Connection from " + socket.toString());
		}

		@Override
		public void run()
		{
			Command command;

			// Wait for commands from client
			while ((command = (Command) serializer.readFromSocket()) != null)
			{

                if(command instanceof RegisterBackupServerCommand)
				    log("Command received from Backup Server of type " + command.getClass().toString());
                else
                    log("Command received from Client of type " + command.getClass().toString());

                // Verify that it is a command, discard otherwise
				if (command.verify())
				{
                    // Send to backups
					backup(command);

                    // Update state
					command.updateState();

                    // Update server information
					command.updateServer(server, this);

                    // Write to disk the new position of the player
					if(command instanceof MoveCommand)
	                	new WriteFile(((MoveCommand) command).username).writeToDisk();
	                
				}
			}

			// close everything
            if (!isTransferred)
			    close();
		}

		/**
		 * Sends a 1 to connected clients to signify that this is the master
		 */
		private void sendPrimarySignal()
		{
			try
			{
				socket.getOutputStream().write(1);
			}
			catch (IOException ex)
			{
				log("Error sending Primary signal to backups");
			}
		}

		public void prepareForTransfer()
        {
            clients.remove(this);
            inGameClients.remove(username);
            isTransferred = true;
        }

        /**
         * Remove from lists and close socket
         */
		public void close()
        {
            try
            {
                log("User \"" + username + "\" disconnected!");
                super.close();

                clients.remove(this);
                inGameClients.remove(username);
            }
            catch (IOException ex)
            {
                log("Error closing client manager!");
                log(ex);
            }
        }
	}

	/**
	 *
	 */
	public static class BackupServerConnection extends Connection
	{
        public BackupServerConnection(ClientConnection connection)
        {
            super(connection);
            // Update the server
            updateState();
        }

        /**
         * Update the server to the current state
         */
        private void updateState()
        {
        	synchronized (GameState.getInstance())
        	{
	            for (HashMap.Entry<String, Player> p : GameState.current.players.entrySet())
	                send(new MoveCommand(p.getKey(), p.getValue().x, p.getValue().y));
        	}
        }
	}
	
	public Server(int port)
	{
		this.port = port;

        // The list of clients
        clients = new ArrayList<>(100);

        // The list of backup servers
        backupServers = new ArrayList<>(10);

        // The list of all clients that are in the game
		inGameClients = new HashMap<String, ClientConnection>();
	}

	public HashMap<String,ClientConnection> getInGameClients()
    {
        return inGameClients;
    }

    public ArrayList<ClientConnection> getClients()
    {
        return getClients();
    }

    public ArrayList<BackupServerConnection> getBackupServers()
    {
        return backupServers;
    }

    /**
     * Sends a command to all clients
     * @param command Command to send
     */
    public void sendAll(Command command)
    {
        for (Connection connection : clients)
            connection.send(command);
    }

    public void backup(Command command)
    {
        for (Connection connection : backupServers)
            connection.send(command);
    }

    /**
     * Creates a server socket
     * @return ServerSocket
     */
	private ServerSocket createServerSocket()
	{
		try
		{
			return new ServerSocket(port);
		}
		catch (Exception ex)
		{
			log("Error creating server socket! at " + port);
			log(ex);
		}

		return null;
	}

	@Override
	public void run()
	{
		connectToMaster();
    }
	
	 /**
     * Connect to the master server
     */
    private void connectToMaster()
    {
    	executorService = Executors.newCachedThreadPool();

        // Get the server list and the socket containing connection to the primary server
        ServerList serverList = new ServerList(port);
        Socket socket = serverList.getConnectionToMasterServer();

        if (socket != null)
        {
        	log("**** Primary server found ****");
            log("**** Starting server as a backup ****");

            // Grab the connection and start the Backup thread
            MasterServerConnection connection = new MasterServerConnection(socket);
            Thread thread = new Thread(connection);
            thread.start();
        }
        else{
        	log("**** No primary server found ****");
            log("**** Starting server as the primary server ****");

        	serverSocket = createServerSocket();
        	if (serverSocket != null)
			{
				while (true)
				{
					log("Waiting for connection...");
					try
                    {
                        // Accept connections to this server
					    ClientConnection clientConnection = new ClientConnection(this, serverSocket.accept());
					    executorService.submit(clientConnection);
					}
					catch (Exception ex)
					{
						log("Error in server loop!");
						log(ex);
					}
				}
			}
        }
    }

    private class MasterServerConnection extends Connection implements Runnable
    {
        private MasterServerConnection(Socket socket)
        {
            super(socket);
        }

        @Override
        public void run()
        {
            Command command;

            // Register the backup to the primary server
            serializer.writeToSocket(new RegisterBackupServerCommand());

            // Wait for commands from the primary server
            while ((command = (Command) serializer.readFromSocket()) != null)
            {
                log("Command received from Master Server of type " + command.getClass().toString());

                // Update state
                command.updateState();

                // Write to disk the new position of the player
                if(command instanceof MoveCommand)
                	new WriteFile(((MoveCommand) command).username).writeToDisk();
            }

            System.err.println("Server crashed.");

            // close everything
            try
            {
            	connectToMaster();
                close();
            }
            catch (IOException ex)
            {
                log("Error closing master server connection");
            }
        }
    }
}