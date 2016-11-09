package game;

import debug.Logger;

import java.io.IOException;
import java.util.Scanner;

/**
 * Created by Ahmed on 11/8/2016.
 */
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
                {
                    tiles[y][x] = line.charAt(x);
                }
            }
        }
        catch (IOException ex)
        {
            Logger.log("Error loading world map!");
            Logger.log(ex);
        }
    }
}
