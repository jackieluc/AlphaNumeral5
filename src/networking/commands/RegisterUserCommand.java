package networking.commands;

import debug.Logger;
import game.GameRenderer;
import game.GameState;
import game.Player;
import networking.Client;
import networking.Server;
import networking.Server.ClientManager;

import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

/**
 * Created by Ahmed on 11/8/2016.
 */
public class RegisterUserCommand extends Command {
    public String username;
    private boolean registrationSuccessful;

    public RegisterUserCommand(String username) {
        this.username = username;
    }

    @Override
    public boolean valid() {
    	return registrationSuccessful;
    }
    
    @Override
    public void updateServer(Server server, ClientManager clientManager) {
        synchronized(server.inGameClients) {
            if (!server.registered(username)) {
                // add to dictionary of in-game clients
                server.inGameClients.put(username, clientManager);
                
                // set clientManager username
                clientManager.username = username;
                
                registrationSuccessful = true;

                Logger.log("New user \"" + username + "\" joined the game!");
            }
            
            // Reply to client with success or not
            clientManager.send(this);
            
            if(registrationSuccessful) {
            	// Update every other client about new player
            	updateExistingPlayers(server, username);
            	
            	// Update his client about every other player
            	getExistingPlayers(clientManager);
            }
        }
    }

    private void updateExistingPlayers(Server server, String username) {
    	Random random = new Random();
    	int x = random.nextInt((GameState.current.map.getWidth()-1)) + 1;
    	int y = random.nextInt((GameState.current.map.getHeight()-1)) + 1;
        // TODO only send to close by players
        MoveCommand moveCommand = new MoveCommand(username, x, y);
        server.sendAll(moveCommand);
    }

    private void getExistingPlayers(ClientManager clientManager) {
        for(HashMap.Entry<String,Player> p : GameState.current.players.entrySet()) {
            if(!p.getKey().equals(username)) {
                clientManager.send(new MoveCommand(p.getKey(), p.getValue().x, p.getValue().y));
            }
        }
    }

    @Override
    public void updateClient(Client client) {
        if(registrationSuccessful) {
            // Set username
            client.username = username;
            // Show the actual game window
            GameRenderer.current.setVisible(true);
            // Log
            Logger.log("Joined game with username " + username + "!");
        } else {
            // Ask for new username
            Scanner scanner = new Scanner(System.in);

            if(username != null) {
                System.out.println("User \"" + username + "\" already exists!");
            }

            System.out.print("Enter new username: ");
            username = scanner.next();

            // Try to register username on server
            client.send(this);
            
            scanner.close();
        }
    }

	//@Override
	//public void updateState() {}
}
