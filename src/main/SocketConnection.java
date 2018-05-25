/**
 * Socket Connection Class
 *
 * This class is used to handle connection coming from the applications used by Doctor and Patient.
 * It will receive an upcode, and use it to either store or send the journal.
 *
 * Methods Used :
 * handleConnection() -  Used to seperate the upcode and execute the upcode methods.
 * receivedUpcode0() - Used to execute upcode 0, which is used for blockchain journal request and send the back.
 * receivedUpcode1() - Used to execute upcode 1, which is used to store the journal received from the Doctor client.
 *
 */

import org.apache.commons.codec.binary.Base64;

import java.io.*;
import java.net.Socket;
import java.security.PublicKey;
import java.security.Signature;
import java.util.ArrayList;
import java.util.List;

public class SocketConnection
{
    private BufferedWriter bufferedWriter = null;
    private BufferedReader bufferedReader = null;
    private Blockchain blockchain;

    public SocketConnection(Blockchain blockchain)
    {
        this.blockchain = blockchain;
    }

    public void handleConnection(Socket socket)
    {
        System.out.println("Connection received!");
        try
        {

            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            final int opcode = bufferedReader.read();

            System.out.println("Opcode: " + opcode);

            switch (opcode)
            {
                /* Request for blockchain data. Sends back blocks from the blockchain. */
                case 0:
                    handleListJournals();
                    break;

                /* Receive journal data and add it to the blockchain */
                case 1:
                    handleAddJournal();
                    break;

                default:
                    System.out.println("Wrong opcode - Closing Connection");
                    socket.close();
                    break;
            }
        }
        catch (IOException e)
        {
            System.out.println("Unable to read packet: " + e.getMessage());
        }
    }

    private void handleListJournals() throws IOException
    {

        String publicKey = bufferedReader.readLine();

        List<BlockData> blockDataList = new ArrayList<>();
        /* If the received patient public key matches any of the blocks in the blockchain, add
         * the block to blockDataList */
        for (BlockData blockData : blockchain.getBlockDataChain())
        {
            if (blockData.getPatientPublicKey().equals(publicKey))
                blockDataList.add(blockData);
        }


        /* Sends the list of blocks that corresponds to the received public key back to the client */
        bufferedWriter.write(blockDataList.size());
        for (BlockData blockData : blockDataList)
        {
            bufferedWriter.write(blockData.getId());
            bufferedWriter.newLine();

            bufferedWriter.write(blockData.getPatientPublicKey());
            bufferedWriter.newLine();

            bufferedWriter.write(blockData.getEncryptedAesKey());
            bufferedWriter.newLine();

            bufferedWriter.write(blockData.getEncryptedData());
            bufferedWriter.newLine();
        }
        bufferedWriter.flush();
    }

    private void handleAddJournal() throws IOException
    {
        String privateKeySignedBlock = bufferedReader.readLine();
        String patientPublicKey = bufferedReader.readLine();
        String encryptedAesKeyIV = bufferedReader.readLine();
        String encryptedJournalData = bufferedReader.readLine();

        // Get the last block of the user, to be used by verifier
        String journalBlockID = "";
        for (int i = 0; i < blockchain.getBlockDataChain().size(); i++)
        {
            if (blockchain.getBlockDataChain().get(i).getPatientPublicKey().equals(patientPublicKey)) {
                journalBlockID = blockchain.getBlockDataChain().get(i).getId();
                System.out.println("The user had an previous block. ");
                System.out.println("Last block ID: " + blockchain.getBlockDataChain().get(i).getId());
            }
        }

        byte[] dataToBeVerified;

        boolean verified = false;

        try
        {
            // This part is known as "block propagation". The riddle is to find the public key belonging to the doctor. If the data cannot be verified, a block
            // will not be created
            for (PublicKey acceptedPublicKey : blockchain.loadAcceptedPublicKeys())
            {
                Signature signature = Signature.getInstance("SHA256WithRSA");
                signature.initVerify(acceptedPublicKey);

                /* If the patient had no previous blocks sign the patients public key.
                 * If the patient had previous blocks sign the public key + previous block
                 * journalBlockID. */

                dataToBeVerified = (journalBlockID + patientPublicKey).getBytes();

                signature.update(dataToBeVerified);
                verified = signature.verify(Base64.decodeBase64(privateKeySignedBlock));

                if (verified) // Correct public key was found, stop iteration
                    break;
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            bufferedWriter.write(0);
        }

        if (verified)
        {
            /* Adds an block with the received data.  */
            blockchain.addBlock(privateKeySignedBlock + ":" + patientPublicKey + ":" + encryptedAesKeyIV + ":"  + encryptedJournalData);
            System.out.println("Added new block");
        }
        else
            bufferedWriter.write(0); // Signature could not be verified, do not create block and callback to client that it failed
    }

}
