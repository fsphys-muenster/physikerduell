package de.uni_muenster.physikerduell;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @author Simon May
 * 
 */
public class Question {

	private final String text;
	private final List<Answer> answers;

	public Question(String question, List<Answer> answers) {
		if (question == null || answers == null) {
			throw new IllegalArgumentException("An argument was null");
		}
		this.text = question;
		this.answers = new ArrayList<>(answers);
		// Antworten nach Punkten absteigend sortiert
		Collections.sort(this.answers, Collections.reverseOrder());
	}

	public String getText() {
		return text;
	}

	public Answer getAnswer(int index) {
		return answers.get(index);
	}

	public String getAnswerText(int index) {
		return answers.get(index).getText();
	}

	public int getAnswerScore(int index) {
		return answers.get(index).getScore();
	}

	public List<Answer> getAnswers() {
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
