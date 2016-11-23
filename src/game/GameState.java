package game;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Scanner;

public class GameState
{
    private static final GameState INSTANCE = new GameState();
    // The players on the map
    private HashMap<String,Player> players;
    // The map
    private final Map map;

    public GameState()
    {
        // Initialize vars
        players = new HashMap<String, Player>();
        // Load map
        map = new Map();

    }

    //singleton
    public static GameState getInstance()
    {
        return INSTANCE;
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
