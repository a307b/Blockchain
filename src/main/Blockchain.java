import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Blockchain
{
    private static List<Block> blockChain = new ArrayList<>();
    private static BufferedReader blockchainReader;
    private static FileWriter blockchainWriter;
    private static String blockchainFile = "blockchain";

    public static List<Block> getBlockChain()
    {
        return blockChain;
    }

    static void start()
    {
        System.out.println("Begin loading the blockchain database...");

        // Begin loading the blockchain
        try
        {
            blockchainReader = new BufferedReader(new FileReader(blockchainFile));

            /* Reads the blockchain file. Each line is read as block with different properties divided by semi colon.
               After an blocks properties has been identified it is added to the blockchain. */

            // Create the blocks and add them to the list
            String line;
            while ((line = blockchainReader.readLine()) != null)
            {
                List<String> splitLinesList = Arrays.asList(line.split(":"));

                if (splitLinesList.size() != 4)
                    throw new Exception("Failed parsing blockchain line, please revise splitting in line:\n" + line);

                Block block = new Block();
                block.id = splitLinesList.get(0);
                block.patientPublicKey = splitLinesList.get(1);
                block.encryptedAesKey = splitLinesList.get(2);
                block.encryptedData = splitLinesList.get(3);

                blockChain.add(block);

                System.out.println("Created block");
            }

            blockchainReader.close();
            System.out.println("Blockchain initialized");
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    static void addBlock(String string)
    {
        try
        {
            blockchainWriter = new FileWriter(blockchainFile,true); //the true will append the new data
            blockchainWriter.write(string + "\n");//appends the string to the file
            blockchainWriter.close();
        }
        catch(Exception e)
        {
            System.err.println("Exception: " + e.getMessage());
        }
    }

    public static void setBlockchainFile(String blockchainFile) {
        Blockchain.blockchainFile = blockchainFile;
    }
}
