package game;

import debug.Logger;
import networking.Client;
import networking.commands.MoveCommand;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Created by Ahmed on 11/7/2016.
 * Controls a game client-side and handles input
 */
public class GameController implements KeyListener
{
    private Client client;

    public GameController(Client client)
    {
        this.client = client;
    }

    @Override
    public void keyTyped(KeyEvent e)
    {

    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        Player player = GameState.current.players.get(client.username);

        switch (e.getKeyCode())
        {
            case KeyEvent.VK_RIGHT:
            
            	
                client.send(new MoveCommand(client.username, player.x + 1, player.y));
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e)
    {

    }
}
