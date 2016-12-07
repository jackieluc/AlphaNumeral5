package game;

import java.util.HashMap;


public class GameState
{
	private static GameState instance = new GameState();
    // The players on the map
    private HashMap<String,Player> players;
    // The map
    private final Map map;

    private GameState()
    {
        players = new HashMap<>();
        map = new Map();
    }

    /**
    * Get the singleton instance
    */
    public static synchronized GameState getInstance() {
        return instance;
    }

    public HashMap<String,Player> getPlayers()
    {
        return players;
    }

    public Map getMap()
    {
        return map;
    }
}
