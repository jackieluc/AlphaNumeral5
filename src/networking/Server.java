package networking;

import FileIO.WriteFile;
import debug.Logger;
import game.GameState;
import game.Player;
import networking.commands.Command;
import networking.commands.MoveCommand;
import networking.commands.RegisterBackupServerCommand;
import networking.commands.RegisterUserCommand;
import networking.groupmanager.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static debug.Logger.log;


public class Server implements Runnable
{
	boolean backup = false;
    private static boolean isRunning;
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
     * Manages a client (Listens on another thread, sends groupCommands)
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

			//System.err.println("backup>> "+ backup);
			// Tell the user to register (no groupCommands will be accepted until successful registration)
			serializer.writeToSocket(new RegisterUserCommand(null));

			// Wait for groupCommands from client
			while ((command = (Command) serializer.readFromSocket()) != null)
			{
				
				//
				log("Command recieved from " + socket.getRemoteSocketAddress() + " of type " + command);
				//
				if (command.verify())
				{
					backup(command);
					command.updateState();
					command.updateServer(server, this);
					
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
        	synchronized (GameState.current)
        	{
	            for (HashMap.Entry<String, Player> p : GameState.current.players.entrySet())
	            {
	                send(new MoveCommand(p.getKey(), p.getValue().x, p.getValue().y));
	                
//	                Logger.log("Writing to disk on backup...");
//	                new WriteFile(p.getKey()).writeToDisk();
	            }
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

	public void setupGroupManager(String proxyIP, String aServerIP)
    {
        GroupManager gm = GroupManager.getInstance();

        gm.proxy_ip = proxyIP;

        //if there is an IP to connect to the group of servers
        if(aServerIP.length() > 0)
            gm.initialize(aServerIP);

        gm.election = new Election();
        gm.isLeaderAlive();
        gm.run();
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
			
			log("Error creating server socket! at "+port);
			log(ex);
		}

		return null;
	}

	@Override
	public void run()
	{
		connectToMaster();
		
    } // run
	
	 /**
     * Connect to the master server
     */
    void connectToMaster()
    {
    	executorService = Executors.newCachedThreadPool();
     //	serverSocket = createServerSocket();
        ServerList serverList = new ServerList(port);
       // System.err.println("returning from server list");
        Socket socket = serverList.getConnectionToMasterServer();
        if (socket != null)
        {
        	//serverSocket = createServerSocket();
        	//Connection connection = new Connection(socket);
        	backup=true;
        	//log(" **** primary found at "+ socket + "****"
        	//		+ "****starting server as a backup ****");
        	// connection.send(new RegisterBackupServerCommand());     	        	
       /**  if (serverSocket != null)
			{       		
				while (true)
				{
					log("backup Waiting for connection...");
					try{
						//Socket clientSocket = serverSocket.accept();
					ClientConnection clientConnection = new ClientConnection(this, serverSocket.accept());
					// sendBackupSignal(clientSocket);
					executorService.submit(clientConnection);
					}catch (Exception ex)
					{
						log("Error in server loop!");
						log(ex);
					}
				}
			}**/
			
        	//executorService = Executors.newCachedThreadPool();
        	//serverSocket = createServerSocket();   	
        	backup=true;
        	//ClientConnection clientConnection = null;
        	log(" **** primary found at "+ socket + "****"
        			+ "****starting server as a backup ****");
        //	System.err.println("line 143 BS socker "+socket.getPort());
            MasterServerConnection connection = new MasterServerConnection(this,socket);
            Thread thread = new Thread(connection);
            thread.start();
        }
        
        
        
 
       ///////////////
        else{
        	log("**** no primary found, starting server as primary ****");
        	executorService = Executors.newCachedThreadPool();
        	serverSocket = createServerSocket();
        	if (serverSocket != null)
			{
				
				while (true)
				{
					log("Waiting for connection...");
					try{
					ClientConnection clientConnection = new ClientConnection(this, serverSocket.accept());
					executorService.submit(clientConnection);
					}catch (Exception ex)
					{
						log("Error in server loop!");
						log(ex);
					}
				}
			}

        }
    
    }
    
    
    // Sends a 0 to connected clients to signify that this is a backup
    void sendBackupSignal(Socket clientSocket)
    {
        try
        {
            clientSocket.getOutputStream().write(0);
        }
        catch (IOException ex)
        {
            log("Error sending Master signal");
        }
    }
    
   /////////////////////
    private class MasterServerConnection extends Connection implements Runnable
    {
    //	private static boolean isRunning;
    	ClientConnection clientConnection;
    	Server server;
        public MasterServerConnection(Server server, Socket socket)
        {
            super(socket);
            this.server=server;
        }

        @Override
        public void run()
        {
            Command command;
            
            serializer.writeToSocket(new RegisterBackupServerCommand());

            // Wait for groupCommands from client
            while ((command = (Command) serializer.readFromSocket()) != null)
            {
                //
                log("Command recieved from Master Server of type " + command);
                //
                command.updateState();
                
                if(command instanceof MoveCommand)
                	new WriteFile(((MoveCommand) command).username).writeToDisk();
            }
            System.err.println("server crashed");

            // close everything
            try
            {
            	connectToMaster();
            	System.err.println("closing");
                isRunning = false;
                close();
            }
            catch (IOException ex)
            {
                Logger.log("Error closing master server connection");
            }
        }
    }
} // networking.Server
