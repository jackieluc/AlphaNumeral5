import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class GroupCommunicator implements Runnable {
	public String ip;
	public Socket socket;
	public ObjectInputStream input;
	public ObjectOutputStream output;
	public GroupManager gm;
	public boolean connected = false;
	public boolean isRunning = false;
	
	public GroupCommunicator(String ip) {
		this.ip = ip;
		gm = GroupManager.getInstance();
	}
	
	public boolean isAlive() {
		return connected;
	}
	
	@Override
	public void run() {
		isRunning = true;
		while(true) {
			// Keep connecting to another group manager until connection is established
			if(socket == null) {
				while(!connectTo()) {
					disconnect();
				}
			// Try and establish connection from another group communicator 
			} else {
				if(!connectFrom()) {
					disconnect();
					return;
				}
				// Send the ip of the connected group manager to every other group manager
				gm.multicast(new ConnectCommand(ip));
			}
			
			// Keep reading in objects from the other group manager
			Object command = null;
			while(true) {
				try {
					command = input.readObject();
					if(command instanceof GroupCommand) {
						((GroupCommand)command).read();
					}
				} catch (ClassNotFoundException e) {
					// Keep reading
				} catch(EOFException e) {
					// Error with other group communicator, disconnect
					disconnect();
					return;
				} catch (IOException e) {
					// Error with this group communicator, reconnect
					disconnect();
					break;
				}
			}
		}
	}
	
	public boolean connectTo() {
		try {
			this.socket = new Socket(ip, GroupManager.port);
			createStreams();
			System.out.println("Connected to " + socket.toString());
			connected = true;
			return true;
		} catch (IOException e) {
			// Failed to establish connection
			return false;
		}
	}
	
	private boolean connectFrom() {
		try {
			createStreams();
			System.out.println("Connected to " + socket.toString());
			connected = true;
			return true;
		} catch (IOException e) {
			// Failed to establish connection
			return false;
		}
	}
	
	private void createStreams() throws IOException {
		output = new ObjectOutputStream(socket.getOutputStream());
		output.flush();
		input = new ObjectInputStream(socket.getInputStream());
	}
	
	public void write(Object command) {
		if(!connected) {
			return;
		}
		try {
			output.writeObject(command);
		} catch (IOException e) {
			// Error, continue
		}
	}
	
	public Object read() throws IOException, ClassNotFoundException {
		return input.readObject();
	}

	public void disconnect() {
		try {
			if(socket != null) {
				socket.close();
			}
		} catch (IOException e) {
			// Error closing socket, continue
		}
		isRunning = false;
		connected = false;
		input = null;
		output = null;
		socket = null;
	}
}
