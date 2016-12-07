import debug.Logger;
import game.GameState;
import networking.Server;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ServerMain
{
    private static void SetupServer(int port)
    {
        Server server = new Server(port);
        Thread thread = new Thread(server);
        thread.start();
    }

    public static void main(String[] args)
    {
    	try
        {
			System.err.println("Connect to: " + InetAddress.getLocalHost().getHostAddress() + ":" + args[0]);
		}
		catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        // Create a new game state
        GameState.getInstance();
        Logger.debug = true;

        // Port is entered into the program arguments
        int port = Integer.parseInt(args[0]);
        SetupServer(port);
    }
}
