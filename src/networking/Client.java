package networking;

import java.net.Socket;
import java.net.UnknownHostException;

import game.GameRenderer;
import networking.commands.Command;
import networking.commands.RegisterUserCommand;

import static debug.Logger.log;

public class Client implements Runnable
{
    private int serverPort;
    private Socket socket;
    private Serializer serializer;
    public String username;
    private boolean reconnected;
	
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
        // Will try to reconnect indefinitely
        // TODO: timeout client if cannot connect
        while(true) {
            reconnected = true;
            // If can't connect, wait for a certain delay
            if (!connect())
            {
                reconnected = false;
                // Poll delay of 2 seconds
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                log("Disconnected from server... trying to reconnect...");
            }
            else {
                if (reconnected) {
                    try {
                        System.out.println("Reconnected to: " + socket.getInetAddress().getLocalHost().getHostAddress() + ":" + serverPort);
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                }
                // Create a serializer for this socket
                serializer = new Serializer(socket);

                new RegisterUserCommand(username).updateClient(this);

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
            }
        }
    }

    /**
     * Tries to connect to a server
     * @return whether connection was successful
     */
    private boolean connect()
    {
        // Get the primary server to connect to from the list of servers
        ServerList serverList = new ServerList(serverPort);
        socket = serverList.getConnectionToMasterServer();
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
