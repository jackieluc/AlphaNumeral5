import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Proxy {
	private ServerSocket client_listener;
	private ServerSocket server_listener;
	private InetAddress server_address;
	private int server_port; // Needs to be the server's serversocket port

	public Proxy(int client_port, int server_port) {
		try {
			client_listener = new ServerSocket(client_port);
			server_listener = new ServerSocket(server_port);
		} catch (NumberFormatException e) {
			// TODO: handle exception, for now, exit program
			System.exit(0);
		} catch (IOException e) {
			// TODO: handle exception, for now, exit program
			System.exit(0);
		}
	}
	
	public void run() {
		// Wait for server to connect first
		waitForServer();
		while(true) {
			try {
				new ProxyThread(client_listener.accept(), new Socket(server_address, server_port)).start();
			} catch(UnknownHostException e) {
				waitForServer();
			} catch (IOException e) {
				// Continue waiting for connections
			}
		}
	}

	private void waitForServer() {
		while(true) {
			try {
				Socket server = server_listener.accept();
				server_address = server.getInetAddress();
				//server_port = server.getPort();
				break;
			} catch (IOException e) {
				// Wait for new server
			}
		}
	}
	
	public static void main(String[] args) {
		Proxy proxy = new Proxy(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
		proxy.run();
	}
}
