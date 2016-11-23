package FileIO;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by Jackie on 2016-11-23.
 */
public class ReadFile
{
    private String username;
    private final String directoryPath = "./PlayerData/";
    private final String fileExtension = ".txt";
    private File directory = new File(directoryPath);
    private File playerFile;
    private File pathOfFile;

    private int[] positions = new int[2];

    public ReadFile(String username)
    {
        this. username = username;
        String filename = username + fileExtension;
        this.playerFile = new File(directoryPath + filename);
        this.pathOfFile = playerFile.getAbsoluteFile();
    }
    /**
     * retrieve the position stored in the player's file
     * @param username
     * @return array of strings, where [0] is x position and [1] is y position
     */
    public int[] readFromDisk()
    {
        FileReader fr;
        try {
            fr = new FileReader(pathOfFile);
            BufferedReader br = new BufferedReader(fr);

            String[] tempPos = br.readLine().split(" ");

            positions[0] = Integer.parseInt(tempPos[0]);
            positions[1] = Integer.parseInt(tempPos[1]);

            fr.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return positions;
    }

    /**
     * TODO: implement such that when the primary server starts up, read all the files from the storage
     */
    public void readAllFilesFromDisk()
    {

    }
}
