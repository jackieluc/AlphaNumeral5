import debug.Logger;
import game.GameState;
import networking.BackupServer;

/**
 * Created by Ahmed on 11/14/2016.
 */
public class BackupServerMain
{
    public static void main(String[] args)
    {
        new GameState();
        Logger.debug = true;
        BackupServer server = new BackupServer(Integer.parseInt(args[0]));
        Thread thread = new Thread(server);
        thread.start();
    }
}
