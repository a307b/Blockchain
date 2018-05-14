import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Main
{
    private static int port = 21149;

    public static void main(String[] args)
    {
        Blockchain.start();


        try
        {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Blockchain initialized");
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
        try (BufferedReader stream = new BufferedReader(new InputStreamReader(socket.getInputStream())))
        {
            int opcode = stream.read();

            System.out.println("Opcode: " + opcode);

            switch (opcode)
            {
                case 1:
                    System.out.println("Get journals using the public key from the packet. Used by doctors");
                    break;

                case 2:
                    System.out.println("Create new block using written journal data. Used by doctors");
                    break;

                case 3:
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
