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
			System.out.println("1");
			Socket temp = new Socket(ip, 5000);
			System.out.println("2");
			temp.close();
			System.out.println("3");
			server = new ServerSocket(4000);
			System.out.println("4");
			socket = server.accept();
			System.out.println("5");
			output = new ObjectOutputStream(socket.getOutputStream());
			System.out.println("6");
			output.flush();
			System.out.println("7");
			input = new ObjectInputStream(socket.getInputStream());
			System.out.println("8");
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
						ClientTestCommand object = (ClientTestCommand)input.readObject();
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
		ServerTest ct = new ServerTest(args[0]);
		ct.run();
	}
}