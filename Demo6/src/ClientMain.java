import debug.Logger;
import game.GameController;
import game.GameRenderer;
import game.GameState;
import networking.Client;

public class ClientMain {

	private static void SetupClient(String proxyIP)
    {
        Thread thread;

        // Create the renderer and run on thread
        GameRenderer renderer = new GameRenderer();
        thread = new Thread(renderer, "Renderer");
        thread.start();

        // Create client and run on thread
        Client client = new Client(proxyIP);
        thread = new Thread(client, "Client");
        thread.start();

        // Create the controller and add it to the JFrame
        GameController controller = new GameController(client);
        renderer.addController(controller);
    }
	public static void main(String[] args) {
		   // Create a new game state
		Logger.debug = true;
        GameState.getInstance();

        //args[0] = IP of the proxy
		SetupClient(args[0]);
	}

}
