import java.util.Arrays;
import java.util.NoSuchElementException;

/**
 * CS312 Assignment 11 - Guitar Hero
 *
 * On my honor, <Samarth Sarda>, this programming assignment is my own work and I have
 * not viewed a solution to this problem from another student or online source.
 * I have also not allowed other students to view my code. Also, I will 
 * not share my solution in the future on Course Hero, a public GitHub repo,
 * or any other place, since this encourages cheating by others and demonstrates
 * a serious lack of academic integrity.
 * 
 *  email address: samarthsarda@utexas.edu
 *  UT EID: ss224784
 *  TA name: Austin
 *
 */

public class RingBuffer {
	
	private double[] ringBuffer;
	private int capacity;
	private int size;
	private int start;
	private int end;
	
	/*
	 * Constructor method for the ring buffer
	 * Takes in capacity parameter to determine length of ring buffer
	 * Sets private variables to its values
	 * No return value since it creates an object
	 */
	public RingBuffer(int capacity) {
		this.ringBuffer = new double[capacity];
		this.capacity = capacity;
		size = 0;
		start = 0; 
		end = 0;
	}
	
	/*
	 * Method that returns the current size of the ring buffer
	 * Takes in no parameters
	 * Returns the int size 
	 */
	public int size() {
		return size;
	}
	
	/*
	 * Method that determines if the ring buffer is empty
	 * Takes in no parameters
	 * Returns a boolean value 
	 */
	public boolean isEmpty() {
		return size == 0;
	}
	
	/*
	 * Method that determines if the ring buffer is full
	 * Takes in no parameters
	 * Returns a boolean value
	 */
	public boolean isFull() {
		return size == this.capacity;
	}
	
	/*
	 * Method that enqueues a value in the ring buffer
	 * Takes in x parameter to set the value in the buffer
	 * Void method so no return value
	 */
	public void enqueue(double x) {
		if(isFull()) {
			throw new IllegalStateException("Queue is full.");
		}
		this.ringBuffer[end] = x;
		size++;
		end = (end + 1) % this.capacity;
	}
	
	/*
	 * Method that dequeues a value in the ring buffer
	 * Takes in no parameters
	 * Returns the double value from the start of the buffer
	 */
	public double dequeue() {
		if(isEmpty()) {
			throw new NoSuchElementException("Queue is empty.");
		}
		double dequeueReturn = this.ringBuffer[start];
		size--;
		start = (start + 1) % this.capacity;
		return dequeueReturn;
	}
	
	/*
	 * Method that returns the value from the front of the ring buffer
	 * Takes in no parameters
	 * Returns the double value from the start of the buffer
	 */
	public double peek() {
		if(isEmpty()) {
			throw new NoSuchElementException("Cannot call peek on an empty RingBuffer.");
		}
		return this.ringBuffer[start];
	}
	
	/*
	 * Method that sets the toString value for the ring buffer
	 * Takes in no parameters
	 * Returns the string representation of the ring buffer
	 */
	public String toString() {
		int currIndex = start;
		String list = "[";
		//Loop that runs once based on the size variable
		for(int i = 0; i < size; i++) {
			list += ringBuffer[currIndex];
			if(i < size - 1) list += ", ";
			currIndex = (currIndex + 1) % this.capacity;
		}
		list += "]";
		return list;
	}
}
