import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Main
{
    private static int port = 21149;

    public static void main(String[] args)
    {
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

                    for (Block block : Blockchain.getBlockChain())
                    {
                        if (block.patientPublicKey.equals(publicKey))
                            blockList.add(block);
                    }

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


                    // System.out.println("Create new block using written journal data. Used by doctors");
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
