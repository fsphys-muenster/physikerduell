package de.uni_muenster.physikerduell.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 
 * The <code>Question</code> class contains the information about a question, i.e. the
 * question text and all of the possible <code>Answer</code>s to the question.
 * 
 * @author Simon May
 * 
 */
public class Question {

	private final String text;
	private final List<Answer> answers;

	/**
	 * Creates a Question whose text is given by <code>question</code> and all of whose
	 * possible answers are given by <code>answers</code>. The list of answers is copied,
	 * so the original can be modified afterwards.
	 * <p>
	 * Neither argument can be <code>null</code>.
	 * 
	 * @param question
	 *            The question's text
	 * @param answers
	 *            All of the question's possible answers
	 */
	public Question(String question, List<Answer> answers) {
		if (question == null || answers == null) {
			throw new IllegalArgumentException("An argument was null");
		}
		this.text = question;
		this.answers = new ArrayList<>(answers);
		// Antworten nach Punkten absteigend sortiert
		Collections.sort(this.answers, Collections.reverseOrder());
	}

	/**
	 * Returns the actual question in text form.
	 * 
	 * @return This question's text
	 */
	public String text() {
		return text;
	}

	/**
	 * Returns the possible answer to this question specified by <code>index</code>.
	 * Answers are sorted by their point value.
	 * 
	 * @param index
	 *            The answer's index
	 * @return An Answer to this question corresponding to the given index
	 */
	public Answer answer(int index) {
		return answers.get(index);
	}

	/**
	 * Returns the possible answer to this question in text form specified by
	 * <code>index</code>.
	 * 
	 * @param index
	 *            The answer's index
	 * @return An answer in text form to this question corresponding to the given index
	 */
	public String answerText(int index) {
		return answers.get(index).text();
	}

	/**
	 * Returns the score of a possible answer to this question specified by
	 * <code>index</code>.
	 * 
	 * @param index
	 *            The answer's index
	 * @return The score of an answer score to this question corresponding to the given
	 *         index
	 */
	public int answerScore(int index) {
		return answers.get(index).score();
	}

	/**
	 * Returns a read-only list of all possible answers to this question.
	 * 
	 * @return A read-only list of answers
	 */
	public List<Answer> allAnswers() {
		return Collections.unmodifiableList(answers);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + answers.hashCode();
		result = prime * result + text.hashCode();
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
		Question other = (Question) obj;
		return text.equals(other.text) && answers.equals(other.answers);
	}

	@Override
	public String toString() {
		return "[Question] " + text;
	}

}
