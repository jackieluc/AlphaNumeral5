package networking;

import debug.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by Ahmed on 11/7/2016.
 */
public class Serializer
{
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public Serializer(Socket socket)
    {
        try
        {
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());
        }
        catch (IOException ex)
        {
            Logger.log("Error creating serializer streams!");
            Logger.log(ex);
        }
    }

    public static byte[] serialize(Object obj)
    {
        return null;
    }

    public Object readFromSocket()
    {
        try
        {
            return in.readObject();
        }
        catch (Exception ex)
        {
            Logger.log("Error reading object from socket!");
            Logger.log(ex);
        }

        return null;
    }

    public void writeToSocket(Object obj)
    {
        try
        {
            out.writeObject(obj);
        }
        catch (Exception ex)
        {
            Logger.log("Error writing object to socket!");
            Logger.log(ex);
        }
    }
}
