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
import java.io.OutputStream;

public class Decompression implements IHuffConstants {
    
    private BitInputStream bitsIn;
    private BitOutputStream bitsOut;
    private int format;
    private int[] freq;
    private HuffmanTree newTree;
    private IHuffViewer myViewer;
    private int bitsWritten;
    
    /**
     * Creates a decompression object that performs all decompression steps
     * @param in - input stream of bits to read the file
     * @param out - output stream to write read bits out to new file
     * @param myViewer - Object for communication for user
     * @throws IOException
     */
    public Decompression(BitInputStream in, OutputStream out, IHuffViewer myViewer) 
            throws IOException {
        this.bitsIn = in;
        this.bitsOut = new BitOutputStream(out);
        this.myViewer = myViewer;
        // Magic Number already read in SHP
        this.format = bitsIn.readBits(BITS_PER_INT);
        if (format == STORE_COUNTS) {
            this.freq = constructFreq();
            newTree = new HuffmanTree(freq);
        }
        if (format == STORE_TREE) {
            // Reads bits for size
            bitsIn.readBits(BITS_PER_INT);
            TreeNode temp = readTree();
            newTree = new HuffmanTree(temp);
        }
        decode();
        bitsIn.close();
        bitsOut.close();
    }
    
    /**
     * Constructs a new frequency array based on values read from compressed file
     * @return an array with values and its associated frequencies
     * @throws IOException
     */
    private int[] constructFreq() throws IOException {
        int[] tempFreq = new int[ALPH_SIZE];
        for (int i = 0; i < ALPH_SIZE; i++) {
            // Bits read is the frequency
            int bitsCounted = bitsIn.readBits(BITS_PER_INT);
            tempFreq[i] = bitsCounted;
            
        }
        return tempFreq;
    }
    
    /**
     * Decodes the HuffmanTree until the PSEUDO_EOF
     * @throws IOException
     */
    private void decode() throws IOException {
        boolean notEnd = true;
        while (notEnd) {  
            TreeNode root = newTree.getRoot();  
            if (!decodeHelper(root)) {  
                notEnd = false;
            }
        }
    }
    
    /**
     * Helper method for decode that walks the tree based on the bit read
     * @param t - current node in the tree
     * @return - boolean based on if we are at the end of the file or not
     * @throws IOException
     */
    private boolean decodeHelper(TreeNode t) throws IOException {
        while (!t.isLeaf()) {
            int bit = bitsIn.readBits(1);
            if (bit == -1) {
                myViewer.showError("Error reading compressed file. \n" +
                    "Unexpected end of input. No PSEUDO_EOF value.");
                return false;
            } else if (bit == 0) {
                t = t.getLeft(); 
            } else {
                t = t.getRight(); 
            }
        }
        if (t.getValue() == PSEUDO_EOF) {
            return false;
        } else {
            bitsOut.write(t.getValue());
            bitsWritten += BITS_PER_WORD;
            return true;
        }
    }
    
    /**
     * Reads the header based on the Store Tree Format and reconstructs Huffman Tree
     * @return - a TreeNode, either references to children node for internal nodes and the root at 
     * the end of reading the tree
     * @throws IOException
     */
    private TreeNode readTree() throws IOException {
        int temp = bitsIn.readBits(1);
        if (temp == 0) {
            TreeNode internal = new TreeNode(readTree(), -1, readTree());
            return internal;
        } else if (temp == 1) {
            int value = bitsIn.readBits(BITS_PER_WORD + 1);
            TreeNode leaf = new TreeNode(value, 1);
            return leaf;
        } else {
            throw new IOException("unexpectedly reached end of file");
        }
    }
    
    /**
     * Gets the total bit written during the decompression process
     * @return - total amount of bits written to the out file
     */
    public int getBits() {
        return bitsWritten;
    }
}
