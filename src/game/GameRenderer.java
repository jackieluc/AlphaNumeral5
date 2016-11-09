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
                    synchronized (GameState.current)
                    {
                        drawMap();
                        drawPlayers();
                        needsRedrawing = false;
                    }
                }

                Thread.sleep(10);
            }
        }
        catch (InterruptedException ex)
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
        Map map = GameState.current.map;

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
        HashMap<String,Player> players = GameState.current.players;

        for (Player p : players.values())
        {
            terminal.write('A', p.x, p.y);
        }
    }
}
