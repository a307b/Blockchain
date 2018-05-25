import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Signature;
import org.apache.commons.codec.binary.Base64;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {
    @Test
    void signingVerifyingTest() {
        try {
            /* Creates signature and RSA keypair */
            Signature privateSignature = Signature.getInstance("SHA256WithRSA");
            KeyPair keyPairDoctor = KeyPairGenerator.getInstance("RSA").generateKeyPair();

            /* Tells the signature i want to sign with the private key */
            privateSignature.initSign(keyPairDoctor.getPrivate());
            String latestBlockID = "123";
            byte[] data = latestBlockID.getBytes();
            /* Signs the data */
            privateSignature.update(data);
            byte[] privateKeySignedBlock = privateSignature.sign();

            /* Creates signature */
            Signature publicSignature = Signature.getInstance("SHA256WithRSA");
            /* Tells the signature i want to verify with the public key */
            publicSignature.initVerify(keyPairDoctor.getPublic());
            /* data2 is the same data as data was before being signed,
               now it is just getting signed with the public key instead */
            byte[] data2 = latestBlockID.getBytes();
            publicSignature.update(data2);
            /* Saves the result of the verification */
            boolean verified = publicSignature.verify(privateKeySignedBlock);
            assertTrue(verified);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

