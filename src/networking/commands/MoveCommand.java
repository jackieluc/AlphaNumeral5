package networking.commands;

import game.GameRenderer;
import game.GameState;
import game.Player;
import networking.Client;
import networking.Server;

import static debug.Logger.log;

public class MoveCommand extends Command
{
    public final String username;
    public final int x, y;

    public MoveCommand(String username, int x, int y)
    {
        this.username = username;
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean verify()
    {
        return GameState.getInstance().getMap().validMove(x, y);
    }

    @Override
    public void updateServer(Server server, Server.ClientConnection clientConnection)
    {
        clientConnection.username = this.username;
    	server.backup(this);
        server.sendAll(this);
    }

    /**
     * Update the state of the game - update player's position
     */
    @Override
    public void updateState()
    {
        synchronized (GameState.getInstance())
        {
            // Grab the player from the game state
            Player player = GameState.getInstance().getPlayers().get(username);

            // If player doesn't exist
            if (player == null)
            {
                player = new Player(username);
                GameState.getInstance().getPlayers().put(username, player);
                log("Created new player " + username);
            }

            // Update player's position
            player.x = x;
            player.y = y;
            log("Updating state: " + player.username + " is @ (" + player.x + ", " + player.y + ")");
        }
    }

    @Override
    public void updateClient(Client client)
    {
        GameRenderer.current.redraw();
    }
}
