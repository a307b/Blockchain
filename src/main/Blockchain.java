/**
 * Blockchain Class
 *
 * This class is used to handle the blockchain.
 *
 * Methods Used :
 * initializeBlockchain() -  Initializeing the blockchain
 * addBlock()- Adding a block to the blockchain file.
 * loadAcceptedPublicKeys() - Loads and verifies public keys.
 * setBlockchainFile() - Used as a setter for the blockchain file.
 *
 */

import org.apache.commons.codec.binary.Base64;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

public class Blockchain
{
    private final List<BlockData> blockDataChain = new ArrayList<>();
    private BufferedReader blockchainReader;
    private FileWriter blockchainWriter;
    private String blockchainFile = "blockchain";
    private final String acceptedPublicKeysFile = "acceptedClientsPublicKeys\\publicKeyList.txt";

    public List<BlockData> getBlockDataChain()
    {
        return blockDataChain;
    }

    public void initializeBlockchain()
    {
        // Begin loading the blockchain
        try
        {
            blockchainReader = new BufferedReader(new FileReader(blockchainFile));

            // Create the blocks and add them to the list
            for (String line = blockchainReader.readLine();
                 line != null;
                 line = blockchainReader.readLine())
            {
                List<String> splitLinesList = Arrays.asList(line.split(":"));

                if (splitLinesList.size() != 4)
                    throw new IllegalArgumentException("Failed parsing blockchain line, please revise splitting in line:\n" + line);

                BlockData blockData = new BlockData(splitLinesList.get(0), splitLinesList.get(1), splitLinesList.get(2), splitLinesList.get(3));
                blockDataChain.add(blockData);

                System.out.println("Created blockData");
            }

            blockchainReader.close();
            System.out.println("Blockchain initialized");
        } catch (IOException | IllegalArgumentException e)
        {
            e.printStackTrace();
        }
    }

    protected void addBlock(String string)
    {
        try
        {
            blockchainWriter = new FileWriter(blockchainFile, true); //the true will append the new data
            blockchainWriter.write(string);//appends the string to the file
            /* Adds an newline. System.getPoperty is used, since fileWriter does now support writeNewLine like
             * buffered writer does. */
            blockchainWriter.write(System.getProperty("line.separator"));
            blockchainWriter.close();
        } catch (Exception e)
        {
            System.err.println("Exception: " + e.getMessage());
        }
    }

    public List<PublicKey> loadAcceptedPublicKeys()
    {

        List<PublicKey> publicKeyList = new ArrayList();

        try (BufferedReader bufferedReader = new BufferedReader(Files.newBufferedReader(Paths.get(acceptedPublicKeysFile))))
        {
            for (String acceptedPublicKeyString = bufferedReader.readLine();
                 acceptedPublicKeyString != null;
                 acceptedPublicKeyString = bufferedReader.readLine())
            {
                byte[] decodedPublicKey = Base64.decodeBase64(acceptedPublicKeyString);
                PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decodedPublicKey));
                publicKeyList.add(publicKey);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return publicKeyList;
    }

    public void setBlockchainFile(String blockchainFile)
    {
        this.blockchainFile = blockchainFile;
    }
}
