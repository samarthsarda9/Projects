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

public class GuitarString {
	private static final int SAMPLING_RATE = 44100;
	private static final double ENERGY_DECAY_FACTOR = 0.994;
	
	private int capacity;
	private RingBuffer string;
	private int ticCounter;
	
	/*
	 * Constructor method for the guitar string
	 * Takes in frequency parameter to determine the capacity of the ring buffer
	 * Initializes values of private variables
	 * No return value since method creates a object
	 */
	public GuitarString(double frequency) {
		this.capacity = (int) Math.round(SAMPLING_RATE/frequency);
		string = new RingBuffer(this.capacity);
		//Loop that runs once for the capacity value
		for(int i = 0; i < this.capacity; i++) {
			string.enqueue(0.0);
		}
		ticCounter = 0;
	}
	
	/*
	 * Constructor method for the guitar string
	 * Takes in initial parameter to assign values from array to ring buffer
	 * Initializes values of private variables
	 * No return value since method creates a object
	 */
	public GuitarString(double[] initial) {
		string = new RingBuffer(initial.length);
		//Loop that runs once for the length of the initial array
		for(int i = 0; i < initial.length; i++) {
			string.enqueue(initial[i]);
		}
	}
	
	/*
	 * Method that replaces the values in the ring buffer with values between -0.5 and 0.5
	 * Takes in no parameters
	 * Void method so no return value
	 */
	public void pluck() {
		double random = 0;
		//Loop that runs once for the capacity value
		for(int i = 0; i < this.capacity; i++) {
			string.dequeue();
			random = Math.random() - 0.5;
			string.enqueue(random);
		}
	}
	
	/*
	 * Method that applies the Karplus-Strong update by dequeuing and then enqueuing
	 * Takes in no parameters
	 * Void method so no return value
	 */
	public void tic() {
		double oldElement = string.dequeue();
		double newElement = string.peek();
		double average = ((oldElement + newElement) / 2) * ENERGY_DECAY_FACTOR;
		string.enqueue(average);
		ticCounter++;
	}
	
	/*
	 * Method that returns value from the front of the ring buffer
	 * Takes in no parameters
	 * Returns the double value from the front of the buffer
	 */
	public double sample() {
		return string.peek();
	}
	
	/*
	 * Method that returns the number of times the tic method was called
	 * Takes in no parameters
	 * Returns a int value based on the number of times tic was called
	 */
	public int time() {
		return ticCounter;
	}
	
	
}
