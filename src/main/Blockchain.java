import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

class Blockchain
{
    private static List<Block> blockChain = new ArrayList<>();
    private static BufferedReader blockchainReader;
    private static FileWriter blockchainWriter;
    private static String blockchainFile = "blockchain";
    private static List<PublicKey> publicKeyList = new ArrayList<>();

    public static List<PublicKey> getPublicKeyList()
    {
        return publicKeyList;
    }

    public static List<Block> getBlockChain()
    {
        return blockChain;
    }

    static void start()
    {
        // Begin loading the blockchain
        try
        {
            blockchainReader = new BufferedReader(new FileReader(blockchainFile));

            // Create the blocks and add them to the list
            String line;
            while ((line = blockchainReader.readLine()) != null)
            {
                List<String> splitLinesList = Arrays.asList(line.split(":"));

                if (splitLinesList.size() != 4)
                    throw new Exception("Failed parsing blockchain line, please revise splitting in line:\n" + line);

                Block block = new Block();
                block.id = splitLinesList.get(0);
                block.publicKey = splitLinesList.get(1);
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

    public static void loadPublicKeys()
    {
        try
        {
            Scanner scan = new Scanner(new File("publickeys"));

            while (scan.hasNextLine())
            {
                String pem = scan.nextLine();

                X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(pem)); // Decode Base64 and create X509EncodedKeySpec
                KeyFactory keyFactory = KeyFactory.getInstance("RSA"); // Create KeyFactory object to generate RSA PublicKey object
                PublicKey publicKey = keyFactory.generatePublic(pubKeySpec);

                publicKeyList.add(publicKey); // Add the PublicKey to the ArrayList
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
}
