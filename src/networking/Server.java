package networking;

import game.Player;
import networking.commands.Command;
import networking.commands.WelcomeCommand;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static debug.Logger.log;


public class Server implements Runnable
{
	private int port = -1;
    // List of players and their client managers
    public ArrayList<ClientManager> clients;
    public Dictionary<Player, ClientManager> players;

    /**
     * Manages a client (Listens on another thread, sends commands)
     */
	private class ClientManager implements Runnable
	{
		private Server server;
		private Socket socket;
		private Serializer serializer;

		public ClientManager(Server server, Socket socket)
		{
			this.server = server;
			this.socket = socket;
			serializer = new Serializer(socket);

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

		@Override
		public void run()
		{
			Command command;

            serializer.writeToSocket(new WelcomeCommand());

			while ((command = (Command) serializer.readFromSocket()) != null)
			{
                //
                log("Command recieved from " + socket.getRemoteSocketAddress());
				//
				if (command != null && command.verify())
				{
					command.updateServer(server);
					command.updateState();
				}
			}

			log("socket closed");
		}
	}
	
	public Server(int port)
	{
		this.port = port;
        clients = new ArrayList<>(100);
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
