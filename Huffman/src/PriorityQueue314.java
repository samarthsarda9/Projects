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

import java.util.Iterator;
import java.util.LinkedList;

public class PriorityQueue314<E extends Comparable<? super E>> {
    
    private LinkedList<E> con;
    private int size;
    
    /**
     * Creates a specialized PriorityQueue used for building a Huffman Tree
     */
    public PriorityQueue314(){
        this.con = new LinkedList<>();
        size = 0;
    }
    
    /**
     * Adds an item to the queue based on where it should be in accordance to other items
     * @param item - item to be added to the queue
     */
    public void enqueue(E item) {
        if (item == null) {
            throw new IllegalArgumentException("violation of precondition.");
        }
        int index = findIndex(item);
        con.add(index, item);
        size++;
    }
    
    /**
     * Helper method that gets the index of where the item should be added
     * @param item - item to be added to the queue
     * @return int representing the index of where the item will be added
     */
    private int findIndex(E item) {
        int index = 0;
        Iterator<E> it = con.iterator();
        while (it.hasNext() && it.next().compareTo(item) <= 0) {
            index++;
        }
        return index;
    }
    
    /**
     * Removes an item from the front of the queue
     * @return item that was removed from the front of the queue
     */
    public E dequeue() {
        size--;
        return con.remove(0);
    }
    
    /**
     * Gets the size of the priority queue
     * @return int representing the size of the queue
     */
    public int size() {
        return size;
    }
    
    /**
     * Gets a string representation of the priority queue
     * @return String representation of queue
     */
    public String toString() {
        String s = "[";
        Iterator<E> it = con.iterator();
        while (it.hasNext()) {
            s += it.next() + ", ";
        }
        s += "]";
        return s;
    }
}
