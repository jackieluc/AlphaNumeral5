package networking;

import debug.Logger;
import sun.rmi.runtime.Log;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Ahmed on 11/14/2016.
 */
public class ServerList
{
    private ArrayList<InetSocketAddress> serverAddresses;

    public ServerList()
    {
        serverAddresses = new ArrayList<>();
        load();
    }

    private void load()
    {
        try
        {
            Scanner sc = new Scanner(getClass().getClassLoader().getResource("serverlist.txt").openStream());

            String line;
            while (sc.hasNext())
            {
                line = sc.next();

                System.out.println(line);
                String ip[] = line.split(":");
                serverAddresses.add(new InetSocketAddress(ip[0], Integer.parseInt(ip[1])));
            }

        }
        catch (IOException ex)
        {

        }
    }

    public Socket getConnectionToMasterServer()
    {
        Socket socket;

        for (InetSocketAddress address : serverAddresses)
        {
            Logger.log("Trying to connect to " + address);

            if ((socket = connect(address)) != null)
            {
                Logger.log("Connected to " + address);

                // If is master server, return socket
                if (isMasterServer(socket))
                {
                    Logger.log(address + " is master server");
                    return socket;
                }

                close(socket);
            }
        }

        // No connection found
        return null;
    }

    private Socket connect(InetSocketAddress address)
    {
        try
        {
            Socket socket = new Socket();
            socket.connect(address);
            return socket;
        }
        catch (IOException ex)
        {
            Logger.log("Failed to connect to " + address);
        }

        return null;
    }

    private boolean isMasterServer(Socket socket)
    {
        try
        {
            int serverStatus = socket.getInputStream().read();
            Logger.log("got " + serverStatus);
            return (serverStatus == 1);
        }
        catch (IOException ex)
        {
            Logger.log("Error checking status of connection!");
        }

        return false;
    }

    private void close(Socket socket)
    {
        try
        {
            socket.close();
        }
        catch (IOException e)
        {
           Logger.log("Could not close socket");
        }
    }
}