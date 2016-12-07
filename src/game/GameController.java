package game;

import networking.Client;
import networking.commands.MoveCommand;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


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
        Player player = GameState.getInstance().getPlayers().get(client.username);

        switch (e.getKeyCode())
        {
            case KeyEvent.VK_RIGHT:
            	if (CheckValid.checkx(player.x, e))
            		client.send(new MoveCommand(client.username, player.x + 1, player.y));
                break;
                
            case KeyEvent.VK_LEFT:
            	if (CheckValid.checkx(player.x, e))
            		client.send(new MoveCommand(client.username, player.x - 1, player.y));
                 break;
                 
            case KeyEvent.VK_UP:
            	if (CheckValid.checky(player.y, e))
            		client.send(new MoveCommand(client.username, player.x, player.y-1));
                break;
                
            case KeyEvent.VK_DOWN:
            	if (CheckValid.checky(player.y, e))
            			client.send(new MoveCommand(client.username, player.x, player.y+1));
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e)
    {

    }
}
