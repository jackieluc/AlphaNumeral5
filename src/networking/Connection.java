package networking;

import networking.commands.Command;

import java.io.IOException;
import java.net.Socket;

public class Connection
{
    protected Socket socket;
    protected Serializer serializer;

    public Connection(){}

    public Connection(Socket socket)
    {
        this.socket = socket;
        serializer = new Serializer(socket);
    }

    public Connection(Connection connection)
    {
        this.socket = connection.getSocket();
        this.serializer = connection.getSerializer();
    }

    public Socket getSocket()
    {
        return socket;
    }

    public Serializer getSerializer()
    {
        return serializer;
    }

    public void send(Command command)
    {
        synchronized (this)
        {
            serializer.writeToSocket(command);
        }
    }

//    public void sendSerialized(byte[] bytes) throws IOException
//    {
//        synchronized (this)
//        {
//            socket.getOutputStream().write(bytes);
//        }
//    }

    public void close() throws IOException
    {
        socket.close();

        socket = null;
        serializer = null;
    }
}
