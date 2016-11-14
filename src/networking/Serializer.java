package networking;

import debug.Logger;
import networking.commands.WelcomeCommand;

import java.io.ByteArrayOutputStream;
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

    //
    private static ByteArrayOutputStream bout;
    private static ObjectOutputStream oout;

    static
    {
        try
        {
            bout = new ByteArrayOutputStream();
            oout = new ObjectOutputStream(bout);

            oout.writeObject(new WelcomeCommand());
            oout.flush();
            bout.toByteArray();
        }
        catch (IOException ex)
        {
            Logger.log("Error creating static serializer!");
        }
    }

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

    /**
     * Serializes an object to a byte array
     * @param obj
     * @return
     */
    public static byte[] serialize(Object obj)
    {
        try
        {
            oout.writeObject(obj);
            oout.flush();
            return bout.toByteArray();
        }
        catch (IOException ex)
        {
            Logger.log("Error serializing with static serializer");
        }

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
