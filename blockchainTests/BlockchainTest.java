import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BlockChainTest{
    Blockchain blockchain;
    @BeforeEach
    void setUp() {
        blockchain = new Blockchain();
    }

    @Test
    void addBlock() {
        blockchain.setBlockchainFile("blockChainTests\\testBlockchain");
        blockchain.addBlock("Hello world");
    }

    @Test
    void loadAcceptedPublicKeys() {
        System.out.println(blockchain.loadAcceptedPublicKeys().size());
    }
}