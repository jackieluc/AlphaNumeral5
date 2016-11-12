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
	
	public Client(String serverIP, int serverPort)
	{
		this.serverIP = serverIP;
		this.serverPort = serverPort;
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