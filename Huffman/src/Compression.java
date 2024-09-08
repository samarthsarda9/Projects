/*  Student information for assignment:
 *
 *  On our honor, Samarth Sarda and Nihal Neerukonda, this programming assignment is our own work
 *  and we have not provided this code to any other student.
 *
 *  Number of slip days used: 2
 *
 *  Student 1 (Student whose Canvas account is being used): Samarth Sarda
 *  UTEID: ss224784
 *  email address: samarthsarda@utexas.edu
 *  Grader name: Namish
 *
 *  Student 2: Nihal Neerukonda
 *  UTEID: nn8337
 *  email address: nihal.neerukonda@utexas.edu
 *
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public class Compression implements IHuffConstants {
    
    private Map<Integer, String> huffMap;
    private BitInputStream bitsIn;
    private BitOutputStream bitsOut;
    private int bitsWritten;
    private int[] freq;
    
    /**
     * Creates a compression object that executes compression steps
     * @param in - input stream for file to be read
     * @param out - output stream where the compressed bits will be written
     * @param huffMap - Huffman map created with the encoding values
     * @param format - int representing if format is SCF or STF
     * @param freq - frequency array based on precompress
     * @param tree - Huffman tree created in precomprerss
     * @throws IOException
     */
    public Compression(InputStream in, OutputStream out, Map<Integer, String> huffMap, 
            int format, int[] freq, HuffmanTree tree) throws IOException {
        this.huffMap = huffMap;
        this.freq = freq;
        this.bitsIn = new BitInputStream(in);
        this.bitsOut = new BitOutputStream(out);
        bitsOut.writeBits(BITS_PER_INT, MAGIC_NUMBER);
        bitsOut.writeBits(BITS_PER_INT, format);
        bitsWritten += BITS_PER_INT * 2;
        if (format == STORE_COUNTS) {
            writeStoreCounts();
        } 
        if (format == STORE_TREE) {
            // Accounts for size of tree
            int temp = tree.leafNodes() * (BITS_PER_WORD + 1) + tree.size();
            bitsOut.writeBits(BITS_PER_INT, temp);
            bitsWritten += BITS_PER_INT;
            writeStoreTree(tree.getRoot());
        }
        writeContent();
        bitsIn.close();
        bitsOut.close();
    }
    
    /**
     * Gets the total amount of bits written to the new file
     * @return int representing the bits written out to new file
     */
    public int getBits() {
        return bitsWritten;
    }
    
    /**
     * Writes the header for SCF and adds to total counts of bits written
     */
    private void writeStoreCounts() {
        for (int i = 0; i < freq.length; i++) {
            bitsOut.writeBits(BITS_PER_INT, freq[i]); 
        }
        bitsWritten += (BITS_PER_INT * ALPH_SIZE);
    }
    
    /**
     * Recursive method that writes the header for STF and adds to bits written
     * @param t - current TreeNode in the Huffman tree
     */
    private void writeStoreTree(TreeNode t) {
        if (t != null) {
            if (!t.isLeaf()) {
                bitsOut.writeBits(1, 0);
            } else if (t.isLeaf()) {
                bitsOut.writeBits(1, 1);
                bitsOut.writeBits(BITS_PER_WORD + 1, t.getValue());
                bitsWritten += BITS_PER_WORD + 1;
            }
            bitsWritten += 1;
            writeStoreTree(t.getLeft());
            writeStoreTree(t.getRight());
        }
    }
    
    /**
     * Writes out the actual compressed content out into new file
     * @throws IOException
     */
    private void writeContent() throws IOException {
        int bitsCounted = bitsIn.readBits(BITS_PER_WORD);
        // Reads until the end of the BitInputStream
        while (bitsCounted != -1) {
            int temp = huffMap.get(bitsCounted).length();
            bitsWritten += temp;
            // Writes out each bit one-by-one
            for (int i = 0; i < temp; i++) {
                bitsOut.writeBits(1, (int) huffMap.get(bitsCounted).charAt(i));
            }
            bitsCounted = bitsIn.readBits(BITS_PER_WORD);
        }
        int temp = huffMap.get(PSEUDO_EOF).length();
        bitsWritten += temp;
        for (int i = 0; i < temp; i++) {
            bitsOut.writeBits(1, (int) huffMap.get(PSEUDO_EOF).charAt(i));
        }
    }
}
