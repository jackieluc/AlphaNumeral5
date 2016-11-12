package networking;

public class FrontEnd {

  ServerInfo state = ServerInfo.getInstance();
 	public FrontEnd() {
 		System.out.println ("syncing...");
 	}

	public void sync() {
		try {
			(new DeclareSelfThread()).start();
		}
		catch (Exception e) {
			System.err.println("error in sync method");
		}
	}		
		
} 

//	multicast presence to other server
class DeclareSelfThread extends Thread {

	ServerInfo state = ServerInfo.getInstance();

	public void run() {			
		String allServer = state.getAllServers();
		System.out.println("state >> " + allServer);
			
		
	}
} //  DeclareSelfThread


//check to see if primary exists
class CheckPrimaryThread extends Thread {

	ServerInfo state = ServerInfo.getInstance();
	
	public void run() {	
		String allServer = state.getAllServers();
		System.out.println("state >> " + allServer);
	}

} // CheckPrimaryThread


//declare yourself as primary if it doesnt exits
class DeclarePrimary extends Thread {

	ServerInfo state = ServerInfo.getInstance();
	
	public void run() {		
		String allServer = state.getAllServers();
		System.out.println("state >> " + allServer);
	}

} // DeclarePrimary


//ping to server it see if alive
class pingServer extends Thread {

	ServerInfo state = ServerInfo.getInstance();
	
	public void run() {		
		String allServer = state.getAllServers();
		System.out.println("state >> " + allServer);
	}

} // pingServer







