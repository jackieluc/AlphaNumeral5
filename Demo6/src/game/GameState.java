package game;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Scanner;


/**
 * Created by Ahmed on 11/7/2016.
 */
public class GameState
{
	private static GameState instance;
    public static GameState current;
    // The players on the map
    public HashMap<String,Player> players;
    // The map
    public final Map map;

    private GameState()
    {
        // Set current game state
        current = this;
        // Initialize vars
        players = new HashMap<>();
        // Load map
        map = new Map();

    }
	/**
	Get the singleton instance
	*/
  public static synchronized GameState getInstance() {
	//  System.out.println(">>> getting instance");
    if (instance == null) {
      instance = new GameState();
    }
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
