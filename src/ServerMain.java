import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import debug.Logger;
import game.GameState;
import networking.Server;

public class ServerMain {
	public static void main(String[] args) {
		int port = -1;
		
		String pattern = "\\d{1,5}";
		Pattern p = Pattern.compile(pattern);
		Matcher m;
		
		for(String arg : args) {
			if(arg.equals(Logger.flag)) {
				Logger.debug = true;
			} else {
				m = p.matcher(arg);
				if(m.find()) {
					port = Integer.parseInt(m.group(0));
					break;
				}
			}
		}
		
		if(!validPort(port)) {
			System.out.println("Invalid port");
			System.exit(0);
		}
		
		// Instantiate server
		try {
			System.out.println("Started server with address: " + InetAddress.getLocalHost().getHostAddress() + ":" + port);
		} catch (UnknownHostException e) {
			System.out.println("Error: couldn't start server.");
			System.exit(0);
		}
		
		new GameState();
		
		Server server = new Server(port);
        Thread thread = new Thread(server);
        thread.start();
	}

	private static boolean validPort(int port) {
	    if(port < 0 || port > 65535) {
	        return false;
	    }
	    return true;
	}
}
