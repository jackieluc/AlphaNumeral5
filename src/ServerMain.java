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
import java.net.InetAddress;
import java.net.UnknownHostException;

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
    	try {
			System.err.println("connect to "+ InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        // Create a new game state
        GameState.getInstance();
        Logger.debug = true;
        int port = 5001;
        SetupServer(port+"");

     
    }
}
