import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
	
	private String serverIP;
	private int serverPort;
	
	public Client(String s, int p) {
		this.serverIP = s;
		this.serverPort = p;
		Logger.debug = true;
	}
	
	public void signup(String u, String p) {
		
	}
	
	private void runClient() {
		try {
			Socket socket = new Socket(serverIP, serverPort);
			
			DataInputStream incomingData = new DataInputStream(socket.getInputStream());
			DataOutputStream outgoingData = new DataOutputStream(socket.getOutputStream());
			
			Logger.log("Connected to : " + serverIP + ":" + serverPort);
			Scanner ui = new Scanner(System.in);
			
			boolean quit = false;
			while(!quit) {
				String data = ui.nextLine();
				outgoingData.writeUTF(data);
				if(data.equals("quit")) {
					quit = true;
				}
			}
			
		} catch (IOException IOError) {
			System.err.println("Error in connecting to the server... Please try again.");
			System.err.println(IOError.getMessage());
			System.exit(0);
//			IOError.printStackTrace();
		}
	} // end runClient
		
	public static void main(String[] args) {
		
		Scanner userInput = new Scanner(System.in);
		System.out.println("Enter the server IP you would like to connect to: ");
		String sip = userInput.nextLine();
		System.out.println("Enter the port: ");
		int sp = userInput.nextInt();
		
		Client client = new Client(sip, sp);
		client.runClient();
	}
}
