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

    public boolean insideMap(int x, int y)
    {
        if (x > 0 && x < width-1)
        {
            if (y > 0 && y < height-1)
                return true;
        }

        return false;
    }

    public boolean validMove(int x, int y)
    {
        if(!insideMap(x, y))
            return false;

        if(tiles[y][x] != ' ')
            return false;

        return true;
    }
}
