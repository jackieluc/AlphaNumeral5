package networking.commands;

import FileIO.ReadFile;
import FileIO.WriteFile;
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
    private final String username;
    private final int x, y;

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
//        return GameState.current.map.insideMap(x, y);
    }

    @Override
    public void updateServer(Server server, Server.ClientConnection clientConnection)
    {
        // TODO only send to close by players
        if (verify())
        {
            Logger.log("username: " + username + " @ " + x + ", " + y);

            Logger.log("Updating position on hard-disk...");
            new WriteFile(username).writeToDisk();
            server.backup(this);
            server.sendAll(this);
        }
    }

    @Override
    public void updateState()
    {
        synchronized (GameState.getInstance())
        {
            Player player = GameState.getInstance().getPlayers().get(username);

            if (player == null)
            {
                player = new Player(username);
                GameState.getInstance().getPlayers().put(username, player);
                Logger.log("Created new player " + username);
            }

            Logger.log(x +" " + y);
            player.x = x;
            player.y = y;
        }
    }

    @Override
    public void updateClient(Client client) { GameRenderer.current.redraw(); }
}
