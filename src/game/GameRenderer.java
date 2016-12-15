package game;

import asciiPanel.AsciiPanel;

import javax.swing.*;
import java.awt.event.KeyListener;
import java.util.HashMap;

import static debug.Logger.log;

public class GameRenderer extends JFrame implements Runnable
{
    public static GameRenderer current;

    private AsciiPanel terminal;
    private boolean needsRedrawing;

    private boolean isRunning;

    public GameRenderer()
    {
        super();
        current = this;

        isRunning = true;
        terminal = new AsciiPanel();
        this.add(terminal);
        this.pack();

        drawMap();
    }

    public void redraw()
    {
        synchronized (GameState.getInstance())
        {
            needsRedrawing = true;
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
                if (needsRedrawing)
                {
                    synchronized (GameState.getInstance())
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
            log("Error in renderer thread!");
            log(ex);
        }

        log("Renderer closed...");

        // Close everything
        this.dispose();
    }

    private void drawMap()
    {
        Map map = GameState.getInstance().getMap();

        for (int x = 0; x < map.getWidth(); x++)
        {
            for (int y = 0; y < map.getHeight(); y++)
                terminal.write(map.getTile(x, y), x, y);
        }
    }

    private void drawPlayers()
    {
    	char letter;

    	synchronized (GameState.getInstance())
    	{
	        HashMap<String,Player> players = GameState.getInstance().getPlayers();
	
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
