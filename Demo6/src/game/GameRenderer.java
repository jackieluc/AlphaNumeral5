package game;

import asciiPanel.AsciiPanel;
import debug.Logger;

import javax.swing.*;
import java.awt.event.KeyListener;
import java.util.HashMap;

/**
 * Created by Ahmed on 11/7/2016.
 */
public class GameRenderer extends JFrame implements Runnable
{
    public static GameRenderer current;

    private AsciiPanel terminal;
    private boolean needsRedrawing;

    private boolean isRunning;

    public GameRenderer()
    {
        super();
        //
        current = this;
        //
        isRunning = true;
        // Add AsciiPanel to JFrame
        terminal = new AsciiPanel();
        this.add(terminal);
        this.pack();

        drawMap();
    }

    public void redraw()
    {
        synchronized (GameState.current)
        {
            needsRedrawing = true;
//            Logger.log("REDRAW PLZ" + needsRedrawing);
        }
    }

    /**
     * Adds a controller to the JFrame
     * @param listener
     */
    public void addController(KeyListener listener)
    {
        synchronized (this)
        {
            this.addKeyListener(listener);
        }
    }

    public void close()
    {
        synchronized (this)
        {
            isRunning = false;
            this.setVisible(false);
        }
    }

    @Override
    public void run()
    {
        try
        {
            while (isRunning)
            {
                //Logger.log(needsRedrawing);
                if (needsRedrawing)
                {
//                    Logger.log("REDRAW");
                    synchronized (GameState.current)
                    {
                        drawMap();
                        drawPlayers();
                        needsRedrawing = false;
                        repaint();
                        terminal.repaint();
                    }
                }
                // FIX error
                Thread.sleep(10);
            }
        }
        catch (Exception ex)
        {
            Logger.log("Error in renderer thread!");
            Logger.log(ex);
        }

        Logger.log("Renderer closed...");

        // Close everything
        this.dispose();
    }

    private void drawMap()
    {
//    	HashMap<String,Player> players = GameState.current.players;
        Map map = GameState.getInstance().map;

        for (int x = 0; x < map.getWidth(); x++)
        {
            for (int y = 0; y < map.getHeight(); y++)
            {
                terminal.write(map.getTile(x, y), x, y);
            }
        }
    }

    private void drawPlayers()
    {
    	char letter = 'a';
    	synchronized (GameState.current)
    	{
	        HashMap<String,Player> players = GameState.current.players;
	
	        for (Player p : players.values())
	        {
	        	String username = p.username;
	        	
	        	if (username.equalsIgnoreCase("Jackie"))
	        		letter = 'J';
	        	else if (username.equalsIgnoreCase("Emil"))
	        		letter = 'E';
	        	else if (username.equalsIgnoreCase("Ahmad"))
	        		letter = 'A';
	        	else if (username.equalsIgnoreCase("Ahmed"))
	        		letter = 'a';
	        	else
	        		letter = 'z';
	        	
	        	terminal.write(letter, p.x, p.y);
	        }
    	}
    }
}