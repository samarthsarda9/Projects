import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

/**
 * Store a list of phrases. Generally for use with a Hangman program.
 * @author scottm
 *
 */
public class PhraseBank {

    private static final String DEFAULT_FILE_NAME = "HangmanMovies.txt";

    // simple phrase bank if we can't find the right file
    private static final String[] EMERGENCY_PHRASES = {"BOB_SCHNIEDER",
            "WILCO", "LEON_BRIDGES", "CARRIE_RODRIGUEZ",
            "LIZZO", "SPOON",
            "BILLE_EILISH", "LYLE_LOVETT", "TAYLOR_SWIFT"};

    private static final String EMERGENCY_TOPIC = "Band";

    private static final int RANDOM_SEED = 6; // STUDENT VERSION IS 6!!!!

    private static int alternateSeed = -1;

    private static boolean useDefaultSeed = true;

    private ArrayList<String> phrases;
    private int currentIndex;
    private String topic;

    /**
     * Create a PhraseBank from the default file.
     */
    public PhraseBank(){
        this(DEFAULT_FILE_NAME);
    }

    /**
     * Create a PhraseBank from the given file name. If the file cannot be
     * found then use the default file. If that cannot be found use
     * the emergency phrase bank of bands. Uses default seed or alternate seed.
     * @param fileName The name of the file that has the phrase bank.
     */
    public PhraseBank(String fileName) {
        createPhraseList(fileName);
        if (useDefaultSeed) {
            Collections.shuffle(phrases, new Random(RANDOM_SEED)); // use this line for non random behavior
        } else {
            Collections.shuffle(phrases, new Random(alternateSeed));
        }
    }

    /**
     * Create a PhraseBank from the given file name. If the file cannot be
     * found then use the default file. If that cannot be found use
     * the emergency phrase bank of bands. The randomize parameter determines
     * if we use the default source of randomness or not.
     * @param fileName The name of the file that has the phrase bank.
     * @param randomize Whether to randomize the phrases
     * or use the default seeds.
     */
    public PhraseBank(String fileName, boolean randomize) {
        createPhraseList(fileName);
        if (!randomize) {
            if (useDefaultSeed) {
                // predictable behavior
                Collections.shuffle(phrases, new Random(RANDOM_SEED));
            } else {
                Collections.shuffle(phrases, new Random(alternateSeed));
            }
        } else {
            // make it less predictable
            Collections.shuffle(phrases);
        }
    }


    /**
     * Set the alternate seed for Randomness.
     * @param newSeed
     */
    public static void setSeed(int newSeed) {
        alternateSeed = newSeed;
    }

    /**
     * Set whether we should use the default seed for randomness
     * or the alternate seed.
     * @param useDefault
     */
    public static void setUseDefaultSeed(boolean useDefault) {
        useDefaultSeed = useDefault;
    }

    /**
     * Get what the alternate seed for Randomness is.
     * @return the current alternate seed
     */
    public static int getAlteranteSeed() {
        return alternateSeed;
    }


    // get the list of phrases ready from the fiven file.
    private void createPhraseList(String fileName) {
        phrases = new ArrayList<>();
        currentIndex = -1;
        loadWords(fileName);
    }

    /**
     * Call this method to get the next phrase.
     * The returned String will contain upper case
     * letters and underscores for spaces.
     * @return the next phrase
     */
    public String getNextPhrase(){
        currentIndex = (currentIndex + 1) % phrases.size();
        return phrases.get(currentIndex);
    }

    /**
     * Return the topic of this phrase bank.
     * @return The topic of this phrase bank.
     */
    public String getTopic(){
        return topic;
    }


    // Read the topic and phrases from the file.
    // If there is any problem then use the Emergency topic and phrases.
    private void loadWords(String fileName) {
        try {
            Scanner s = new Scanner(new File(fileName));
            topic = s.nextLine();
            while (s.hasNextLine()) {
                String phrase = trim(s.nextLine().trim());
                phrases.add(phrase.toUpperCase());
            }
            s.close();
        } catch (IOException e) {
            System.out.println("\n***** ERROR IN READING FILE ***** ");
            System.out.println("Can't find this file "
                    + fileName + " in the current directory.");
            System.out.println("Error: " + e);
            String currentDir = System.getProperty("user.dir");
            System.out.println("Be sure " + fileName + " is in this directory: ");
            System.out.println(currentDir);
            // problem with reading file, use the emergency topic and phrases
            System.out.println();
            System.out.println("Program will use the back-up topic of Bands Mike likes");
            constructFromEmergencyData();
        }
        // if no values construct from emergency data
        if (phrases.size() == 0) {
            constructFromEmergencyData();
        }
    }

    // no files found, use our emergency list of phrases
    private void constructFromEmergencyData() {
        topic = EMERGENCY_TOPIC;
        for (String phrase : EMERGENCY_PHRASES) {
            phrases.add(phrase);
        }
    }

    // I assume nextLine is not null.
    // Return a String with only characters and
    // underscores for spaces.
    // No other characters in org are included
    // in the result.
    private static String trim(String org) {
        String result = "";
        for (int i = 0; i < org.length(); i++) {
            char ch = org.charAt(i);
            if ( Character.isLetter(ch)) {
                result += ch;
            } else if(ch == ' ') {
                result += '_';
            }
        }
        return result;
    }
}

