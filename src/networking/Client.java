package networking;

import java.net.Socket;

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
	
	public Client(String s, int p)
	{
		this.serverIP = s;
		this.serverPort = p;
	}

	public void stop()
    {

    }

    @Override
    public void run()
    {
        try
        {
            socket = new Socket(serverIP, serverPort);
            serializer = new Serializer(socket);
            log("go");
            // Loop and wait for packets
            Command command;
            while ((command = (Command) serializer.readFromSocket()) != null)
            {
                serializer.writeToSocket(new MoveCommand());
                //
                if (command != null)
                {
                    command.updateClient(this);
                    command.updateState();
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
