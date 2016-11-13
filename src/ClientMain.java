import java.util.regex.Matcher;
import java.util.regex.Pattern;
import debug.Logger;
import game.GameController;
import game.GameRenderer;
import game.GameState;
import networking.Client;

public class ClientMain {

	public static void main(String[] args) {
		String address = null;
		
		String pattern = "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}:\\d{4,6}";
		Pattern p = Pattern.compile(pattern);
		Matcher m;
		
		// Verify args
		for(String arg : args) {
			if(arg.equals(Logger.flag)) {
				Logger.debug = true;
			} else {
				m = p.matcher(arg);
				if(m.find()) {
					address = m.group(0);
					break;
				}
			}
		}
		
		// Confirm ip and port
		if(address.equals(null)) {
			System.out.println("Invalid address format");
			System.exit(0);
		}
		
		// Instantiate client
		String ip = address.split(":")[0];
		int port = Integer.parseInt(address.split(":")[1]);
		Thread thread;
		
		new GameState();
		
		GameRenderer renderer = new GameRenderer();
        thread = new Thread(renderer, "Renderer");
        thread.start();
		
		System.out.println("Trying to connect to " + ip + ":" + port);
		Client client = new Client(ip, port);
		thread = new Thread(client, "Client");
		thread.start();
		
		long time = 1000;
		while(!client.registered) {
			try {
				Thread.sleep(time);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		GameController controller = new GameController(client);
        renderer.addController(controller);
	}

}
