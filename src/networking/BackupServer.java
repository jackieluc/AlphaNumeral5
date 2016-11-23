package networking;

import debug.Logger;
import game.GameState;
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
public class BackupServer implements Runnable
{
    private static boolean isRunning;

    private class MasterServerConnection extends Connection implements Runnable
    {
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

            // close everything
            try
            {
                isRunning = false;
                close();
            }
            catch (IOException ex)
            {
                Logger.log("Error closing master server connection");
            }
        }
    }


    private int port;
    //


    ServerSocket serverSocket;
    Serializer serializer;

    public BackupServer(int port)
    {
        this.port = port;
    }

    /**
     * Creates a server socket
     * @return
     */
    private ServerSocket createServerSocket()
    {
        try
        {
            return new ServerSocket(port);
        }
        catch (Exception ex)
        {
            log("Error creating server socket!");
            log(ex);
        }

        return null;
    }

    @Override
    public void run()
    {
        //
        serverSocket = createServerSocket();
        // Connect to master server
        connectToMaster();

        try
        {
            if (serverSocket != null)
            {
                isRunning = true;

                while (isRunning)
                {
                    log("Waiting for connection...");

                    // Open connection to client
                    Socket clientSocket = serverSocket.accept();
                    // Tell client we are a backup
                    sendBackupSignal(clientSocket);
                    // Nothing else we can do for client, so close connection
                    clientSocket.close();
                }
            }
        }
        catch (Exception ex)
        {
            log("Error in server loop!");
            log(ex);
        }

        close();

        Thread thread = new Thread(new Server(port));
        thread.start();
    } // run

    /**
     * Connect to the master server
     */
    void connectToMaster()
    {
        ServerList serverList = new ServerList();
        Socket socket = serverList.getConnectionToMasterServer();

        if (socket != null)
        {
            MasterServerConnection connection = new MasterServerConnection(socket);
            Thread thread = new Thread(connection);
            thread.start();
        }
    }

    // Sends a 0 to connected clients to signify that this is a backup
    void sendBackupSignal(Socket clientSocket)
    {
        try
        {
            clientSocket.getOutputStream().write(0);
        }
        catch (IOException ex)
        {
            log("Error sending Master signal");
        }
    }

    /**
     * Closes the socket and the gamerenderer
     */
    private void close()
    {
        try
        {
            if (serverSocket!= null) serverSocket.close();
        }
        catch (Exception ex)
        {
            log("Error closing backup server socket!");
            log(ex);
        }
    }
}
