package networking.proxy;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ProxyThread extends Thread{
	private Socket sender;
	private Socket receiver;

	public ProxyThread(Socket sender, Socket receiver) {
		this.sender = sender;
		this.receiver = receiver;
	}

	@Override
	public void run() {
		try {
			String sender_address = sender.getInetAddress().toString();
			String receiver_address = receiver.getInetAddress().toString();
			ObjectOutputStream out = new ObjectOutputStream(sender.getOutputStream());
			out.flush();
			ObjectInputStream in = new ObjectInputStream(receiver.getInputStream());
			Object command;
			while(true) {
				// Get command from receiver
				command = in.readObject();
				System.out.println("sender: " + sender_address + "    receiver: " + receiver_address + "    command: " + command.toString());

				// Send command to server
				out.writeObject(command);
				out.flush();
			}
		} catch (IOException e) {
			System.err.println("Server to client failed");
		} catch (ClassNotFoundException e) {
			System.err.println("Invalid command object");
		}
		try {
			sender.close();
			receiver.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}