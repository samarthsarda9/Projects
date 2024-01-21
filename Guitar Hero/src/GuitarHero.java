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


public class GuitarHero {
	
	public static final String NOTES = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";
	public static final GuitarString[] STRINGS = new GuitarString[NOTES.length()];
	public static final double CONCERT_A = 440.0, BASE_FREQUENCY = 1.05956;
	public static final int ITH_EXPONENT = 24;
	

	public static void main(String[] args) {
		//Creates an array of guitar strings based on length of NOTES string
		//Loop that runs the length of STRINGS array
		for(int i = 0; i < STRINGS.length; i++) {
			double frequency = CONCERT_A * Math.pow(BASE_FREQUENCY, (i - ITH_EXPONENT));
			STRINGS[i] = new GuitarString(frequency);
		}
		//The main input loop
		Keyboard keyboard = new Keyboard();
		//Loop that runs while the condition is true
		while(true) {
			//Check if the user has played a key, if so processes it
			if(keyboard.hasNextKeyPlayed()) {
				//Key the user played
				char key = keyboard.nextKeyPlayed();
				//Plucks the corresponding string
				//Loop that runs the length of the NOTES string
				for(int i = 0; i < NOTES.length(); i++) {
					if(NOTES.charAt(i) == key) {
						STRINGS[i].pluck();
					}
				}
			}
			double sample = 0;
			//Computes the superposition of the samples
			//Loop that runs the length of the NOTES string 
			for(int i = 0; i < NOTES.length(); i++) {
				sample += STRINGS[i].sample();
				//Advance the simulation of each guitar string by one step
				STRINGS[i].tic();
			}
			//Play the sample on standard audio
			StdAudio.play(sample);
		}
	}
}
