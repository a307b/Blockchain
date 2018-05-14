import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Blockchain
{
    List<Block> blockChain = new ArrayList<>();
    static BufferedReader blockchainReader;
    static FileWriter blockchainWriter;
    static String blockchainFile = "blockchain";

    public static void start()
    {
        System.out.println("Begin loading the blockchain database...");

        try
        {
            blockchainReader = new BufferedReader(new FileReader(blockchainFile));

            // Begin creating the blocks and adding them
            String line;
            while ((line = blockchainReader.readLine()) != null)
            {
                List<String> splitLinesList = Arrays.asList(line.split(":"));

                if (splitLinesList.size() != 4)
                {
                    //System.out.println("");
                    //System.out.println(line);
                    throw new Exception("Failed parsing blockchain line, please revise splitting in line:\n" + line);
                }

                Block block = new Block();

                block.blockID = splitLinesList.get(0);
                block.patientPublicKey = splitLinesList.get(1);
                block.encryptedAesKey = splitLinesList.get(2);
                block.encryptedData = splitLinesList.get(3);


                System.out.println("Created block");
            }

            blockchainReader.close();
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }


        /*
        try
        {
            blockchainWriter = new FileWriter(blockchainFile,true); //the true will append the new data
            blockchainWriter.write("add a line\n");//appends the string to the file
            blockchainWriter.close();
        }
        catch(Exception e)
        {
            System.err.println("Exception: " + e.getMessage());
        }
        */
    }

    public static void addBlock(Block block)
    {

    }

}
