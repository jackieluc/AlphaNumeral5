import debug.Logger;
import game.GameState;
import networking.Server;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ServerMain
{
    private static final int PORT = 4000;
   
    private static void SetupServer(String proxyIP, String aServerIP)
    {
        Server server = new Server(PORT);
//        Thread thread = new Thread(server);
//        thread.start();
        server.setupGroupManager(proxyIP, aServerIP);
    }

    public static void main(String[] args)
    {
    	try {
			System.err.println("connect to "+ InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        // Create a new game state
        GameState.getInstance();
        Logger.debug = true;

        //args[0] = proxy ip
        //args[1] = the ip of a server that is in the group of servers
        if(args.length == 2)
            SetupServer(args[0], args[1]);
        else if (args.length == 1)
            SetupServer(args[0], "");
        else
        {
            System.err.println("Please enter the correct format to start the server:");
            System.err.println("1. <IP of proxy>");
            System.err.println("2. <IP of proxy> <IP of server you are trying to connect to>");
            System.exit(2);
        }
    }
}
