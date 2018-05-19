/* An blockchain that contains and setter method for the blockchains save location. */
public class ExtendedBlockchain extends Blockchain {
    public static void setBlockchainFile(String blockchainFile) {
        Blockchain.blockchainFile = blockchainFile;
    }
}
