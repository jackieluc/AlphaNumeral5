import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class ServerTest {
	public ServerSocket server;
	public Socket socket;
	public ObjectOutputStream output;
	public ObjectInputStream input;
	
	public ServerTest(String ip) {
		try {
			Socket temp = new Socket(ip, 5000);
			temp.close();
			server = new ServerSocket(4000);
			socket = server.accept();
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
						output.writeObject(new ServerTestCommand());
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
						ClientTestCommand object = (ClientTestCommand)input.readObject();
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
		ServerTest ct = new ServerTest(args[0]);
		ct.run();
	}
}