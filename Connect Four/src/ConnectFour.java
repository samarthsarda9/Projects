import java.util.Arrays;
import java.util.Scanner;

/**
 * CS312 Assignment 9.
 *
 * On my honor, <Samarth Sarda>, this programming assignment is my own work and I have
 * not viewed a solution to this problem from another student or online source.
 * I have also not allowed other students to view my code. Also, I will 
 * not share my solution in the future on Course Hero, a public GitHub repo,
 * or any other place, since this encourages cheating by others and demonstrates
 * a serious lack of academic integrity.
 * 
 *  email address: samarthsarda@utexas.edu
 *  UTEID: ss224784
 *  TA name: Austin
 *
 * Program that allows two people to play Connect Four.
 */


public class ConnectFour {

    // CS312 Students, add you constants here
	public static final int NUM_TURNS = 42, NAMES = 2;
	public static final int COLUMNS = 7, ROWS = 6;
	public static final int DIRECTIONS = 4, WINS = 4;
	public static final int POS_DIRECTION = 1, NEG_DIRECTION = -1;
	public static final int NO_DIRECTION = 0;

    public static void main(String[] args) {
    	
    	Scanner keyboard = new Scanner(System.in);
    	
        intro();
    	int[] height = new int[COLUMNS];
        String[] names = getPlayerName(keyboard);
        char[] color = {'r', 'b'};
        String[][] board = board();
        int colChoice = 0; 
        int turn = 0;
        boolean won = false;
        //Loop that will run until there is a win or 
        //turns is equal to 42
        while(!won && turn < NUM_TURNS) {
        	System.out.println("\nCurrent Board");
        	displayBoard(board);
        	colChoice = getChoice(names, keyboard, color, height, turn);
        	updateBoard(board, colChoice, color, height, turn);
        	won = checkWin(board);
        	turn++;
        }
        printWinner(board, names, turn - 1, won);
        
        keyboard.close();
    }

    // CS312 Students, add your methods
    
    /*
     * Method that gets player names from user
     * Takes in keyboard parameter to get input from user
     * Returns a String array back to main to be used
     */
    public static String[] getPlayerName(Scanner keyboard) {
    	String[] names = new String[NAMES];
    	System.out.print("Player 1 enter your name: ");
    	names[0] = keyboard.nextLine();
    	System.out.print("\nPlayer 2 enter your name: ");
    	names[1] = keyboard.nextLine();
    	return names;
    }
    
    /*
     * Method that creates the 2D array board and sets 
     * elements to ". "
     * Does not take in any parameters
     * Returns a String 2D array to be used
     */
    public static String[][] board() {
    	String[][] board = new String[ROWS][COLUMNS];
    	//Loop that runs once till i = ROWS
    	for(int i = 0; i < ROWS; i++) {
    		//Loop that runs once till j = COLUMNS
    		for(int j = 0; j < COLUMNS; j++) {
    			board[i][j] = ". ";
    		}
    	}
    	return board;
    }
    
    /*
     * Method that displays the board 
     * Takes in board parameter to be used to print
     * Void method so no return value
     */
    public static void displayBoard(String [][] board) {
    	//Loop that runs once till i > COLUMNS
    	for(int i = 1; i <= COLUMNS; i++) {
    		System.out.print(i + " ");
    	}
    	System.out.print(" column numbers\n");
    	//Loop that runs once for length of board array
    	for(int i = 0; i < board.length; i++) {
    		//Loop that runs once for length of board[0] array
    		for(int j = 0; j < board[0].length; j++) {
    			System.out.print(board[i][j]);
    		}
    		System.out.println();
    	}	
    }
    
    /*
     * Method that gets the user choice for their turn
     * Takes in names parameter to display who's turn
     * Takes in keyboard parameter to get user input
     * Takes in color parameter to show the player's piece
     * Takes in height parameter to be used to check the choice
     * Takes in turn parameter to flip between players
     * Returns an int value as the column choice 
     */
    public static int getChoice(String names[], Scanner keyboard, char[] color, 
    		int[] height, int turn) {
    	int index = turn % 2;
    	System.out.println("\n" + names[index] + " it is your turn.");
    	System.out.println("Your pieces are the " + color[index] + "'s.");
    	String prompt = names[index] + ", enter the column to drop your checker: ";
    	int colChoice = 0;
    	boolean valid = false;
    	//Loop that runs until a valid input is given
    	while(!valid) {
    		System.out.print(prompt);
    		colChoice = getInt(keyboard, prompt);
    		valid = checkChoice(colChoice, valid, height);
    	}
    	return colChoice;
    }
    
    /*
     * Method that checks validity of user column choice 
     * Takes in colChoice parameter to be checked
     * Takes in valid parameter to decide between true and false
     * Takes in height parameter as way to check if valid
     * Returns a boolean if choice is valid or not
     */
    public static boolean checkChoice(int colChoice, boolean valid, int[] height) {
    	if(colChoice < 1 || colChoice > COLUMNS) {
    		System.out.println("\n" + colChoice + " is not a valid column.");
    		return valid;
    	} else if(height[colChoice - 1] == ROWS) {
    		System.out.println("\n" + colChoice + " is not a legal column. That column is full");
    		return valid;
    	}
    	return !valid;
    }
    
    /*
     * Method that updates the board based on user choice
     * Takes in board parameter to be changed
     * Takes in colChoice parameter to be used to alter board
     * Takes in color parameter to assign element in board
     * Takes in height parameter to calculate the row
     * Takes in turn parameter to decide between colors
     * Void method so no return value
     */
    public static void updateBoard(String[][] board, int colChoice, char[] color, 
    		int[] height, int turn) {
    	int index = turn % 2;
    	board[ROWS - height[(colChoice - 1)] - 1][colChoice - 1] = color[index] + " ";
    	height[colChoice - 1]++;
    }
    
    /*
     * Method that checks the board for a win
     * Takes in board parameter to be used to check for win
     * Returns a boolean based on if there is a win or not
     */
    public static boolean checkWin(String[][] board) {
    	int[] rows = {NO_DIRECTION, POS_DIRECTION, POS_DIRECTION, POS_DIRECTION}, 
    			columns = {POS_DIRECTION, POS_DIRECTION, NO_DIRECTION, NEG_DIRECTION};
    	//Loop that runs once for length of board array
    	for(int i = 0; i < board.length; i++) {
    		//Loop that runs once for length of board[0] array
			for(int j = 0; j < board[0].length; j++) {
				if(!board[i][j].equals( ". ")) {
					//Loop that runs until k = DIRECTIONS
					for(int k = 0; k < DIRECTIONS; k++) {
						int win = 1;
						int tempR  = 0, tempC = 0;
						//Loop that runs until no longer in bounds or elements aren't equal
						while(inBounds(i, j, tempR, tempC) && board[i][j].equals(board[i + tempR][j + tempC])) {
							tempR += rows[k];
							tempC += columns[k];
							if(inBounds(i, j, tempR, tempC) && board[i][j].equals(board[i + tempR][j + tempC])) {
								win++;
							}
							if(win == WINS) return true;
						}
					}
				}
			}
		}
    	return false;
    }
    
    /*
     * Method that checks if the index of a element
     * is in bounds
     * Takes in i and j parameters as index of original element
     * Takes in tempR and tempC parameters to be added to i and j
     * Returns a boolean if the index is in bounds or not
     */
    public static boolean inBounds(int i, int j, int tempR, int tempC) {
    	return (i + tempR < ROWS && j + tempC < COLUMNS && i + tempR >= 0 
    			&& j + tempC >= 0);
    }
    
    /*
     * Method that prints out the winner/draw and the final board
     * Takes in board parameter to print out final board
     * Takes in names parameter to print out winner
     * Takes in turn parameter to decide the winner
     * Takes in won parameter to decide if there is a draw
     * Void method so no return value
     */
    public static void printWinner(String[][] board, String[] names, int turn, 
    		boolean won) {
    	if(won == false) {
    		System.out.println("\nThe game is a draw.");
    	} else {
    		System.out.println("\n" + names[turn % 2] + " wins!!");
    	}
    	System.out.println("\nFinal Board");
    	displayBoard(board);
    }

    // show the intro
    public static void intro() {
        System.out.println("This program allows two people to play the");
        System.out.println("game of Connect four. Each player takes turns");
        System.out.println("dropping a checker in one of the open columns");
        System.out.println("on the board. The columns are numbered 1 to 7.");
        System.out.println("The first player to get four checkers in a row");
        System.out.println("horizontally, vertically, or diagonally wins");
        System.out.println("the game. If no player gets fours in a row and");
        System.out.println("and all spots are taken the game is a draw.");
        System.out.println("Player one's checkers will appear as r's and");
        System.out.println("player two's checkers will appear as b's.");
        System.out.println("Open spaces on the board will appear as .'s.\n");
    }


    // Prompt the user for an int. The String prompt will
    // be printed out. I expect key is connected to System.in.
    public static int getInt(Scanner keyboard, String prompt) {
        while(!keyboard.hasNextInt()) {
            String notAnInt = keyboard.nextLine();
            System.out.println("\n" + notAnInt + " is not an integer.");
            System.out.print(prompt);
        }
        int result = keyboard.nextInt();
        keyboard.nextLine();
        return result;
    }
}
