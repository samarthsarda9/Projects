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

import java.util.HashMap;
import java.util.Map;

public class HuffmanTree {
    
    private TreeNode root;
    private int size;
    private int leaves;
    
    /**
     * Creates a Huffman Tree to be used to store all values and their frequencies
     * Used for both compression and decompression
     * @param freq - frequency array of bits and their frequency in a file
     */
    public HuffmanTree(int[] freq) {
        if (freq == null) {
            throw new IllegalArgumentException("violation of precondition.");
        }
        PriorityQueue314<TreeNode> nodeQueue = new PriorityQueue314<>();
        for (int i = 0; i < freq.length; i++) {
            // Only add to queue if the frequency is not 0
            if (freq[i] != 0) {
                nodeQueue.enqueue(new TreeNode(i, freq[i]));
                size++;
            }
        }
        nodeQueue.enqueue(new TreeNode(IHuffConstants.PSEUDO_EOF, 1));
        // Add one to size to account for PSEUDO_EOF
        size++;
        // Keeps going till only one element is remaining, which is the full tree
        while (nodeQueue.size() != 1) {
            TreeNode temp1 = nodeQueue.dequeue();
            TreeNode temp2 = nodeQueue.dequeue();
            TreeNode parent = new TreeNode(temp1, -1, temp2);
            size++;
            nodeQueue.enqueue(parent);
        }
        this.root = nodeQueue.dequeue();
    }
    
    /**
     * Creates a Huffman Tree to be used to store all values
     * Only used in decompression for STF
     * @param t - first TreeNode in constructed tree in STF
     */
    public HuffmanTree(TreeNode t) {
        this.root = t;
    }
    
    /**
     * Gets the size of the Huffman Tree
     * @return - int representing the size of the tree
     */
    public int size() {
        return size;
    }
    
    /**
     * Creates a map with encoding values for each value in the Huffman Tree
     * @return - a map with new encodings for each value
     */
    public Map<Integer, String> huffMap(){
        Map<Integer, String> result = new HashMap<>();
        addToMap(result, root, "");
        return result;
    }
    
    /**
     * Recursive method that goes through the Huffman Tree and gets new encoding values
     * @param map - map given to store values and its new encodings
     * @param t - current node in Huffman Tree
     * @param s - encoding value being built for each value
     */
    private void addToMap(Map<Integer, String> map, TreeNode t, String s) {
        if (t.isLeaf()) {
            map.put(t.getValue(), s);
            leaves++;
        } else {
            addToMap(map, t.getLeft(), s + "0");
            addToMap(map, t.getRight(), s + "1");
        }
    }
    
    /**
     * Gets the number of leaf nodes in the Huffman Tree
     * @return int representing the total amount of leaf nodes in the tree
     */
    public int leafNodes() {
        return leaves;
    }
    
    /**
     * Gets the root of the Huffman Tree
     * @return a TreeNode representing the root of the tree
     */
    public TreeNode getRoot() {
        return root;
    }
}
