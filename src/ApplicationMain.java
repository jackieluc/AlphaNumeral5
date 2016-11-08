/**
 * Created by Ahmed on 10/26/2016.
 */

import javax.swing.*;

import asciiPanel.AsciiPanel;
import debug.Logger;
import networking.Client;
import networking.Server;

public class ApplicationMain extends JFrame
{
    private AsciiPanel terminal;

    public ApplicationMain()
    {
        super();
        terminal = new AsciiPanel();
        add(terminal);
        pack();
    }

    private static void SetupClient(String ip, String port)
    {
        Client client = new Client(ip,Integer.parseInt(port));
        Thread thread = new Thread(client);
        thread.run();
    }

    private static void SetupServer(String port)
    {
        Server server = new Server(Integer.parseInt(port));
        Thread thread = new Thread(server);
        thread.run();
    }

    public static void main(String[] args)
    {
        //ApplicationMain app = new ApplicationMain();
        //app.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //app.setVisible(true);

        //app.terminal.write("rl tutorial", 0, 0);

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
