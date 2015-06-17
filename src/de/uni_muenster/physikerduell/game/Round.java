package de.uni_muenster.physikerduell.game;

/**
 * XXX Klasse bisher nicht benutzt
 */
public class Round {

	private final int numberOfAnswers;
	private final int roundMultiplier;
	//private RoundState state = RoundState.BUZZER;

	public Round(int roundMultiplier, int numberOfAnswers) {
		if (numberOfAnswers < 0) {
			throw new IllegalArgumentException("Number of answers has to be > 0!");
		}
		this.numberOfAnswers = numberOfAnswers;
		this.roundMultiplier = roundMultiplier;
	}
	
	public void endRound(boolean stealSuccess, Answer endingAnswer) {
	}

	public int numberOfAnswers() {
		return numberOfAnswers;
	}

	public int roundMultiplier() {
		return roundMultiplier;
	}
	
}
