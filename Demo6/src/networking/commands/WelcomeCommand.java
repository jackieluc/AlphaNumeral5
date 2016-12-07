package networking.commands;

import debug.Logger;
import networking.Client;

/**
 * Created by Ahmed on 11/7/2016.
 */
public class WelcomeCommand extends Command
{
    @Override
    public void updateClient(Client client)
    {
        Logger.log("WELCOME TO THE SERVER!");
    }
}
