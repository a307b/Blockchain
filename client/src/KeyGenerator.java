import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Map;
// Class used to generate and save keys, in final version only private keys should be stored in
// in the program, the public keys should be retreived from the database
public class KeyGenerator {
    public void savePrivateKey(String keyFileName) throws Exception{
        // Generates keypair
        KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();

        // saves private key
        byte[] privateKey1 = keyPair.getPrivate().getEncoded();
        Path filePath = Paths.get("C:\\Client\\Client\\privateKeys\\" + keyFileName + ".dat");
        Files.write(filePath, privateKey1);
        System.out.println(privateKey1);

        // saves public key
        byte[] publicKey1 = keyPair.getPublic().getEncoded();
        Path filePath2 = Paths.get("C:\\Client\\Client\\privateKeys\\pu01.dat");
        Files.write(filePath2, publicKey1);
    }
}
