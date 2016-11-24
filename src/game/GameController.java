package game;

import debug.Logger;
import networking.Client;
import networking.commands.Command;
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
        int x = -1;
        int y = -1;

        synchronized(GameState.getInstance()) {
            Player player = GameState.getInstance().getPlayers().get(client.username);
            x = player.x;
            y = player.y;
        }

        switch (e.getKeyCode())
        {
            case KeyEvent.VK_RIGHT:
                x++;
                break;
            case KeyEvent.VK_LEFT:
                x--;
                break;
            case KeyEvent.VK_UP:
                y--;
                break;
            case KeyEvent.VK_DOWN:
                y++;
                break;
        }

        Command command = new MoveCommand(client.username, x, y);

//        if (command.verify())
//        {
            client.send(command);
//        }
    }

    @Override
    public void keyReleased(KeyEvent e)
    {

    }
}
