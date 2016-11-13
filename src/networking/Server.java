package networking;

import networking.commands.Command;
import networking.commands.QuitCommand;
import networking.commands.RegisterUserCommand;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static debug.Logger.log;


public class Server implements Runnable
{
	private int port = -1;
    // All clients in game, key is username
	public HashMap<String,ClientManager> inGameClients;
	// List of players and their client managers
    public ArrayList<ClientManager> clients;

    /**
     * Manages a client (Listens on another thread, sends commands)
     */
	public class ClientManager implements Runnable
	{
		public String username;
		private Server server;
		private Socket socket;
		private Serializer serializer;

		public ClientManager(Server server, Socket socket)
		{
			// reset username
			this.username = null;
			// set vars
			this.server = server;
			this.socket = socket;
			serializer = new Serializer(socket);

			// add to list of clients
            clients.add(this);

			log("Connection from " + socket.getRemoteSocketAddress());
		}

        /**
         * Serializes and sends a command
         * @param command
         */
		public void send(Command command)
        {
            synchronized (socket)
            {
                serializer.writeToSocket(command);
            }
        }

        public void sendSerialized(byte[] bytes)
        {

        }

        public boolean isConnected() {
            return socket.isClosed() ? false : socket.isConnected();
        }
        
        private void registerUser() {
        	// Send request for registration
        	serializer.writeToSocket(new RegisterUserCommand(null));
        	
        	// Wait for a valid registration response
        	Command command;
        	do {
        		command = (Command)serializer.readFromSocket();
        		if(command instanceof RegisterUserCommand) {
        			command.updateServer(server, this);
        			if(command.valid()) {
        				command.updateState();
        				break;
        			}
        		}
        	} while(isConnected());
        }
        
        private void receiveCommands() {
        	Command command;
        	do {
        		command = (Command)serializer.readFromSocket();
        		if(command.valid()) {
        			command.updateState();
        			command.updateServer(server, this);
        		}
        		if(command instanceof QuitCommand) {
        			break;
        		}
        	} while(isConnected());
        }
        
		@Override
		public void run()
		{
			registerUser();
			receiveCommands();

			// close everything
			close();
		}

        /**
         * Remove from lists and close socket
         */
		public void close()
        {
            try
            {
                clients.remove(this);
                inGameClients.remove(username);
                socket.close();

                log("User \"" + username + "\" disconnected!");
            }
            catch (IOException ex)
            {
                log("Error closing client manager!");
                log(ex);
            }
        }
	}
	
	public Server(int port)
	{
		this.port = port;
        clients = new ArrayList<>(100);
		inGameClients = new HashMap<String, ClientManager>();
	}
	
	public boolean registered(String username) {
		return (inGameClients.get(username) != null);
	}

    /**
     * Sends a command to all clients
     * @param command Command to send
     */
    public void sendAll(Command command)
    {
        for (ClientManager client : clients)
        {
            client.send(command);
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
			ServerSocket serverSocket = createServerSocket();
			ExecutorService executorService = Executors.newCachedThreadPool();

			if (serverSocket != null)
			{
				while (true)
				{
					log("Waiting for connection...");
					ClientManager clientManager = new ClientManager(this, serverSocket.accept());
					executorService.submit(clientManager);
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
