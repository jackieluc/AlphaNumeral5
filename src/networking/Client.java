package networking;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import debug.Logger;
import game.GameRenderer;
import networking.commands.Command;
import networking.commands.MoveCommand;
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
	
	public Client()
	{
        username = null;
	}

	public boolean isConnected()
    {
        return (username != null);
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

    /**
     * The client listen loop
     */
    @Override
    public void run()
    {
        // If can't connect, close and quit program
        if (!connect())
        {
            close();
            return;
        }

        // Create a serializer for this socket
        serializer = new Serializer(socket);

        // Loop and wait for packets
        Command command;
        while ((command = (Command) serializer.readFromSocket()) != null)
        {
            //
            if (command != null)
            {
                log("Command recieved of type " + command);

                command.updateState();
                command.updateClient(this);
            }
        }
        try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        run();
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
        /*try
        {
            socket = new Socket(serverIP, serverPort);
            return true;
        }
        catch (IOException ex)
        {
            log("Error connecting to server");
            log(ex);
            return false;
        }*/
    	//System.err.println("client 101 serverPort "+ serverPort);
        ServerList serverList = new ServerList(serverPort);
        socket = serverList.getConnectionToMasterServer();
       // System.err.println("returned socket in client line 103 "+socket);
        return (socket != null);
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
