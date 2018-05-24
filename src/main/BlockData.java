/**
 * Block Data
 *
 * Data class for a Block - Contians the data and used getters & setters.
 *
 */

public class BlockData
{
    private final String id;
    private final String patientPublicKey;
    private final String encryptedAesKey;
    private final String encryptedData;

    public BlockData(String id, String patientPublicKey, String encryptedAesKey, String encryptedData)
    {
        this.id = id;
        this.patientPublicKey = patientPublicKey;
        this.encryptedAesKey = encryptedAesKey;
        this.encryptedData = encryptedData;
    }

    public String getId()
    {
        return id;
    }

    public String getPatientPublicKey()
    {
        return patientPublicKey;
    }

    public String getEncryptedAesKey()
    {
        return encryptedAesKey;
    }

    public String getEncryptedData()
    {
        return encryptedData;
    }
}
