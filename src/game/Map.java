package game;

import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

import static debug.Logger.log;

public class Map
{
    private int width, height;
    private char[][] tiles;

    public Map()
    {
        load();
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public char getTile(int x, int y)
    {
        return tiles[y][x];
    }

    /**
     * Loads the map from the resources folder
     */
    private void load()
    {
        try
        {
            Scanner sc = new Scanner(getClass().getClassLoader().getResource("world.map").openStream());

            width = Integer.parseInt(sc.nextLine());
            height = Integer.parseInt(sc.nextLine());

            // Initialize tiles array
            tiles = new char[height][width];

            // Read each line and write the tiles
            String line;
            for (int y = 0; y < height; y++)
            {
                line = sc.nextLine();

                for (int x = 0; x < line.length(); x++)
                    tiles[y][x] = line.charAt(x);
            }
        }
        catch (IOException ex)
        {
            log("Error loading world map!");
            log(ex);
        }
    }

    /**
     * @param x - x position of player
     * @param y - y position of player
     * @return - true if it is inside the map, false otherwise
     */
    public boolean insideMap(int x, int y)
    {
        if (x > 0 && x < width-1)
        {
            if (y > 0 && y < height-1)
                return true;
        }

        return false;
    }


    /**
     * @param x - x position of player
     * @param y - y position of player
     * @return - true if it is valid, false otherwise
     */
    public boolean validMove(int x, int y)
    {
    	HashMap<String,Player> players = GameState.getInstance().getPlayers();
        for (Player p : players.values())
        {
            // If the values are not consistent, return false
            if (p.x == x && p.y == y)
                return false;
        }

        // If it is outside of the map, return false
        if (!insideMap(x, y))
            return false;

        return true;
    }
}
