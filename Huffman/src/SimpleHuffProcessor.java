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

public class SimpleHuffProcessor implements IHuffProcessor {

    private IHuffViewer myViewer;
    private int[] freq;
    private Map<Integer, String> huffMap;
    private int originalBits;
    private int newBits;
    private HuffmanTree tree;
    private int header;
    private boolean preProcessed;

    /**
     * Preprocess data so that compression is possible ---
     * count characters/create tree/store state so that
     * a subsequent call to compress will work. The InputStream
     * is <em>not</em> a BitInputStream, so wrap it int one as needed.
     * @param in is the stream which could be subsequently compressed
     * @param headerFormat a constant from IHuffProcessor that determines what kind of
     * header to use, standard count format, standard tree format, or
     * possibly some format added in the future.
     * @return number of bits saved by compression or some other measure
     * Note, to determine the number of
     * bits saved, the number of bits written includes
     * ALL bits that will be written including the
     * magic number, the header format number, the header to
     * reproduce the tree, AND the actual data.
     * @throws IOException if an error occurs while reading from the input file.
     */
    public int preprocessCompress(InputStream in, int headerFormat) throws IOException {
        this.freq = getFreq(in);
        this.tree = new HuffmanTree(freq);
        this.huffMap = tree.huffMap();
        this.newBits = BITS_PER_INT * 2;
        this.originalBits = ogBits();
        this.header = headerFormat;
        if (header == STORE_COUNTS) {
            this.newBits += (BITS_PER_INT * ALPH_SIZE) + compressedBits();
        }
        if (header == STORE_TREE) {
            this.newBits += BITS_PER_INT;
            this.newBits += (tree.leafNodes() * (BITS_PER_WORD + 1)) + tree.size() + 
                    compressedBits();
        }
        this.preProcessed = true;
        return originalBits - newBits;
    }
    
    /**
     * Creates a frequency array based on input file
     * @param in - given input stream file to be read
     * @return int array of size ALPH_SIZE with values and its frequency
     * @throws IOException
     */
    private int[] getFreq(InputStream in) throws IOException {
        BitInputStream inbits = new BitInputStream(in);
        int[] tempFreq = new int[ALPH_SIZE];
        int bitsCounted = inbits.readBits(BITS_PER_WORD);
        while (bitsCounted != -1) {
            tempFreq[bitsCounted]++;
            bitsCounted = inbits.readBits(BITS_PER_WORD);
        }
        inbits.close();
        return tempFreq;
    }
    
    /**
     * Gets the total amount of compressed bits based on the map created by the Huffman tree
     * @return int representing the total amount of bits based on map
     */
    private int compressedBits() {
        int sum = 0;
        for (int i = 0; i < freq.length; i++) {
            if (freq[i] != 0) {
                sum += huffMap.get(i).length() * freq[i];
            }
        }
        return sum + huffMap.get(PSEUDO_EOF).length();
    }
    
    /**
     * Calculates the amount of bits in the original file
     * @return int representing the bits in the original file
     */
    private int ogBits() {
        int sum = 0;
        for (int i = 0; i < freq.length; i++) {
            if (freq[i] != 0) {
                sum += (freq[i] * BITS_PER_WORD);
            }
        }
        return sum;
    }

    /**
	     * Compresses input to output, where the same InputStream has
     * previously been pre-processed via <code>preprocessCompress</code>
     * storing state used by this call.
     * <br> pre: <code>preprocessCompress</code> must be called before this method
     * @param in is the stream being compressed (NOT a BitInputStream)
     * @param out is bound to a file/stream to which bits are written
     * for the compressed file (not a BitOutputStream)
     * @param force if this is true create the output file even if it is larger than the input file.
     * If this is false do not create the output file if it is larger than the input file.
     * @return the number of bits written.
     * @throws IOException if an error occurs while reading from the input file or
     * writing to the output file.
     */
    public int compress(InputStream in, OutputStream out, boolean force) throws IOException {
        if (!preProcessed) {
            throw new IOException("compress is not implemented");
        }
        if ((newBits > originalBits) && !force) {
            myViewer.showError("Compressed file has more bits than the original file. "
                    + "Select \"force compression\" to compress.");
            return -1;
        }
        Compression c = new Compression(in, out, huffMap, header, freq, tree);
        return c.getBits();
    }

    /**
     * Uncompress a previously compressed stream in, writing the
     * uncompressed bits/data to out.
     * @param in is the previously compressed data (not a BitInputStream)
     * @param out is the uncompressed file/stream
     * @return the number of bits written to the uncompressed file/stream
     * @throws IOException if an error occurs while reading from the input file or
     * writing to the output file.
     */
    public int uncompress(InputStream in, OutputStream out) throws IOException {
        BitInputStream bitsIn = new BitInputStream(in);
        if (bitsIn.readBits(BITS_PER_INT) != MAGIC_NUMBER) {
            myViewer.showError("Error reading compressed file. \n" +
                    "File did not start with the huff magic number.");
            bitsIn.close();
            return -1;
        }
        Decompression d = new Decompression(bitsIn, out, myViewer);
	        return d.getBits();
    }

    public void setViewer(IHuffViewer viewer) {
        myViewer = viewer;
    }

    private void showString(String s){
        if (myViewer != null) {
            myViewer.update(s);
        }
    }
}
