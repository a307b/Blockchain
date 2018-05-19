import org.junit.jupiter.api.Test;

class BlockchainTest{
    @Test
    void addBlock() {
        ExtendedBlockchain blockchain = new ExtendedBlockchain();
        blockchain.setBlockchainFile("blockChainTests\\testBlockchain");
        blockchain.addBlock("Hello world");
    }
}