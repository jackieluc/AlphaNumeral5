package networking;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import game.GameRenderer;
import networking.commands.Command;
import networking.commands.RegisterUserCommand;

import static debug.Logger.log;

public class Client implements Runnable
{
    private String proxyIP;
    private static final int PROXYPORT = 10000;
    //
    private Socket socket;
    private Serializer serializer;
    //
    public String username;
	
	public Client(String proxyIP)
	{
        this.username = null;
        this.proxyIP = proxyIP;
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

        log("Connected to proxy. my ip is ");
        // Create a serializer for this socket
        serializer = new Serializer(socket);
        //send(new RegisterUserCommand("Ahmad"));

        // Loop and wait for packets
        Command command;
        while ((command = (Command) serializer.readFromSocket()) != null)
        {
            log("Command received of type " + command);

            command.updateState();
            command.updateClient(this);
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
//        ServerList serverList = new ServerList(serverPort);

        //connect to the proxy
//        Socket socket = null;
        try
        {
            this.socket = new Socket(proxyIP, PROXYPORT);
        }
        catch (IOException e)
        {
            //todo reconnect for a certain amount of time
            e.printStackTrace();
        }
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
