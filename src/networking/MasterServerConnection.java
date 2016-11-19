package networking;

import debug.Logger;
import networking.commands.Command;
import networking.commands.RegisterBackupServerCommand;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static debug.Logger.log;

/**
 * Created by Ahmed on 11/13/2016.
 */

    

    public class MasterServerConnection extends Connection implements Runnable
    {
    	private static boolean isRunning;
        public MasterServerConnection(Socket socket)
        {
            super(socket);
            // Ask server to send backups
            send(new RegisterBackupServerCommand());
        }

        @Override
        public void run()
        {
            Command command;

            // Wait for commands from client
            while ((command = (Command) serializer.readFromSocket()) != null)
            {
                //
                log("Command recieved from Master Server of type " + command);
                //
                command.updateState();
            }
            System.err.println("server crahsed");

            // close everything
            try
            {
            	System.err.println("closing");
                isRunning = false;
                close();
            }
            catch (IOException ex)
            {
                Logger.log("Error closing master server connection");
            }
   
        }
    }
        
       
   

  


