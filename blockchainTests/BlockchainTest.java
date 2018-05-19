import org.junit.jupiter.api.Test;

class BlockchainTest{
    @Test
    void addBlock() {
        Blockchain blockchain = new Blockchain();
        blockchain.setBlockchainFile("blockChainTests\\testBlockchain");
        blockchain.addBlock("Hello world");
    }
}