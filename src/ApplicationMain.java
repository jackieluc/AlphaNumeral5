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
import networking.Server;

import java.awt.*;

public class ApplicationMain
{
    private static void SetupClient(String ip, String port)
    {
        Thread thread;

        // Create the renderer and run on thread
        GameRenderer renderer = new GameRenderer();
        thread = new Thread(renderer, "Renderer");
        thread.start();

        // Create client and run on thread
        Client client = new Client(ip,Integer.parseInt(port));
        thread = new Thread(client, "Client");
        thread.start();

        // Create the controller and add it to the JFrame
        GameController controller = new GameController(client);
        renderer.addController(controller);
    }

    private static void SetupServer(String port)
    {
        Server server = new Server(Integer.parseInt(port));
        Thread thread = new Thread(server);
        thread.start();
    }

    public static void main(String[] args)
    {
        // Create a new game state
        new GameState();

        // Handle command line arguments
        for (int i = 0; i < args.length; i++)
        {
            if (args[i].equalsIgnoreCase("-c"))
            {
                SetupClient(args[++i],args[++i]);
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
