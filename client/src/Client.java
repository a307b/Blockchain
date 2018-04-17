import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.json.simple.JSONObject;

import javax.crypto.Cipher;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class Client extends Application {
    private OutputStream os;

    public void connect() {
        try {
            Socket socket = new Socket("127.0.0.1", 8000);
            os = socket.getOutputStream();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Oh no! Could not connect");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    public void sendCryptedMessage(PublicKey publicKey) throws Exception, IOException
    {
        // Creates cipher object and initializes cipher to encrypt
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        // Creates JSON object to test
        JSONObject patientData = new JSONObject();
        patientData.put("hospitalname", "Randers Sygehus");

        // Gets bytes of JSON object and encrypts it with the cipher
        byte[] patientDataBytes = patientData.toString().getBytes("UTF8");
        byte[] dataEncrypted = cipher.doFinal(patientDataBytes);

        // Writes the ciphered bytes to the server
        DataOutputStream dOut = new DataOutputStream(os);
        dOut.writeInt(dataEncrypted.length);
        dOut.write(dataEncrypted);
    }

    public static void main(String[] args){
        launch(args);
    }
    // GUI´en kræver at man klikker på connect og så send før det virker, selvom man har forbundet før
    @Override
    public void start(Stage primaryStage) throws Exception{
        // Backend stuff
        Path storedPublicKeyDES = Paths.get("C:\\Client\\Client\\privateKeys\\pu01.dat");
        byte[] publicKeyBytes =  Files.readAllBytes(storedPublicKeyDES);
        PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(publicKeyBytes));

        // Activate this code if you want to create and save new private key
        //KeyGenerator keys = new KeyGenerator();
        //keys.savePrivateKey("pr01");

        VBox vbox = new VBox();
        Scene scene = new Scene(vbox);

        Button connectButton = new Button("Connect");
        connectButton.setOnMouseClicked(e -> connect());

        Button sendButton = new Button("Send Ciphered Message");
        sendButton.setOnMouseClicked(e -> {
            try {
                sendCryptedMessage(publicKey);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });

        vbox.getChildren().add(connectButton);
        vbox.getChildren().add(sendButton);


        primaryStage.setTitle("Client");

        primaryStage.setScene(scene);
        primaryStage.show();
        /*  Old GUI code
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("JournalMaker.fxml"));
        primaryStage.setTitle("UserSearch");
        primaryStage.setScene(new Scene(root, 840, 580));
        primaryStage.show();
        */
    }
}
