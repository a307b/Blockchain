import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class Main
{
    private static int port = 21149;

    public static void main(String[] args)
    {
        System.out.println("Loading valid public keys...");
        Blockchain.loadPublicKeys();
        System.out.println("Loaded " + Blockchain.getPublicKeyList().size() + " public keys");

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
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader stream = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            int opcode = stream.read();

            System.out.println("Opcode: " + opcode);

            switch (opcode)
            {
                case 0:
                    String publicKey = stream.readLine();

                    List<Block> blockList = new ArrayList<>();
                    /* If the received public key matches any of the blocks in the blockchain, add
                     * the block to blockList */

                    // Iterate over blockchain and check if received base64 public key matches any of those in the blockchain
                    // If yes, add them to a list
                    for (Block block : Blockchain.getBlockChain())
                    {
                        if (block.patientPublicKey.equals(publicKey))
                            blockList.add(block);
                    }

                    /* Sends the list of blocks that corresponds to the received public key back to the client */
                    bw.write(blockList.size());
                    for (Block block : blockList)
                    {
                        bw.write(block.id);
                        bw.newLine();

                        bw.write(block.encryptedData);
                        bw.newLine();
                    }
                    bw.flush();

                    // System.out.println("Get journals using the public key from the packet. Used by doctors");
                    break;

                case 1:
                    String signedBlock = stream.readLine();
                    String patientPublicKey = stream.readLine();
                    String encryptedAesKeyIV = stream.readLine();
                    String encryptedJournalData = stream.readLine();


                    System.out.println(signedBlock);
                    System.out.println(patientPublicKey);
                    System.out.println(encryptedAesKeyIV);
                    System.out.println(encryptedJournalData);

                    // Get the last block of the user, to be used by verifier
                    String latestPatientBlockId = "";
                    for (int i = Blockchain.getBlockChain().size() - 1; i >= 0; --i)
                    {
                        if (Blockchain.getBlockChain().get(i).patientPublicKey.equals(patientPublicKey))
                            latestPatientBlockId = Blockchain.getBlockChain().get(i).patientPublicKey;
                    }

                    boolean verified = false;
                    try
                    {
                        // This part is known as "block propagation". The riddle is to find the public key belonging to the doctor. If the data cannot be verified, a block
                        // will not be created
                        for (PublicKey doctorPublicKey : Blockchain.getPublicKeyList())
                        {
                            Signature signature = Signature.getInstance("SHA256WithRSA");
                            signature.initVerify(doctorPublicKey);

                            if (!latestPatientBlockId.equals(""))
                                signature.update((latestPatientBlockId+patientPublicKey).getBytes());
                            else
                                signature.update(patientPublicKey.getBytes());

                            verified = signature.verify(Base64.getDecoder().decode(signedBlock));

                            if (verified) // Public key was found, stop iteration
                                break;
                        }
                    }
                    catch (Exception e)
                    {
                        System.out.println(e.getMessage());
                        bw.write(0);
                    }

                    if (!verified)
                    {
                        bw.write(0);
                        return; // Signature could not be verified
                    }


                    String blockId = "";

                    try
                    {
                        MessageDigest digest = MessageDigest.getInstance("SHA-256");

                        if (latestPatientBlockId.equals(""))
                        {
                            blockId = Base64.getEncoder().encodeToString(digest.digest(patientPublicKey.getBytes()));
                        }
                        else
                        {
                            blockId = Base64.getEncoder().encodeToString(digest.digest((latestPatientBlockId+patientPublicKey).getBytes()));
                        }
                    }
                    catch (Exception e)
                    {
                        System.out.println(e.getMessage());
                        bw.write(0);
                    }


                    Block block = new Block();
                    block.id = blockId;
                    block.patientPublicKey = patientPublicKey;
                    block.encryptedAesKey = encryptedAesKeyIV;
                    block.encryptedData = encryptedJournalData;

                    Blockchain.getBlockChain().add(block);
                    System.out.println("Succesfully added block");

                    bw.write(1);
                    bw.write(blockId);
                    bw.newLine();
                    bw.flush();
                    break;

                case 2:
                    System.out.println("List latest journal. Used by the citizen");
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
