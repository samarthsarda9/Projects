/*  Student information for assignment:
*
*  On my honor, Samarth Sarda, this programming assignment is my own work
*  and I have not provided this code to any other student.
*
*  Name: Samarth Sarda
*  email address: samarthsarda@utexas.edu
*  UTEID: ss224784
*  Section 5 digit ID: ss224784
*  Grader name: Namish
*  Number of slip days used on this assignment: 1
*/

//add imports as necessary

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.TreeMap;

/**
 * Manages the details of EvilHangman. This class keeps tracks of the possible
 * words from a dictionary during rounds of hangman, based on guesses so far.
 */
public class HangmanManager {

    private HangmanDifficulty difficulty;
    private ArrayList<Character> charsUsed;
    private ArrayList<String> totalWords;
    private int numGuesses;
    private ArrayList<String> availableWords;
    private String pattern;
    private boolean debugOn;
    private int turnCounter;

    /**
     * Create a new HangmanManager from the provided set of words and phrases. pre:
     * words != null, words.size() > 0
     * 
     * @param words   A set with the words for this instance of Hangman.
     * @param debugOn true if we should print out debugging to System.out.
     */
    public HangmanManager(Set<String> words, boolean debugOn) {
        if (words == null || words.size() <= 0) {
            throw new IllegalArgumentException(
                    "Violation of precondition: " + "words must not be null and size of words must be greater than 0.");
        }
        this.totalWords = new ArrayList<>(words);
        this.debugOn = debugOn;
    }

    /**
     * Create a new HangmanManager from the provided set of words and phrases.
     * Debugging is off. pre: words != null, words.size() > 0
     * 
     * @param words A set with the words for this instance of Hangman.
     */
    public HangmanManager(Set<String> words) {
        this(words, false);
    }

    /**
     * Get the number of words in this HangmanManager of the given length. pre: none
     * 
     * @param length The given length to check.
     * @return the number of words in the original Dictionary with the given length
     */
    public int numWords(int length) {
        int wordsOfLength = 0;
        for (int i = 0; i < totalWords.size(); i++) {
            if (totalWords.get(i).length() == length) {
                wordsOfLength++;
            }
        }
        return wordsOfLength;
    }

    /**
     * Get for a new round of Hangman. Think of a round as a complete game of
     * Hangman.
     * 
     * @param wordLen    the length of the word to pick this time. numWords(wordLen)
     *                   > 0
     * @param numGuesses the number of wrong guesses before the player loses the
     *                   round. numGuesses >= 1
     * @param diff       The difficulty for this round.
     */
    public void prepForRound(int wordLen, int numGuesses, HangmanDifficulty diff) {
        if (numWords(wordLen) <= 0 || numGuesses < 1) {
            throw new IllegalArgumentException("Violation of precondition: "
                    + "Number of words for specified length must be greater than 0 and number "
                    + "of guesses must be greater than 0.");
        }
        charsUsed = new ArrayList<>();
        this.numGuesses = numGuesses;
        this.difficulty = diff;
        this.turnCounter = 1;
        this.availableWords = new ArrayList<>();
        for (int i = 0; i < totalWords.size(); i++) {
            // Word is initially available if the length matches the wordLen
            if (totalWords.get(i).length() == wordLen) {
                availableWords.add(totalWords.get(i));
            }
        }
        StringBuilder answer = new StringBuilder();
        // Creates initial pattern
        for (int i = 0; i < wordLen; i++) {
            answer.append("-");
        }
        this.pattern = answer.toString();
    }

    /**
     * The number of words still possible (live) based on the guesses so far.
     * Guesses will eliminate possible words.
     * 
     * @return the number of words that are still possibilities based on the
     *         original dictionary and the guesses so far.
     */
    public int numWordsCurrent() {
        return availableWords.size();
    }

    /**
     * Get the number of wrong guesses the user has left in this round (game) of
     * Hangman.
     * 
     * @return the number of wrong guesses the user has left in this round (game) of
     *         Hangman.
     */
    public int getGuessesLeft() {
        return numGuesses;
    }

    /**
     * Return a String that contains the letters the user has guessed so far during
     * this round. The characters in the String are in alphabetical order. The
     * String is in the form [let1, let2, let3, ... letN]. For example [a, c, e, s,
     * t, z]
     * 
     * @return a String that contains the letters the user has guessed so far during
     *         this round.
     */
    public String getGuessesMade() {
        Collections.sort(charsUsed);
        return charsUsed.toString();
    }

    /**
     * Check the status of a character.
     * 
     * @param guess The characater to check.
     * @return true if guess has been used or guessed this round of Hangman, false
     *         otherwise.
     */
    public boolean alreadyGuessed(char guess) {
        return charsUsed.contains(guess);
    }

    /**
     * Get the current pattern. The pattern contains '-''s for unrevealed (or
     * guessed) characters and the actual character for "correctly guessed"
     * characters.
     * 
     * @return the current pattern.
     */
    public String getPattern() {
        return pattern;
    }

    /**
     * Update the game status (pattern, wrong guesses, word list), based on the give
     * guess.
     * 
     * @param guess pre: !alreadyGuessed(ch), the current guessed character
     * @return return a tree map with the resulting patterns and the number of words
     *         in each of the new patterns. The return value is for testing and
     *         debugging purposes.
     */
    public TreeMap<String, Integer> makeGuess(char guess) {
        if (alreadyGuessed(guess)) {
            throw new IllegalArgumentException("Violation of precondition: " + "character is already guessed.");
        }
        TreeMap<String, Integer> result = new TreeMap<>();
        TreeMap<String, ArrayList<String>> families = new TreeMap<>();
        charsUsed.add(guess);
        for (int i = 0; i < availableWords.size(); i++) {
            String word = availableWords.get(i);
            String newPattern = updatePattern(word, guess, pattern);
            // Adds new key-value pair if pattern is not found
            if (!families.containsKey(newPattern)) {
                ArrayList<String> subFamily = new ArrayList<>();
                subFamily.add(word);
                families.put(newPattern, subFamily);
            } else {
                families.get(newPattern).add(word);
            }
        }
        getNewFamily(families);
        result = resultForDebug(families);
        // Checks if user guess is in updated pattern
        if (!pattern.contains(guess + "")) {
            numGuesses--;
        }
        return result;
    }

    /*
     * Uses current family TreeMap to store the pattern and the size of the
     * associated pattern family
     * 
     * @param families current family TreeMap
     * 
     * @return TreeMap that contains a string pattern key and integer size value
     */
    private TreeMap<String, Integer> resultForDebug(TreeMap<String, ArrayList<String>> families) {
        TreeMap<String, Integer> result = new TreeMap<>();
        for (String key : families.keySet()) {
            result.put(key, families.get(key).size());
        }
        return result;
    }

    /*
     * Manages the patterns created when a user inputs a character. This class aids
     * in sorting the list of patterns possible based on user input
     */
    private class Pattern implements Comparable<Pattern> {
        ArrayList<String> associatedWords;
        String currPattern;

        /*
         * Creates a new Pattern based on the current pattern and a list of patterns
         * 
         * @param currPattern possible pattern from tree
         * 
         * @param associatedWords words associated with currPattern
         */
        public Pattern(String currPattern, ArrayList<String> associatedWords) {
            this.associatedWords = associatedWords;
            this.currPattern = currPattern;
        }

        /*
         * Override compareTo Based on three criteria of comparing
         * 
         * @return positive or negative number based on comparison
         */
        public int compareTo(Pattern other) {
            if (associatedWords.size() != other.associatedWords.size()) {
                return other.associatedWords.size() - associatedWords.size();
            }
            int charRevealed1 = charRevealed(currPattern);
            int charRevealed2 = charRevealed(other.currPattern);
            if (charRevealed1 != charRevealed2) {
                return charRevealed1 - charRevealed2;
            }
            return currPattern.compareTo(other.currPattern);
        }

        /*
         * Counts the number of characters revealed in a pattern
         * 
         * @param currPattern current pattern in Pattern object
         * 
         * @return number of characters revealed
         */
        private int charRevealed(String currPattern) {
            int count = 0;
            for (int i = 0; i < currPattern.length(); i++) {
                if (currPattern.charAt(i) != '-') {
                    count++;
                }
            }
            return count;
        }
    }

    /*
     * Sorts and chooses the new pattern and associated family of words New family
     * is then used for the next guess from the user
     * 
     * @param families map of current families
     */
    private void getNewFamily(TreeMap<String, ArrayList<String>> families) {
        ArrayList<Pattern> patternGroup = new ArrayList<>();
        // Creates new Pattern objects
        for (String key : families.keySet()) {
            patternGroup.add(new Pattern(key, families.get(key)));
        }
        Collections.sort(patternGroup);
        pattern = difficultyChooser(patternGroup);
        availableWords = families.get(pattern);
        // Runs only if debugOn is true
        if (debugOn) {
            System.out.println("DEBUGGING: New pattern is: " + pattern + ". New family " + "has "
                    + availableWords.size() + " words.\n");
        }
    }

    /*
     * Picks the new pattern according to the difficulty Easy difficulty alternates
     * between hardest and second hardest list Medium difficulty chooses second
     * hardest list every 4th turn Hard difficulty always chooses hardest list
     * 
     * @param patternGroup list of Pattern family
     * 
     * @return pattern chosen based on difficulty
     */
    private String difficultyChooser(ArrayList<Pattern> patternGroup) {
        String tempPattern = "";
        // Only runs if debugOn is true
        if (debugOn) {
            debugPickingList(patternGroup);
        }
        if (difficulty.equals(HangmanDifficulty.EASY)) {
            if (turnCounter % 2 == 1 || patternGroup.size() == 1) {
                tempPattern = patternGroup.get(0).currPattern;
            } else {
                tempPattern = patternGroup.get(1).currPattern;
            }
        } else if (difficulty.equals(HangmanDifficulty.MEDIUM)) {
            if (turnCounter % 4 != 0 || patternGroup.size() == 1) {
                tempPattern = patternGroup.get(0).currPattern;
            } else {
                tempPattern = patternGroup.get(1).currPattern;
            }
        } else {
            tempPattern = patternGroup.get(0).currPattern;
        }
        turnCounter++;
        return tempPattern;
    }

    /*
     * Displays debugging messages if debug us on Messages based on the difficulty
     * of the round
     * 
     * @param patternGroup list of Pattern family
     */
    private void debugPickingList(ArrayList<Pattern> patternGroup) {
        if (difficulty.equals(HangmanDifficulty.EASY)) {
            if (turnCounter % 2 == 1) {
                System.out.println("\nDEBUGGING: Picking hardest list.");
            } else if (turnCounter % 2 == 0 && patternGroup.size() == 1) {
                System.out.println("\nDEBUGGING: Should pick second hardest pattern this turn,"
                        + " but only one pattern available.\n");
                System.out.println("DEBUGGING: Picking hardest list.");
            } else {
                System.out.println("\nDEBUGGING: Difficulty second hardest pattern and " + "list.\n");
            }
        } else if (difficulty.equals(HangmanDifficulty.MEDIUM)) {
            if (turnCounter % 4 != 0) {
                System.out.println("\nDEBUGGING: Picking hardest list.");
            } else if (turnCounter % 4 == 0 && patternGroup.size() == 1) {
                System.out.println("\nDEBUGGING: Should pick second hardest pattern this turn,"
                        + " but only one pattern available.\n");
                System.out.println("DEBUGGING: Picking hardest list.");
            } else {
                System.out.println("\nDEBUGGING: Difficulty second hardest pattern and " + "list.\n");
            }
        } else {
            System.out.println("\nDEBUGGING: Picking hardest list.");
        }
    }

    /*
     * Updates the pattern based on the guess of the user
     * 
     * @param word current word from the list of available words
     * 
     * @param guess guess from user
     * 
     * @param pattern the current pattern
     * 
     * @return updated pattern based on guess
     */
    private String updatePattern(String word, char guess, String pattern) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == guess) {
                sb.append(guess);
            } else {
                sb.append(pattern.charAt(i));
            }
        }
        return sb.toString();
    }

    /**
     * Return the secret word this HangmanManager finally ended up picking for this
     * round. If there are multiple possible words left one is selected at random.
     * <br>
     * pre: numWordsCurrent() > 0
     * 
     * @return return the secret word the manager picked.
     */
    public String getSecretWord() {
        String secretWord = "";
        if (availableWords.size() > 1) {
            int random = (int) (Math.random() * availableWords.size());
            secretWord = availableWords.get(random);
        } else {
            secretWord = availableWords.get(0);
        }
        return secretWord;
    }
}
