package networking;

import debug.Logger;
import game.GameState;
import game.Player;
import networking.commands.Command;
import networking.commands.MoveCommand;
import networking.commands.RegisterUserCommand;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static debug.Logger.log;


public class Server implements Runnable
{
	private int port = -1;
    //
    private ServerSocket serverSocket;
    //
    public ExecutorService executorService;
    // All clients in game, key is username
	public HashMap<String,ClientConnection> inGameClients;
	// List of players and their client managers
    public ArrayList<ClientConnection> clients;
    //
    public ArrayList<BackupServerConnection> backupServers;

    /**
     * Manages a client (Listens on another thread, sends commands)
     */
	public class ClientConnection extends Connection implements Runnable
	{
		public String username;
		private Server server;
        private boolean isTransferred;

		public ClientConnection(Server server, Socket socket)
		{
            this.socket = socket;
            //
            sendMasterSignal();
            //
            serializer = new Serializer(socket);

			// reset username
			this.username = null;
			// set vars
			this.server = server;

			// add to list of clients
			clients.add(this);

			log("Connection from " + socket.getRemoteSocketAddress());
		}

		@Override
		public void run()
		{
			Command command;

			// Tell the user to register (no commands will be accepted until successful registration)
			serializer.writeToSocket(new RegisterUserCommand(null));

			// Wait for commands from client
			while ((command = (Command) serializer.readFromSocket()) != null)
			{
				//
				log("Command recieved from " + socket.getRemoteSocketAddress() + " of type " + command);
				//
				if (command != null && command.verify())
				{
					Logger.log("Is valid command");
					command.updateState();
					command.updateServer(server, this);

                    // Send to backup servers
                    backup(command);
				}
			}

			// close everything
            if (!isTransferred)
			    close();
		}

		/**
		 * Sends a 1 to connected clients to signify that this is the master
		 */
		void sendMasterSignal()
		{
			try
			{
				socket.getOutputStream().write(1);
			}
			catch (IOException ex)
			{
				log("Error sending Master signal");
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
                super.close();

                clients.remove(this);
                inGameClients.remove(username);

                log("User \"" + username + "\" disconnected!");
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
            for (HashMap.Entry<String, Player> p : GameState.current.players.entrySet())
            {
                send(new MoveCommand(p.getKey(), p.getValue().x, p.getValue().y));
            }
        }
	}
	
	public Server(int port)
	{
		this.port = port;

        clients = new ArrayList<>(100);
        backupServers = new ArrayList<>(10);

		inGameClients = new HashMap<String, ClientConnection>();
	}

    /**
     * Sends a command to all clients
     * @param command Command to send
     */
    public void sendAll(Command command)
    {
        for (Connection connection : clients)
        {
            connection.send(command);
        }
        //sendSerialized(clients, Serializer.serialize(command));
    }

    public void backup(Command command)
    {
        for (Connection connection : backupServers)
        {
            connection.send(command);
        }
        //sendSerialized(backupServers, Serializer.serialize(command));
    }

    private void sendSerialized(ArrayList<? extends Connection> list, byte[] bytes)
    {
        try
        {
            for (Connection connection : list)
            {
                connection.sendSerialized(bytes);
            }
        }
        catch (IOException ex)
        {
            Logger.log("Error sending to all!");
        }
    }

    /**
     * Creates a server socket
     * @return
     */
	private ServerSocket createServerSocket()
	{
		try
		{
			return new ServerSocket(port);
		}
		catch (Exception ex)
		{
			log("Error creating server socket!");
			log(ex);
		}

		return null;
	}

	@Override
	public void run()
	{
		try
		{
			serverSocket = createServerSocket();
			executorService = Executors.newCachedThreadPool();

			if (serverSocket != null)
			{
				while (true)
				{
					log("Waiting for connection...");
					ClientConnection clientConnection = new ClientConnection(this, serverSocket.accept());
					executorService.submit(clientConnection);
				}
			}
		}
		catch (Exception ex)
		{
			log("Error in server loop!");
			log(ex);
		}
    } // run
} // networking.Server
