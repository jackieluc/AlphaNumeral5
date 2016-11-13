package game;

import java.util.HashMap;

/**
 * Created by Ahmed on 11/7/2016.
 */
public class GameState
{
    public static GameState current;
    // The players on the map
    public HashMap<String,Player> players;
    // The map
    public final Map map;

    public GameState()
    {
        // Set current game state
        current = this;
        // Initialize vars
        players = new HashMap<>();
        // Load map
        map = new Map();

    }
}
