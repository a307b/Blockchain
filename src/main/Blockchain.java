import org.apache.commons.codec.binary.Base64;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import java.util.stream.Collectors;

class Blockchain
{
    private static List<Block> blockChain = new ArrayList<>();
    private static BufferedReader blockchainReader;
    private static FileWriter blockchainWriter;
    private static String blockchainFile = "blockchain";
    private static String acceptedPublicKeysFile = "acceptedClientsPublicKeys\\publicKeyList.txt";

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
            blockchainWriter.write(string);//appends the string to the file
            /* Adds an newline. System.getPoperty is used, since fileWriter does now support writeNewLine like
            * buffered writer does. */
            blockchainWriter.write(System.getProperty( "line.separator" ));
            blockchainWriter.close();
        }
        catch(Exception e)
        {
            System.err.println("Exception: " + e.getMessage());
        }
    }

    static List<PublicKey> loadAcceptedPublicKeys() {
        List<PublicKey> publicKeyList = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(Files.newBufferedReader(Paths.get(acceptedPublicKeysFile)))) {
           String acceptedPublicKeyString;
           while ((acceptedPublicKeyString = bufferedReader.readLine()) != null)
            {
                byte[] decodedPublicKey = Base64.decodeBase64(acceptedPublicKeyString);
                PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decodedPublicKey));
                publicKeyList.add(publicKey);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return publicKeyList;
    }

    public static void setBlockchainFile(String blockchainFile) {
        Blockchain.blockchainFile = blockchainFile;
    }
}
