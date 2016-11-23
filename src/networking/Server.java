package networking;

import debug.Logger;
import game.GameState;
import game.Player;
import networking.commands.Command;
import networking.commands.MoveCommand;
import networking.commands.RegisterUserCommand;

import java.io.*;
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
		public void run() {
			Command command;

			// Tell the user to register (no commands will be accepted until successful registration)
			serializer.writeToSocket(new RegisterUserCommand(null));

			// Wait for commands from client
			while ((command = (Command) serializer.readFromSocket()) != null) {
				//
				log("Command recieved from " + socket.getRemoteSocketAddress() + " of type " + command.getClass().toString());
				//
				if (command.verify()) {
					// Send to backup servers
					backup(command);
					command.updateState();
					command.updateServer(server, this);
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
//TODO: fix so that if a user disconnects, don't remove them until after 30 seconds (aka let them try to reconnect)
//                clients.remove(this);
//                inGameClients.remove(username);

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
            for (HashMap.Entry<String, Player> p : GameState.getInstance().getPlayers().entrySet())
            {
                send(new MoveCommand(p.getKey(), p.getValue().x, p.getValue().y));
				log("username: " + p.getKey() + " x: " + p.getValue().x + " y:" + p.getValue().y);
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
		log("Updating backups...");
        for (Connection connection : backupServers)
        {
            connection.send(command);
        }
        //sendSerialized(backupServers, Serializer.serialize(command));
    }

	/**
	 * create a directory called PlayerData if it doesn't exist
	 * create a file for the player if it doesn't exist
	 * update the player's file by storing x and y position
	 * @param username
	 */
	public void writeToDisk(String username)
	{
		String directoryPath = "./PlayerData/";
		String filename = username + ".txt";
		File playerFile = new File(directoryPath + filename);
		File directory = new File(directoryPath);

		//create the "PLayerData" directory if it doesn't exist
		if(!directory.exists())
			directory.mkdir();

		//create the player's file if it doesn't exist
		if(!playerFile.exists())
		{
			try {
				playerFile.createNewFile();
			} catch (IOException e) {
				//TODO: handle exception
				e.printStackTrace();
			}
		}

		try {
			FileWriter fw = new FileWriter(playerFile.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);

			//get the player's position to write to disk
			Player player = GameState.getInstance().getPlayers().get(username);
			bw.write(player.x + " " + player.y);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * retrieve the position stored in the player's file
	 * @param username
	 * @return array of strings, where [0] is x position and [1] is y position
	 */
	public int[] readFromDisk(String username)
	{
		FileReader fr;
		int[] positions = new int[2];
		String directoryPath = "./PlayerData/";
		try {
			File playerFile = new File(directoryPath + username + ".txt");
			fr = new FileReader(playerFile.getAbsolutePath());
			BufferedReader br = new BufferedReader(fr);

			String[] pos = br.readLine().split(" ");

			positions[0] = Integer.parseInt(pos[0]);
			positions[1] = Integer.parseInt(pos[1]);

			fr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return positions;
	}

	/**
	 * TODO: implement such that when the primary server starts up, read all the files from the storage
	 */
	public void readAllFilesFromDisk()
	{

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
