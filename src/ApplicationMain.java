/**
 * Created by Ahmed on 10/26/2016.
 */

import javax.swing.*;

import asciiPanel.AsciiPanel;
import debug.Logger;
import game.GameController;
import game.GameRenderer;
import game.GameState;
import networking.Client;
import networking.ServerInfo;
import networking.Server;

import java.awt.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ApplicationMain
{
	static boolean primary= false;
    private static void SetupClient(String ip, String port)
    {
        Thread thread;

        // Create the renderer and run on thread
        GameRenderer renderer = new GameRenderer();
        thread = new Thread(renderer, "RendererS");
        thread.start();

        // Create client and run on thread
        Client client = new Client(ip,Integer.parseInt(port));
        thread = new Thread(client, "Client");
        thread.start();

        // Create the controller and add it to the JFrame
        GameController controller = new GameController(client);
        renderer.addController(controller);
    }

    private static void SetupServer(String port) throws NumberFormatException, UnknownHostException
    {
        Server server = new Server(Integer.parseInt(port), primary);
      
        Thread thread = new Thread(server);
      
        thread.start();
        
        
    }

    public static void main(String[] args) throws NumberFormatException, UnknownHostException
    {
    	
    	ServerInfo frontEnd = ServerInfo.getInstance();
    
    
    	
    	
    	
        // Create a new game state
        new GameState();

        // Handle command line arguments
        for (int i = 0; i < args.length; i++)
        {
        	
            if (args[i].equalsIgnoreCase("-c"))
            {
                SetupClient(args[++i],args[++i]);
            }
            else if (args[i].equalsIgnoreCase("-p"))
            {
            	System.out.println("is prim");
            	primary = true;
            }
            else if (args[i].equalsIgnoreCase("-s"))
            {
                SetupServer(args[++i]);
            }
            else if (args[i].equalsIgnoreCase("-d"))
            {
                Logger.debug = true;
            }
            
        }
    	
       
    }
}
