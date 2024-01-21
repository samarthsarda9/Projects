/**
 * CS312 Assignment 5
 *
 * On my honor, <Samarth Sarda>, this programming assignment is my own work
 * and I have not shared my solution with any other student in the class.
 *
 * A program to play Hangman.
 *
 * Email address: samarthsarda@utexas.edu
 * UTEID: ss224784
 * TA: Austin
 */

import java.util.Scanner;

public class Hangman {
	
	public static final int MAX_NUM_GUESSES = 5;

    public static void main(String[] args) {

        // Load phrases from a file into the phrases variable
        PhraseBank phrases = buildPhraseBank(args);
        // Create a scanner for the keyboard. (Do not create another scanner object.)
        Scanner keyboard = new Scanner(System.in);
        // Print the intro to the program.
        intro(); 
    	boolean playGame = true;
    	//The loop will run once for each round played
        do {
            game(keyboard, phrases);
            System.out.print("Do you want to play again?\n"
            		+ "Enter 'Y' or 'y' to play again: ");
            String decision = keyboard.nextLine();
            if(decision.equals("y")||decision.equals("Y")) {
            	playGame = true;
            } else {
            	playGame = false;
            }
        } while (playGame);
        keyboard.close();
    }

    /*
     *  Method that initiates topic and then plays the game
     *  Uses scanner parameter to get user input
     *  uses PhraseBank parameter to obtain a phrase
     *  Void method so no return value
     */
    public static void game(Scanner keyboard, PhraseBank phrases) {
    	String topic = phrases.getTopic();
    	String phrase = phrases.getNextPhrase();
    	String notGuessed = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    	System.out.println("\nI am thinking of a " + topic + " ...\n");
    	String starredPhrase = starredPhrase(phrase);
    	userGuess(keyboard, phrase, notGuessed, starredPhrase);
    }
    
    /*
     * Method that displays letter availale for the game
     * Uses notGuessed parameter from game method to create the 
     * line of strings
     * Void method so no return value
     */
    public static void letters(String notGuessed) {
    	System.out.println("\nThe letters you have not guessed yet are: ");
    	System.out.print(notGuessed.charAt(0));
    	//The loop will run once for each character in notGuessed
    	for(int i = 1; i < notGuessed.length(); i++) {
    		System.out.print("--" + notGuessed.charAt(i));
    	}
    	System.out.println();
    }
    
    /*
     * Method that takes the chosen phrase and masks it
     * Uses phrase parameter as it is the phrase that will
     * be masked
     * Returns phrase as the starred string
     */
    public static String starredPhrase(String phrase) {
    	String starPhrase = "";
    	//The loop will run once for each character in phrase
    	for(int i = 0; i < phrase.length(); i++) {
    		if(phrase.charAt(i) == '_') {
    			starPhrase += "_";
    		} else {
    			starPhrase += "*";
    		}
    	}
    	return starPhrase;
    }
    
    /*
     * Method that plays the actual game and gets user input
     * Uses keyboard parameter to get user input
     * Uses phrase parameter to compare the starred phrase to
     * the actual phrase
     * Uses notGuessed parameter to keep updating the letters available
     * Uses starredPhrase parameter to keep updated the starred phrase
     * Void method so does not return anything
     */
    public static void userGuess(Scanner keyboard, String phrase, String notGuessed,
    		String starredPhrase) {
    	int wrong = 0;
    	boolean isGuessed = false;
		System.out.println("The current phrase is " + starredPhrase);
		//The loop will run once for each guess
    	while(wrong < MAX_NUM_GUESSES && !starredPhrase.equals(phrase)) {
    		if(isGuessed) {
        		System.out.println("The current phrase is " + starredPhrase);
    		}
        	letters(notGuessed);
    		System.out.print("\nEnter your next guess: ");
    		String guess =  keyboard.next() + keyboard.nextLine();
    		String charGuess = guess.charAt(0) + "";
    		if(isGuessed = notGuessed.contains(charGuess.toUpperCase())) {
    			System.out.println("\nYou guessed " + charGuess.toUpperCase() + ".");
    			if(phrase.contains(charGuess.toUpperCase())) {
    				System.out.println("\nThat is present in the secret phrase.");
    				starredPhrase = unstarPhrase(charGuess.toUpperCase(), starredPhrase, phrase);
    			} else {
    				System.out.println("\nThat is not present in the secret phrase.");
    				wrong++;
    			}
    			System.out.println("\nNumber of wrong guesses so far: " + wrong);
        		notGuessed = updateLetters(notGuessed, charGuess.toUpperCase());
    		} else {
    			System.out.print("\n" + guess + " is not a valid guess.");
    		}
    	}
    	ending(starredPhrase, wrong, phrase);
    }
    
    /*
     * Method that uses user input and unstars the letter 
     * Uses guess parameter to compare the letter to the word and 
     * fine instances in the word
     * Uses starredPhrase parameter to take the starred phrase and
     * update it
     * Uses phrase method to check if guess is present in the phrase
     * Returns the iterated starredPhrase back to the previous method
     */
    public static String unstarPhrase(String guess, String starredPhrase, String phrase) {
    	String unstarred = "";
    	char charGuess = guess.charAt(0);
    	//The loop will run once for each character in phrase
    	for(int i = 0; i < phrase.length(); i++) {
    		if(phrase.charAt(i) == charGuess) {
    			unstarred += phrase.charAt(i);
    		} else {
    			unstarred += starredPhrase.charAt(i);
    		}
    	}
    	//System.out.println("The current phrase is " + unstarred);
    	return unstarred;
    }
    
    /*
     * Method that updates the letters available to use in game
     * Uses notGuessed parameter to update the letters available from
     * original string
     * Uses guess method to to remove that specific letter from the String
     * Returns the updated string of letters to be displayed in userGuess method
     */
    public static String updateLetters(String notGuessed, String guess) {
    	int index = notGuessed.indexOf(guess);
    	notGuessed = notGuessed.substring(0, index) + notGuessed.substring(index + 1);
    	return notGuessed;
    }
    
    /*
     * Method that displays the ending results of the game based 
     * on the what happened in the game
     * Uses starredPhrase parameter to display the unstarred phrase
     * if player wins
     * Uses phrase parameter to display original phrase if user loses
     * Void method so no return value
     */
    public static void ending(String starredPhrase, int wrong, String phrase) {
		//System.out.println("\nNumber of wrong guesses so far: " + wrong);
    	if(wrong < MAX_NUM_GUESSES) {
    		System.out.println("The phrase is " + starredPhrase + ".");
    		System.out.println("You win!!");
    	} else {
    		System.out.println("You lose. The secret phrase was " + phrase.toUpperCase());
    	}
    }


    // Build the PhraseBank.
    // Creates a set of secret phrases read from a file using the PhraseBank.java class.
    // ** Except for lines 47 and 48, do not change this method. **
    public static PhraseBank buildPhraseBank(String[] args) {
        PhraseBank result;
        // If no command line arguments, 
        if (args == null || args.length == 0
                        || args[0] == null || args[0].length() == 0) {
            result =  new PhraseBank(); // Creates a PhraseBank with the default file, HangmanMovies.txt
            // result = new PhraseBank("ProSportsTeams.txt"); // Creates a PhraseBank with the filename you specify
            
        // Yes, command line arguments. The grading script uses this code so that mutiple files can be tested.                    
        } else {
            result = new PhraseBank(args[0]);
        }

        return result;
    }


    // Print the intro to the program.
    // ** Do not change this method **
    public static void intro() {
        System.out.println("This program plays the game of hangman.");
        System.out.println();
        System.out.println("The computer will pick a random phrase.");
        System.out.println("Enter letters for your guess.");
        System.out.println("After 5 wrong guesses you lose.");
    }
}
