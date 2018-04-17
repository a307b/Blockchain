import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.crypto.Cipher;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;


public class Server {
    public static void main(String[] args) throws Exception {
        // Reads private key from .dat file
        Path storedPrivateKeyDES = Paths.get("C:\\Client\\Client\\privateKeys\\pr01.dat");
        byte[] privateKeyBytes =  Files.readAllBytes(storedPrivateKeyDES);
        PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));


        try {
            ServerSocket serverSocket = new ServerSocket(8000);
            System.out.println("Server ready");
            while(true) {
                Socket socket = serverSocket.accept();
                listen(privateKey, socket);
            }
        } catch (IOException e) {
            System.out.println("Could not bind to port: " + e.getMessage());
        }
    }

    protected static void listen(PrivateKey privateKey, Socket socket) throws Exception {
        try (DataInputStream dIn = new DataInputStream(socket.getInputStream())) {
            String clientAddress = socket.getInetAddress().getHostAddress();
            System.out.println("\r\nNew connection from " + clientAddress);

            // Creates RSA cipher and activates Decrypt mode with the private key
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            // read length of and the incoming message
            int length = dIn.readInt();
            if (length > 0) {
                byte[] data = new byte[length];
                dIn.readFully(data, 0, data.length);   // reads the data
                // Decrypts the bytes and parses the bytes to an JSON object
                System.out.println("Client connected, decrypts message.");
                byte[] decryptedData = cipher.doFinal(data);

                JSONParser parser = new JSONParser();
                JSONObject returnedJsonObj = (JSONObject) parser.parse(new String(decryptedData));
                System.out.println("Decrypted message :" + returnedJsonObj);
            }
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}