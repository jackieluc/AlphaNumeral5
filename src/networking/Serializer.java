package networking;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static debug.Logger.log;

public class Serializer
{
    private ObjectInputStream in;
    private ObjectOutputStream out;

    private static ByteArrayOutputStream bout;
    private static ObjectOutputStream oout;

    static
    {
        try
        {
            bout = new ByteArrayOutputStream();
            oout = new ObjectOutputStream(bout);

            oout.flush();
            bout.toByteArray();
        }
        catch (IOException ex)
        {
            log("Error creating static serializer!");
            log(ex);
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
            log("Error creating serializer streams!");
            log(ex);
        }
    }

    public Object readFromSocket()
    {
        try
        {
            return in.readObject();
        }
        catch (Exception ex)
        {
            log("Error reading object from socket!");
            log(ex);
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
            log("Error writing object to socket!");
            log(ex);
        }
    }
}
