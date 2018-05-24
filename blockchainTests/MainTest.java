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
            Signature privateSignature = Signature.getInstance("SHA256WithRSA");
            KeyPair keyPairDoctor = KeyPairGenerator.getInstance("RSA").generateKeyPair();

            privateSignature.initSign(keyPairDoctor.getPrivate());
            String latestBlockID = "123";
            byte[] data = latestBlockID.getBytes();
            privateSignature.update(data);

            byte[] privateKeySignedBlock = privateSignature.sign();
            //String signedBlockAsString = Base64.encodeBase64String(signedBlock);


            //KeyPair keyPairPatient = KeyPairGenerator.getInstance("RSA").generateKeyPair();
            Signature publicSignature = Signature.getInstance("SHA256WithRSA");
            /* Tells the signature i want to verify with the public key */
            publicSignature.initVerify(keyPairDoctor.getPublic());
            /* data2 is the same data as data, now it is just getting signed with the public key instead */
            byte[] data2 = latestBlockID.getBytes();
            byte[] dataCopy = data2.clone();
            publicSignature.update(data2);
            /* Checks that the signed data is different from the unsigned. */
            assert(dataCopy != data2);
            /* Saves the result of the verification */
            boolean verified = publicSignature.verify(privateKeySignedBlock);
            assertTrue(verified);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void copyPasteSignatureExample() {
        try {
            Signature signature = Signature.getInstance("SHA256WithRSA");
            KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();

            signature.initSign(keyPair.getPrivate());

            byte[] data = "abcdefghijklmnopqrstuvxyz".getBytes("UTF-8");
            signature.update(data);

            byte[] digitalSignature = signature.sign();

            Signature signature2 = Signature.getInstance("SHA256WithRSA");
            signature2.initVerify(keyPair.getPublic());
            byte[] data2 = "abcdefghijklmnopqrstuvxyz".getBytes("UTF-8");
            signature2.update(data2);

            boolean verified = signature2.verify(digitalSignature);
            System.out.println(verified);
        }catch (Exception e) {
            e.printStackTrace();
        }

    }
}