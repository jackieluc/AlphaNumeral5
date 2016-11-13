package networking;

import java.io.IOException;
import java.net.Socket;
import game.GameRenderer;
import networking.commands.Command;
import networking.commands.MoveCommand;
import networking.commands.RegisterUserCommand;

import static debug.Logger.log;

public class Client implements Runnable
{
	private String serverIP;
	private int serverPort;
    //
    private Socket socket;
    private Serializer serializer;
    //
    public String username;
    public boolean registered = false;
	
	public Client(String serverIP, int serverPort)
	{
		this.serverIP = serverIP;
		this.serverPort = serverPort;
	}

	public boolean isConnected()
    {
        return socket.isClosed() ? false : socket.isConnected();
    }

	public void stop()
    {
        close();
    }

    public void send(Command command)
    {
        synchronized (socket)
        {
            serializer.writeToSocket(command);
        }
    }
    
    public void register() {
    	// Wait until valid registration response
    	Command command;
    	do {
    		command = (Command)serializer.readFromSocket();
    		if(command instanceof RegisterUserCommand) {
    			command.updateClient(this);
    			if(command.valid()) {
    				break;
    			}
    		}
    	} while(isConnected());
    	
    	// Wait until valid initial move response
    	do {
    		command = (Command)serializer.readFromSocket();
    		if(command instanceof MoveCommand) {
    			if(command.valid()) {
    				command.updateState();
                    command.updateClient(this);
    				break;
    			}
    		}
    	} while(isConnected());
    	
    	// Registration complete
    	registered = true;
    }

    /**
     * The client listen loop
     */
    @Override
    public void run()
    {
        // If can't connect, close and quit program
        if(!connect()) {
            close();
            return;
        }

        // Create a serializer for this socket
        serializer = new Serializer(socket);

        register();
        
        // Loop and wait for packets
        Command command;
        while ((command = (Command) serializer.readFromSocket()) != null) {
            //
            if (command != null) {
                log("Command recieved of type " + command);

                command.updateState();
                command.updateClient(this);
            }
        }

        // TODO try to reconnect
        log("Disconnected from server...");

        // Close
        close();
    }

    /**
     * Tries to connect to a server
     * @return whether connection was successful
     */
    private boolean connect()
    {
        try
        {
            socket = new Socket(serverIP, serverPort);
            return true;
        }
        catch (IOException ex)
        {
            log("Error connecting to server");
            log(ex);
            return false;
        }
    }

    /**
     * Closes the socket and the gamerenderer
     */
    private void close()
    {
        try
        {
            if (socket != null) socket.close();
            if (GameRenderer.current != null) GameRenderer.current.close();
        }
        catch (Exception ex)
        {
            log("Error closing client!");
            log(ex);
        }
    }
}
