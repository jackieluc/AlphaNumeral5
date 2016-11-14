package networking.commands;

import debug.Logger;
import game.GameRenderer;
import game.GameState;
import game.Player;
import networking.Client;
import networking.Server;

/**
 * Created by Ahmed on 11/7/2016.
 */
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
        return super.verify();
    }

    @Override
    public void updateServer(Server server, Server.ClientConnection clientConnection)
    {
        Logger.log("VALID MOVE COMMAND");

        // TODO only send to close by players
        server.sendAll(this);
    }

    @Override
    public void updateState()
    {
        synchronized (GameState.current)
        {
            Player player = GameState.current.players.get(username);

            if (player == null)
            {
                player = new Player(username);
                GameState.current.players.put(username, player);
                Logger.log("Created new player " + username);
            }

            Logger.log(x +" " + y);
            player.x = x;
            player.y = y;
        }
    }

    @Override
    public void updateClient(Client client)
    {
        GameRenderer.current.redraw();
    }
}
