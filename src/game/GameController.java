package game;

/**
 * Created by Ahmed on 11/7/2016.
 * Controls a game client-side
 */
public class GameController
{
    private boolean quit;
    private GameRenderer renderer;

    public void loop()
    {
        while (!quit)
        {
            renderer.draw();
            //control.check();
            // wait for a bit
        }
    }
}
