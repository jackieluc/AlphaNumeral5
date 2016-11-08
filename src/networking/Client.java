package networking;

import java.net.Socket;
import java.util.Scanner;

import debug.Logger;
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

    }

    public void send(Command command)
    {
        synchronized (socket)
        {
            serializer.writeToSocket(command);
        }
    }

    @Override
    public void run()
    {
        try
        {
            socket = new Socket(serverIP, serverPort);
            serializer = new Serializer(socket);

            // Loop and wait for packets
            Command command;
            while ((command = (Command) serializer.readFromSocket()) != null)
            {
                //
                if (command != null)
                {
                    Logger.log("Command recieved of type " + command);

                    command.updateState();
                    command.updateClient(this);
                }
            }
        }
        catch (Exception ex)
        {
            Logger.log("Error! Client has crashed!");
            Logger.log(ex);
        }
    }
}
