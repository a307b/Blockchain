/**
 * Main Class - used ot initializeBlockchain Blockchain
 *
 * Methods Used :
 * main : psvm to execute starting the blockchain
 *
 */
import java.net.ServerSocket;
import java.net.Socket;

public class Main
{

    public static void main(String[] args)
    {
        boolean running = true;

        Blockchain blockchain = new Blockchain();

        System.out.println("Loading valid public keys...");
        System.out.println("Loaded " + blockchain.loadAcceptedPublicKeys().size() + " public keys");
        System.out.println("Begin loading the blockchain database...");

        blockchain.initializeBlockchain();

        try
        {
            SocketConnection socketConnection = new SocketConnection(blockchain);
            ServerSocket serverSocket = new ServerSocket(21149);

            while (running)
            {
                System.out.println("Awaiting connection...");
                Socket socket = serverSocket.accept();
                socket.setSoTimeout(10000); // 10 seconds timeout to read the packet from the client
                socketConnection.handleConnection(socket);
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
