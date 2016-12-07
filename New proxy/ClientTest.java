import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientTest {
	public Socket socket;
	public ObjectOutputStream output;
	public ObjectInputStream input;
	
	public ClientTest(String ip) {
		try {
			socket = new Socket(ip, 10000);
			output = new ObjectOutputStream(socket.getOutputStream());
			output.flush();
			input = new ObjectInputStream(socket.getInputStream());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run() {
		// write objects
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(true) {
					try {
						output.writeObject(new ClientTestCommand());
						Thread.sleep(1000);
					} catch (IOException e) {
						System.out.println("Disconnected");
						break;
					} catch (InterruptedException e) {
						System.out.println("Disconnected");
						break;
					}
				}
			}
		}).start();
		
		// read objects
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(true) {
					try {
						ServerTestCommand object = (ServerTestCommand)input.readObject();
						System.out.println(object.toString());
						Thread.sleep(1000);
					} catch (IOException e) {
						System.out.println("Disconnected");
						break;
					} catch (InterruptedException e) {
						System.out.println("Disconnected");
						break;
					} catch (ClassNotFoundException e) {
						System.out.println("Disconnected");
						break;
					}
				}
			}
		}).start();
	}
	
	public static void main(String[] args) {
		ClientTest ct = new ClientTest(args[0]);
		ct.run();
	}
}
