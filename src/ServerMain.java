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

public class ServerMain
{
   
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
        Logger.debug = true;
        int port = 5000;
        SetupServer(port+"");

     
    }
}
