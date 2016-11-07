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
	
	private void runClient() {
		try {
			Socket socket = new Socket(serverIP, serverPort);
			Logger.log("Connected to server: " + serverIP + " : " + serverPort);
			
			DataInputStream incomingData = new DataInputStream(socket.getInputStream());
			DataOutputStream outgoingData = new DataOutputStream(socket.getOutputStream());
			Scanner ui = new Scanner(System.in);
			
			String incoming = "";
			String outgoing = "";
			
			// initial "hello" to establish this client's name
			System.out.print("What is your name?: ");
			outgoing = ui.nextLine();
			outgoingData.writeUTF("hello " + outgoing);
			
			// response from server
			System.out.println(incomingData.readUTF());

			// client send requests and see responses from server until client wants to quit
			boolean quit = false;
			while(!quit) {
				
				outgoing = ui.nextLine();
				outgoingData.writeUTF(outgoing);
				if(outgoing.equals("quit")) {
					quit = true;
					Logger.log("You decided to quit.");
				}
				
				Logger.log("Request: " + outgoing);
				if(!quit)
					Logger.log("Response: " + incomingData.readUTF());
			}
			Logger.log("Cleaning up...");
			ui.close();
			socket.close();
			
		} catch (IOException IOError) {
			System.err.println("Error in connecting to the server... Please try again.");
			System.err.println(IOError.getMessage());
			System.exit(0);
		}
	} // end runClient
		
	public static void main(String[] args) {
		
		Scanner userInput = new Scanner(System.in);
		System.out.println("Enter the server IP you would like to connect to: ");
		String sip = userInput.nextLine();
		System.out.println("Enter the port: ");
		int sp = userInput.nextInt();
		
		Client client = new Client(sip, 5555);
		client.runClient();
		
		userInput.close();
	}
}
