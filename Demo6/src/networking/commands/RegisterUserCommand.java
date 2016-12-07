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

import static game.GameState.current;


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
        synchronized (server.hashLock)
        {
            // Check if the username exists
            registrationSuccessful = (server.inGameClients.get(username) == null);

            // Reply to client with success or not
            System.out.println("before sending this");
            clientConnection.send(this);
            System.out.println("after sending this");

            // If successful, add player
            if (registrationSuccessful)
            {
                // add to dictionary of in-game clients
                server.inGameClients.put(username, clientConnection);
                // Set clientConnection username
                clientConnection.username = username;

                // prepare to read a file with the same name as "username"
                ReadFile playerFile = new ReadFile(username);

                // get the player if it exists on the server
                if(GameState.getInstance().players.containsKey(username))
                {
                	updatePlayer(server, username);
                    Logger.log("Welcome back " + username + "!");
                }
                // get the player if it exists in file
                else if (playerFile.exists())
                {
                    Logger.log("Found: " + username + "... Reading data from file...");

                    // get the positions from the file and create a new player and add it back into the system
                    int[] pos = playerFile.readFromDisk();
                    Player player = new Player(username);
                    player.x = pos[0];
                    player.y = pos[1];

                    synchronized (GameState.getInstance())
                    {
                        GameState.getInstance().players.put(username, player);
                    }

                    updatePlayer(server, username);
                    Logger.log("Welcome back " + username + "!");
                }
                // create a new user
                else
                {
                    // create player on the map
                	createPlayer(server, username);
                	Logger.log("New user: " + username + ", joined the game!");
                }
                
                getExistingPlayers(clientConnection);
            }
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
     */    
    private void updatePlayer(Server server, String username) 
    {
    	synchronized (GameState.getInstance())
    	{
	    	Player player = GameState.getInstance().players.get(username);
	        MoveCommand moveCommand = new MoveCommand(username, player.x, player.y);
	        server.sendAll(moveCommand);
    	}
    }

    private void getExistingPlayers(Server.ClientConnection clientConnection)
    {
        for (HashMap.Entry<String,Player> p : GameState.getInstance().players.entrySet())
        {
            if (!p.getKey().equals(username))
            {
                clientConnection.send(new MoveCommand(p.getKey(), p.getValue().x, p.getValue().y));
            }
        }
    }

    @Override
    public void updateClient(Client client)
    {
        if (registrationSuccessful)
        {
            // Set username
            client.username = username;
            // Show the actual game window
            GameRenderer.current.setVisible(true);
            // Log
            Logger.log("Joined game with username " + username + "!");
        }
        else
        {
            // Ask for new username
            Scanner scanner = new Scanner(System.in);

//            if (username != null)
//                System.out.println("User \"" + username + "\" already exists!");

            System.out.print("Enter new username:");
            username = scanner.next();

            // Try to register username on server
            client.send(this);
        }
    }
}
