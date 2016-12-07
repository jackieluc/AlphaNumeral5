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
			System.out.println("1");
			socket = new Socket(ip, 10000);
			System.out.println("2");
			output = new ObjectOutputStream(socket.getOutputStream());
			System.out.println("3");
			output.flush();
			System.out.println("4");
			input = new ObjectInputStream(socket.getInputStream());
			System.out.println("5");
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
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
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
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
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
