package networking;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

import static debug.Logger.log;

public class ServerList
{
    private ArrayList<InetSocketAddress> serverAddresses;
    private int port;

    public ServerList(int port)
    {
        serverAddresses = new ArrayList<>();
        load();
        this.port = port;
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
            if ((socket = connect(address)) != null )
            {
                log("Connected to " + address);

                // If is master server, return the socket
                if (isMasterServer(socket))
                    return socket;

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
            log("Failed to connect to " + address);
            log(ex);
        }

        return null;
    }

    private boolean isMasterServer(Socket socket)
    {
        try
        {
            int serverStatus = socket.getInputStream().read();
            return (serverStatus == 1);
        }
        catch (IOException ex)
        {
            log("Error checking status of connection!");
            log(ex);
        }

        return false;
    }

    private void close(Socket socket)
    {
        try
        {
            socket.close();
        }
        catch (IOException ex)
        {
           log("Could not close socket");
           log(ex);
        }
    }
}
