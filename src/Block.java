import java.security.PublicKey;
import java.time.LocalDateTime;

public class Block {
    private String Hash;
    private String previousHash;
    private String timestamp;
    private String previousTimestamp;
    private String transaktionID;
    private String AESKey;
    private String jData;
    private PublicKey patientPubKey;

    public Block() {
        this.timestamp = LocalDateTime.now().toString();
    }

    public String getTimestamp() {
        return timestamp;
    }
}


