package networking.commands;

import debug.Logger;
import networking.Client;
import networking.Server;

import java.util.Scanner;

/**
 * Created by Ahmed on 11/8/2016.
 */
public class RegisterUserCommand extends Command
{
    public String username;
    private boolean registrationSuccessful;

    public RegisterUserCommand(String username)
    {
        this.username = username;
    }

    @Override
    public void updateServer(Server server, Server.ClientManager clientManager)
    {
        synchronized (server.inGameClients)
        {
            // Check if the username exists
            registrationSuccessful = (server.inGameClients.get(username) == null);

            // If not then add
            if (registrationSuccessful)
            {
                server.inGameClients.put(username,clientManager);
                Logger.log("New user " + username + " joined the game!");
            }

            // Reply to client
            clientManager.send(this);
        }
    }

    @Override
    public void updateClient(Client client)
    {
        if (registrationSuccessful)
        {
            client.username = username;
            Logger.log("Joined game with username " + username + "!");
        }
        else
        {
            // Ask for new username
            Scanner scanner = new Scanner(System.in);

            if (username != null)
                System.out.println("User \"" + username + "\" already exists!");

            System.out.print("Enter new username:");
            username = scanner.next();

            // Try to register username on server
            client.send(this);
        }
    }
}
