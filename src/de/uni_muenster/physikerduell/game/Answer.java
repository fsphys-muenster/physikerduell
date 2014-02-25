package de.uni_muenster.physikerduell.game;

/**
 * The Answer class contains the information about an answer to a question, i.e. the
 * answer text, how many points the answer is worth and whether it has been revealed.
 * 
 * @author Simon May
 * 
 */
public class Answer implements Comparable<Answer> {

	private final String text;
	private final int score;
	private Game game;
	private boolean revealed;

	Answer(Game game, String text, int score) {
		if (score < 0) {
			throw new IllegalArgumentException("Score < 0, was " + score);
		}
		this.game = game;
		this.text = text;
		this.score = score;
	}

	/**
	 * Constructs an Answer with the given text and score.
	 * 
	 * @param text
	 *            The Answer's text
	 * @param score
	 *            The Answer's score
	 */
	public Answer(String text, int score) {
		this(null, text, score);
	}

	/**
	 * Returns this Answer in text form.
	 * 
	 * @return The Answer's text
	 */
	public String getText() {
		return text;
	}

	/**
	 * Returns this Answer's score, i.e. how many points it is worth.
	 * 
	 * @return The Answer's score
	 */
	public int getScore() {
		return score;
	}

	/**
	 * Returns whether this Answer is revealed or not.
	 * 
	 * @return the The Answer's state
	 */
	public boolean isRevealed() {
		return revealed;
	}

	/**
	 * Sets whether this Answer is revealed or not.
	 * 
	 * @param revealed
	 *            The Answer's state
	 */
	public void setRevealed(boolean revealed) {
		this.revealed = revealed;
		if (game != null) {
			game.update();
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + text.hashCode();
		result = prime * result + score;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		else if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		Answer other = (Answer) obj;
		return text.equals(other.text) && score == other.score;
	}

	@Override
	public String toString() {
		return "[Answer] " + text;
	}

	/**
	 * Comparison of <code>Answer</code>s is done on the scores (for sorting). Note that
	 * this means that compareTo() == 0 does not imply equals().
	 */
	@Override
	public int compareTo(Answer o) {
		return Integer.compare(score, o.score);
	}

}
