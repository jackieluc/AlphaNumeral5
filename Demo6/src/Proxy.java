

import networking.Client;
import networking.proxy.ProxyThread;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Proxy {
	private static final int proxy_client_port = 10000;
	private static final int proxy_server_port = 5000;
	private static final int server_port = 4000; // needs to be the server's serversocket static port
	private ServerSocket client_listener;
	private ServerSocket server_listener;
	private InetAddress server_address;

	public Proxy() {
		try {
			client_listener = new ServerSocket(proxy_client_port);
			server_listener = new ServerSocket(proxy_server_port);
			System.out.println("Server connection: " + InetAddress.getLocalHost().getHostAddress() + ":" + proxy_server_port);
			System.out.println("Client connection: " + InetAddress.getLocalHost().getHostAddress() + ":" + proxy_client_port);
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
				connect(client_listener.accept(), new Socket(server_address, server_port));
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
				System.out.println("Waiting for server to connect...");
				Socket server = server_listener.accept();
				server_address = server.getInetAddress();
				System.out.println("Server " + server.getInetAddress().toString() + " connected");
				break;
			} catch (IOException e) {
				System.out.println("Connection failed");
				// Wait for new server
			}
		}
	}

	private void connect(Socket client, Socket server) {
		System.out.println("Connecting client " + client.getInetAddress().toString() + " to server " + server.getInetAddress().toString());

		new Thread(new ProxyThread(client, server)).start();
		new Thread(new ProxyThread(server, client)).start();
	}

	public static void main(String[] args) {
		Proxy proxy = new Proxy();
		proxy.run();
	}
}