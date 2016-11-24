package networking.commands;

import FileIO.ReadFile;
import debug.Logger;
import game.GameRenderer;
import game.GameState;
import game.Player;
import networking.Client;
import networking.Server;

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
//            // Check if the username exists
//            registrationSuccessful = (server.inGameClients.get(username) == null);
//
//            // Reply to client with success or not
//            clientConnection.send(this);
//
//            // If successful, add player
//            if (registrationSuccessful)
//            {
            if (server.inGameClients.get(username) == null) {
                // add to dictionary of in-game clients
                server.inGameClients.put(username, clientConnection);
                // Set clientConnection username
                clientConnection.username = username;
                // create player on the map
                createPlayer(server, username);

                if (server.clients.contains(username)) {
                    Logger.log("Reading " + username + " from file...");
                    //get the dictionary in-game clients
                    int[] pos = new ReadFile(username).readFromDisk();

                    updateExistingPlayer(server, username, pos[0], pos[1]);
                }
            }

            getExistingPlayers(clientConnection);

                Logger.log("User:  " + username + " joined the game!");
//            }//TODO: UPDATE CLIENTCONNECTION WHEN USER DISCONNECTS OR DELETE SOCKET AND RECREATE SOCKET
        }
    }

    private void createPlayer(Server server, String username)
    {
        // TODO only send to close by players
        MoveCommand moveCommand = new MoveCommand(username, 1, 1);
        server.sendAll(moveCommand);
    }

    public void updateExistingPlayer(Server server, String username, int x, int y)
    {
        Logger.log("inside update: " + username + " pos: " + x + ", " + y);
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
