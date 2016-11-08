package game;

import debug.Logger;

/**
 * Created by Ahmed on 11/7/2016.
 * Controls a game client-side and handles input
 */
public class GameController
{
    private boolean quit;
    private GameRenderer renderer;

    public GameController()
    {

    }

    private void getUsername()
    {

    }

    public void loop()
    {
        try
        {
            while (!quit)
            {
                renderer.draw();
                //control.check();
                // wait for a bit
                Thread.sleep(10);
            }
        }
        catch (Exception ex)
        {
            Logger.log("Error in game loop!");
            Logger.log(ex);
        }
    }
}
