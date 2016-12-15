package FileIO;

import game.GameState;
import game.Player;

import java.io.*;

/**
 * Created by Jackie on 2016-11-23.
 */
public class WriteFile
{
    private String username;
    private final String directoryPath = "./PlayerData/";
    private final String fileExtension = ".txt";
    private File directory = new File(directoryPath);
    private File playerFile;
    private File pathOfFile;

    public WriteFile(String username)
    {
        this. username = username;
        String filename = username + fileExtension;
        this.playerFile = new File(directoryPath + filename);
        this.pathOfFile = playerFile.getAbsoluteFile();
    }
    /**
     * create a directory called PlayerData if it doesn't exist
     * create a file for the player if it doesn't exist
     * update the player's file by storing x and y position
     */
    public void writeToDisk()
    {
        //create the "PlayerData" directory if it doesn't exist
        if(!directory.exists())
            directory.mkdir();

        //create the player's file if it doesn't exist
        if(!playerFile.exists())
        {
            try {
                playerFile.createNewFile();
            } catch (IOException e) {
                //TODO: handle exception
                e.printStackTrace();
            }
        }

        try {
            FileWriter fw = new FileWriter(pathOfFile);
            BufferedWriter bw = new BufferedWriter(fw);

            //get the player's position to write to disk
            synchronized (GameState.getInstance()) {
                Player player = GameState.getInstance().getPlayers().get(username);
                bw.write(player.x + " " + player.y);
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
