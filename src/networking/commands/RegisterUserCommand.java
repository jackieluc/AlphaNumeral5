package networking.commands;

import FileIO.ReadFile;
import debug.Logger;
import game.GameRenderer;
import game.GameState;
import game.Player;
import networking.Client;
import networking.Server;

import java.io.File;
import java.util.HashMap;
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
    public void updateServer(Server server, Server.ClientConnection clientConnection)
    {
        synchronized (server.inGameClients) {

            if (server.inGameClients.get(username) == null) {
                // add to dictionary of in-game clients
                server.inGameClients.put(username, clientConnection);
                // Set clientConnection username
                clientConnection.username = username;
                // prepare to read a file with the same name as "username"
                ReadFile playerFile = new ReadFile(username);

                // if the username exists in the "database" of players
                if (playerFile.exists()) {
                    Logger.log("Found: " + username + "... Reading data from file...");

                    // get positions from the file
                    int[] pos = playerFile.readFromDisk();
                    createPlayer(server, username, pos[0], pos[1]);
                }
                else
                {
                    // create player on the map
                    createPlayer(server, username);
                }
            }

            getExistingPlayers(clientConnection);

            Logger.log("User:  " + username + " joined the game!");
        }
    }

    /**
     * put the player on the server with the default position of (1,1)
     * @param server - the primary server
     * @param username - the username entered by the user
     * TODO: make the default position be random
     */
    private void createPlayer(Server server, String username)
    {
        // TODO only send to close by players
        MoveCommand moveCommand = new MoveCommand(username, 1, 1);
        server.sendAll(moveCommand);
    }

    /**
     * put the player on the server with a specified position of (x,y)
     * @param server - the primary server
     * @param username - the username entered by the user
     * @param x - x position of the player
     * @param y - y position of the player
     */
    public void createPlayer(Server server, String username, int x, int y)
    {
        MoveCommand moveCommand = new MoveCommand(username, x, y);
        server.sendAll(moveCommand);
    }

    private void getExistingPlayers(Server.ClientConnection clientConnection)
    {
        synchronized (GameState.getInstance()) {
            for (HashMap.Entry<String, Player> p : GameState.getInstance().getPlayers().entrySet()) {
                if (!p.getKey().equals(username)) {
                    clientConnection.send(new MoveCommand(p.getKey(), p.getValue().x, p.getValue().y));
                }
            }
        }
    }

    @Override
    public void updateClient(Client client)
    {
//        if (registrationSuccessful)
//        {
//            // Set username
//            client.username = username;
//            // Show the actual game window
//            GameRenderer.current.setVisible(true);
//            // Log
//            Logger.log("Joined game with username " + username + "!");
//        }
//        else
//        {
            // Ask for new username
            Scanner scanner = new Scanner(System.in);

            if (username != null)
                System.out.println("User \"" + username + "\" already exists!");

            System.out.print("Enter new username:");
            username = scanner.next();

            // Try to register username on server
            client.send(this);

            // Set username
            client.username = username;
            // Show the actual game window
            GameRenderer.current.setVisible(true);
            // Log
            Logger.log("Joined game with username " + username + "!");
//        }
    }
}
