import org.apache.commons.codec.binary.Base64;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.Signature;
import java.util.ArrayList;
import java.util.List;

public class Main
{
    private static int port = 21149;

    public static void main(String[] args)
    {
        System.out.println("Loading valid public keys...");
        System.out.println("Loaded " + Blockchain.loadAcceptedPublicKeys().size() + " public keys");
        System.out.println("Begin loading the blockchain database...");

        Blockchain.start();

        try
        {
            ServerSocket serverSocket = new ServerSocket(port);
            while(true)
            {
                System.out.println("Awaiting connection...");
                Socket socket = serverSocket.accept();
                socket.setSoTimeout(10000); // 10 seconds timeout to read the packet from the client
                handleConnection(socket);
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    private static void handleConnection(Socket socket)
    {
        System.out.println("Connection received!");
        try
        {
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Blockchain blockchain = new Blockchain();
            int opcode = bufferedReader.read();

            System.out.println("Opcode: " + opcode);

            switch (opcode)
            {
                /* Request for blockchain data. Sends back blocks from the blockchain. */
                case 0:
                    String publicKey = bufferedReader.readLine();

                    List<Block> blockList = new ArrayList<>();
                    /* If the received patient public key matches any of the blocks in the blockchain, add
                     * the block to blockList */
                    for (Block block : Blockchain.getBlockChain())
                    {
                        if (block.patientPublicKey.equals(publicKey))
                            blockList.add(block);
                    }

                    /* Sends the list of blocks that corresponds to the received public key back to the client */
                    bufferedWriter.write(blockList.size());
                    for (Block block : blockList)
                    {
                        bufferedWriter.write(block.id);
                        bufferedWriter.newLine();

                        bufferedWriter.write(block.patientPublicKey);
                        bufferedWriter.newLine();

                        bufferedWriter.write(block.encryptedAesKey);
                        bufferedWriter.newLine();

                        bufferedWriter.write(block.encryptedData);
                        bufferedWriter.newLine();
                    }
                    bufferedWriter.flush();
                    break;
                /* Receive journal data and add it to the blockchain */
                case 1:
                    String privateKeySignedBlock = bufferedReader.readLine();
                    String patientPublicKey = bufferedReader.readLine();
                    String encryptedAesKeyIV = bufferedReader.readLine();
                    String encryptedJournalData = bufferedReader.readLine();

                    // Get the last block of the user, to be used by verifier
                    String journalBlockID = "";
                    for (int i = 0; i < Blockchain.getBlockChain().size(); i++)
                    {
                        if (Blockchain.getBlockChain().get(i).patientPublicKey.equals(patientPublicKey)) {
                            journalBlockID = Blockchain.getBlockChain().get(i).id;
                            System.out.println("The user had an previous block. ");
                            System.out.println("Last block ID: " + Blockchain.getBlockChain().get(i).id);
                        }
                    }

                    byte[] contentToBeSigned = null;
                    boolean verified = false;
                    try
                    {
                        // This part is known as "block propagation". The riddle is to find the public key belonging to the doctor. If the data cannot be verified, a block
                        // will not be created
                        for (PublicKey acceptedPublicKey : Blockchain.loadAcceptedPublicKeys())
                        {
                            Signature signWithPublicKey = Signature.getInstance("SHA256WithRSA");
                            signWithPublicKey.initVerify(acceptedPublicKey);

                            /* If the patient had no previous blocks sign the patients public key.
                             * If the patient had previous blocks sign the public key + previous block
                              * journalBlockID. */

                            if (journalBlockID == null) {
                                contentToBeSigned = patientPublicKey.getBytes();
                            }
                            else {
                                contentToBeSigned = (journalBlockID + patientPublicKey).getBytes();
                            }

                            signWithPublicKey.update(contentToBeSigned);
                            verified = signWithPublicKey.verify(Base64.decodeBase64(privateKeySignedBlock));

                            if (verified) // Public key was found, stop iteration
                                break;
                        }
                    }
                    catch (Exception e)
                    {
                        System.out.println(e.getMessage());
                        bufferedWriter.write(0);
                    }

                    System.out.println("Verfied: " + verified);

                    if(verified) {
                        /* Adds an block with the received data.  */
                        blockchain.addBlock(privateKeySignedBlock + ":" + patientPublicKey + ":" + encryptedAesKeyIV + ":"
                                + encryptedJournalData);
                        System.out.println("Added new block");
                    } else{
                        bufferedWriter.write(0); // Signature could not be verified, so do not create block
                    }
                    break;

                default:
                    System.out.println("Wrong opcode, modded client?");
                    break;
            }
        }
        catch (IOException e)
        {
            System.out.println("Unable to read packet: " + e.getMessage());
        }
    }
}
