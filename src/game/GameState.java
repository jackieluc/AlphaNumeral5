package game;

import java.util.Dictionary;

/**
 * Created by Ahmed on 11/7/2016.
 */
public class GameState
{
    public static GameState current;
    // The players on the map
    public Dictionary<String,Player> players;
    // The map
    public char[][] tiles;

    private void loadMap()
    {

    }
}
