import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.*;


public class Server implements Runnable {
	
	private int port = -1;	
	private boolean stopServer = false;
	
	public void stop() {	
		stopServer	= true; 
	}
	
	public Server(int port) {
		this.port = port;
	}

	@Override
	public void run() {
	    // Create code that initializes thread pool, start server, run server and have
		// each connect request passed to a thread in the tread pool.
		// The server should accept connections in a loop, until the instance variable
		// stopServer is set to true.  When the loop ends, clean up server
		// and thread pool.
    	
    	ServerSocket serverSocket = null;
    	
    	try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			System.err.println( "Error opening server socket: " + e.getMessage() );
			e.printStackTrace();
			System.exit(1);
		} // try catch
  
		ExecutorService threadPool = Executors.newCachedThreadPool();
		
		while(!stopServer) {
			try {
				threadPool.execute(new RequestHandler(this, serverSocket.accept()));
			} catch (IOException e) {
				System.err.println( "Error accepting connection from socket: " + e.getMessage() );
				e.printStackTrace();
				System.exit(2);
			} // try catch	
		} // end while
		
		try {
			
			serverSocket.close();
			threadPool.shutdown();
			
		} catch (IOException e) {
			System.err.println( "Error in closing server socket: " + e.getMessage() );
			e.printStackTrace();
			System.exit(3);
		} // try catch
    } // run
} // Server
