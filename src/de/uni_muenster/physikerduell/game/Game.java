package de.uni_muenster.physikerduell.game;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import de.uni_muenster.physikerduell.csv.CSVReader;

/**
 * The Game class represents the internal state (model) of the game and keeps track of
 * score, lives, current round and so on.
 * 
 * @author Simon May
 * 
 */
public class Game {

	/**
	 * Maximum number of answers which have to be found in a given round.
	 */
	public static final int MAX_ANSWERS = 6;
	/**
	 * Maximum number of lives a team can have during a round.
	 */
	public static final int MAX_LIVES = 3;
	/**
	 * Number of rounds which have to be played.
	 */
	public static final int NUM_ROUNDS = 5;
	/**
	 * File path for the log file.
	 */
	public static final String LOG_FILE_PATH = "Physikerduell-Log.txt";
	private final List<GameListener> listeners = new ArrayList<>();
	private final List<Question> questions = new ArrayList<>();
	private String team1Name = "Team 1";
	private String team2Name = "Team 2";
	private int team1Score;
	private int team2Score;
	private int currentLives;
	private int currentRound = 1;
	private int currentScore;
	private int currentQuestionIndex;
	private int currentTeam = -1;
	private boolean roundEnd;
	private boolean stealingPoints;
	private String lastLine;
	private FileWriter logWriter;
	private boolean updating = true;

	/**
	 * Creates the Game instance and reads the questions from the specified stream. The
	 * question file must be a CSV file with exactly two columns. Each question appears on
	 * its own row in the first column (with an empty second column) and the answers are
	 * given in the following rows with the answer text in the first and the score (a
	 * positive integer) in the second row. A question is terminated by a single row with
	 * empty columns.
	 * 
	 * @param questionFile
	 *            A stream from which the CSV file's characters are read (UTF-8)
	 * @throws GameException
	 *             If the question file cannot be read or is invalid
	 */
	public Game(InputStream questionFile) throws GameException {
		CSVReader csv;
		try {
			csv = new CSVReader(questionFile);
		}
		catch (IOException ex) {
			throw new GameException("Error reading CSV file", ex);
		}
		if (csv.getColumnCount() != 2) {
			throw new GameException("CSV file does not have 2 columns");
		}
		boolean questionRow = true;
		String questionText = "";
		List<Answer> answers = new ArrayList<>();
		for (int row = 0; row < csv.getRowCount(); row++) {
			if (csv.getItem(row, 0).isEmpty() && csv.getItem(row, 1).isEmpty()) {
				questionRow = true;
				questions.add(new Question(questionText, answers));
				answers.clear();
			}
			else if (questionRow) {
				questionText = csv.getItem(row, 0);
				questionRow = false;
				continue;
			}
			else {
				try {
					String answerText = csv.getItem(row, 0);
					int answerScore = Integer.parseInt(csv.getItem(row, 1));
					answers.add(new Answer(this, answerText, answerScore));
				}
				catch (NumberFormatException ex) {
					throw new GameException("Incorrect score field in CSV file, line "
						+ (row + 1), ex);
				}
			}
		}
	}

	/**
	 * Adds a <code>GameListener</code> to this <code>Game</code> instance so that it will
	 * receive updates about the game state.
	 * 
	 * @param listener
	 *            The <code>GameListener</code> to add (not <code>null</code>)
	 */
	public void addListener(GameListener listener) {
		if (listener == null) {
			throw new IllegalArgumentException("Listener was null");
		}
		listeners.add(listener);
	}

	/**
	 * Returns the number of lives remaining for the playing team in the current round.
	 * 
	 * @return The new number of remaining lives
	 */
	public int getCurrentLives() {
		return currentLives;
	}

	/**
	 * Returns the question which is currently selected.
	 * 
	 * @return The current question
	 */
	public Question currentQuestion() {
		return questions.get(currentQuestionIndex);
	}

	/**
	 * Returns the index of the question which is currently selected.
	 * 
	 * @return The current question's index
	 */
	public int getCurrentQuestionIndex() {
		return currentQuestionIndex;
	}

	/**
	 * Returns the round which is currently played.
	 * 
	 * @return The current round
	 */
	public int getCurrentRound() {
		return currentRound;
	}

	/**
	 * Returns the score accumulated by the playing team in the current round.
	 * 
	 * @return The current score
	 */
	public int currentScore() {
		return currentScore;
	}

	/**
	 * Returns the number of the team currently playing.
	 * 
	 * @return The number of the current team
	 */
	public int getCurrentTeam() {
		return currentTeam;
	}

	/**
	 * Returns the question specified by its numerical index.
	 * 
	 * @param index
	 *            The question's index (has to be a valid question index, i.e. &ge; 0 and
	 *            < questionCount())
	 * @return The corresponding question
	 */
	public Question getQuestion(int index) {
		return questions.get(index);
	}

	/**
	 * Returns the name of the first team.
	 * 
	 * @return The name of the first team
	 */
	public String getTeam1Name() {
		return team1Name;
	}

	/**
	 * Returns the score of the first team.
	 * 
	 * @return The score of the first team
	 */
	public int getTeam1Score() {
		return team1Score;
	}

	/**
	 * Returns the name of the second team.
	 * 
	 * @return The name of the second team
	 */
	public String getTeam2Name() {
		return team2Name;
	}

	/**
	 * Returns the score of the second team.
	 * 
	 * @return The score of the second team
	 */
	public int getTeam2Score() {
		return team2Score;
	}

	/**
	 * Returns whether the game is being logged.
	 * 
	 * @return The logging status
	 */
	public boolean isLogging() {
		return logWriter != null;
	}

	/**
	 * Returns the number of answers which have to be found in the current round.
	 * 
	 * @return Current number of answers to be found
	 */
	public int numberOfAnswers() {
		int answers = MAX_ANSWERS;
		switch (currentRound) {
		case 3:
			answers = 5;
			break;
		case 4:
			answers = 4;
			break;
		case 5:
			answers = 3;
			break;
		}
		return answers;
	}

	/**
	 * Returns the total number of questions loaded in this Game instance.
	 * 
	 * @return The number of questions
	 */
	public int questionCount() {
		return questions.size();
	}

	/**
	 * Updates the game if a correct answer to the current question has been given.
	 * 
	 * @param index
	 *            The index of the correctly given answer to the current question
	 */
	public void correctAnswer(int index) {
		currentQuestion().answer(index).setRevealed(true);
		updateCurrentScore();
		// Bei Punkteklau: Erfolgreich geklaut, sonst normale richtige Antwort
		if (stealingPoints) {
			endRound(true);
		}
		update();
	}

	public void wrongAnswer() {
		// no action if no team selected
		if (currentTeam == -1) {
			return;
		}
		// decrease lives; if no lives: change team
		if (currentLives > 1 && !stealingPoints) {
			currentLives--;
		}
		else if (stealingPoints) {
			// points could not be stolen; update score
			endRound(false);
		}
		// 3 lives lost
		else {
			currentLives = MAX_LIVES;
			stealingPoints = true;
			if (currentTeam == 1) {
				currentTeam = 2;
			}
			else if (currentTeam == 2) {
				currentTeam = 1;
			}
		}
		update();
	}

	/**
	 * Removes a <code>GameListener</code> from this <code>Game</code> instance so that it
	 * will no longer receive updates about the game state.
	 * 
	 * @param listener
	 *            The <code>GameListener</code> to remove
	 */
	public void removeListener(GameListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Returns the score multiplier for the current round. Scores are multiplied with this
	 * value before they are awarded.
	 * 
	 * @return The current score multiplier
	 */
	public int roundMultiplier() {
		int multiplier = 1;
		switch (currentRound) {
		case 3:
		case 4:
			multiplier = 2;
			break;
		case 5:
			multiplier = 3;
			break;
		}
		return multiplier;
	}

	/**
	 * 
	 * Sets the lives remaining for the playing team in the current round.
	 * 
	 * @param currentLives
	 *            The new number of lives (&ge; 0)
	 */
	public void setCurrentLives(int currentLives) {
		if (currentLives < 0) {
			throw new IllegalArgumentException("Lives < 0, was " + currentLives);
		}
		this.currentLives = currentLives;
		update();
	}

	/**
	 * Sets the current question by specifying its numerical index.
	 * 
	 * @param currentQuestionIndex
	 *            The new current question's index (has to be a valid question index, i.e.
	 *            &ge; 0 and < questionCount())
	 */
	public void setCurrentQuestionIndex(int currentQuestionIndex) {
		if (currentQuestionIndex < 0 || currentQuestionIndex > questions.size()) {
			throw new IndexOutOfBoundsException("Invalid question index: "
				+ currentQuestionIndex);
		}
		if (currentQuestionIndex != this.currentQuestionIndex) {
			this.currentQuestionIndex = currentQuestionIndex;
			updating = false;
			// New question => no answer revealed
			currentScore = 0;
			for (int i = 0; i < MAX_ANSWERS; i++) {
				currentQuestion().answer(i).setRevealed(false);
			}
			updating = true;
		}
		if (roundEnd) {
			currentRound++;
			if (currentRound > NUM_ROUNDS) {
				currentRound = NUM_ROUNDS;
			}
			currentTeam = -1;
			currentLives = MAX_LIVES;
			stealingPoints = false;
		}
		roundEnd = false;
		update();
	}

	/**
	 * Sets the current round.
	 * 
	 * @param currentRound
	 *            The new current round (&ge; 0)
	 */
	public void setCurrentRound(int currentRound) {
		if (currentRound < 0) {
			throw new IllegalArgumentException("Invalid round number: " + currentRound);
		}
		this.currentRound = currentRound;
		update();
	}

	/**
	 * Sets the current team.
	 * 
	 * @param teamNumber
	 *            The number of the new current team. Valid values are 1, 2 or -1 (no
	 *            team)
	 */
	public void setCurrentTeam(int teamNumber) {
		if (teamNumber != 1 && teamNumber != 2 && teamNumber != -1) {
			throw new IllegalArgumentException(
				"Team Number has to be 1, 2 or -1 (no team), was " + teamNumber);
		}
		this.currentTeam = teamNumber;
		update();
	}

	/**
	 * Activate or deactivate logging changes to the game to a file (
	 * <code>LOG_FILE_PATH</code>). If logging is activated, the log file is created in
	 * the current working directory. If it already exists, it is appended to.
	 * 
	 * @param logging
	 *            Activate (true) or deactivate (false) logging
	 */
	public void setLogging(boolean logging) {
		if (logging) {
			File logFile = new File(LOG_FILE_PATH);
			try {
				// append if file exists
				logWriter = new FileWriter(logFile, true);
				String timeStamp =
					new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(Calendar
						.getInstance().getTime());
				String newLine = "\n\n\n[" + timeStamp + "]\n----- Neues Spiel -----\n";
				logWriter.write(newLine);
				logWriter.flush();
			}
			catch (IOException ex) {
				logWriter = null;
				System.err.println("Error starting log: " + ex);
			}
		}
		else if (logWriter != null) {
			try {
				logWriter.close();
			}
			catch (IOException ex) {
				System.err.println("Error closing log: " + ex);
			}
			logWriter = null;
		}
	}

	/**
	 * Sets the name of the first team.
	 * 
	 * @param team1Name
	 *            The new name of the first team (not <code>null</code>)
	 */
	public void setTeam1Name(String team1Name) {
		if (team1Name == null) {
			throw new IllegalArgumentException("Team 1 name was null");
		}
		this.team1Name = team1Name;
		update();
	}

	/**
	 * Sets the score of the first team.
	 * 
	 * @param team1Score
	 *            The new score of the first team (not <code>null</code>)
	 */
	public void setTeam1Score(int team1Score) {
		if (team1Score < 0) {
			throw new IllegalArgumentException("Team score < 0, was " + team1Score);
		}
		this.team1Score = team1Score;
		update();
	}

	/**
	 * Sets the name of the second team.
	 * 
	 * @param team2Name
	 *            The new name of the second team (not <code>null</code>)
	 */
	public void setTeam2Name(String team2Name) {
		if (team1Name == null) {
			throw new IllegalArgumentException("Team 2 name was null");
		}
		this.team2Name = team2Name;
		update();
	}

	/**
	 * Sets the score of the second team.
	 * 
	 * @param team2Score
	 *            The new score of the second team
	 */
	public void setTeam2Score(int team2Score) {
		if (team2Score < 0) {
			throw new IllegalArgumentException("Team score < 0, was " + team2Score);
		}
		this.team2Score = team2Score;
		update();
	}

	/**
	 * Returns the total score accumulated by the playing team in the current round that
	 * would be awarded at the end of the round. This score includes the round multiplier.
	 * 
	 * @return The current score (with round multiplier)
	 */
	public int totalCurrentScore() {
		return currentScore * roundMultiplier();
	}

	/**
	 * Updates the log file with the current state of the game.
	 */
	private void updateLog() {
		Question curr = currentQuestion();
		String timeStamp =
			new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(Calendar.getInstance()
				.getTime());
		String newLine =
			"[" + timeStamp + "]\n" + team1Name + ": " + String.valueOf(team1Score)
				+ " | " + team2Name + ": " + String.valueOf(team2Score)
				+ " | CurrentScore:    " + String.valueOf(currentScore)
				+ " | CurrentTeam:     " + String.valueOf(currentTeam)
				+ " | CurrentLives:    " + String.valueOf(currentLives)
				+ " | CurrentRound:    " + String.valueOf(currentRound)
				+ " | CurrentQuestion: " + curr.text() + "\n";
		for (int i = 0; i < Game.MAX_ANSWERS; i++) {
			Answer ans = curr.answer(i);
			String revealed = ans.isRevealed() ? "X" : " ";
			newLine += revealed + " " + ans.text() + " [" + ans.score() + "]\n";
		}
		try {
			if (!newLine.equals(lastLine)) {
				logWriter.write("\n\n");
				logWriter.write(newLine);
			}
			//else {
			//	logWriter.write(newLine);
			//}
			logWriter.flush();
		}
		catch (IOException ex) {
			System.err.println("Error writing to log: " + ex);
		}
		lastLine = newLine;
	}

	/**
	 * Called when the game state has changed. Updates all attached
	 * <code>GameListener</code>s and the log.
	 */
	void update() {
		if (!updating) {
			return;
		}
		if (isLogging()) {
			updateLog();
		}
		for (GameListener listener : listeners) {
			listener.gameUpdate();
		}
	}

	/**
	 * Returns whether the current round has ended (after all answers were found or points
	 * were successfully or unsuccessully stolen).
	 * 
	 * @return The round status
	 */
	public boolean roundEnded() {
		return roundEnd;
	}

	/**
	 * Returns whether the currently selected team can steal the current round's points by
	 * answering correctly.
	 * 
	 * @return Whether points can be stolen
	 */
	public boolean isStealingPoints() {
		return stealingPoints;
	}

	/**
	 * Sets whether the currently selected team can steal the current round's points by
	 * answering correctly.
	 * 
	 * @param stealingPoints
	 *            Whether points can be stolen
	 */
	public void setStealingPoints(boolean stealingPoints) {
		this.stealingPoints = stealingPoints;
		update();
	}

	/**
	 * 
	 * Updates the current round's accumulated score.
	 */
	private void updateCurrentScore() {
		Question curr = currentQuestion();
		int score = 0;
		for (int i = 0; i < Game.MAX_ANSWERS; i++) {
			if (curr.answer(i).isRevealed()) {
				score += curr.answerScore(i);
			}
		}
		currentScore = score;
	}

	/**
	 * 
	 * Ends the current round and updates the teams' scores.
	 * 
	 * @param stealSuccess
	 *            Whether points have successfully been stolen
	 */
	private void endRound(boolean stealSuccess) {
		Question curr = currentQuestion();
		int revealedanswers = 0;
		for (int i = 0; i < Game.MAX_ANSWERS; i++) {
			if (curr.answer(i).isRevealed()) {
				revealedanswers++;
			}
		}
		// Gesamtpunkte updaten
		boolean allAnswers = revealedanswers == numberOfAnswers();
		if (stealingPoints || allAnswers) {
			int updScore = totalCurrentScore();
			// Punkteklau erfolgreich oder alle Antworten => aktuelles Team erhält Punkte
			if (stealSuccess || allAnswers) {
				if (currentTeam == 1) {
					team1Score += updScore;
				}
				else if (currentTeam == 2) {
					team2Score += updScore;
				}
			}
			// Punkteklau fehlgeschlagen => anderes Team erhält Punkte
			else {
				if (currentTeam == 1) {
					team2Score += updScore;
				}
				else if (currentTeam == 2) {
					team1Score += updScore;
				}
			}
			currentTeam = -1;
			roundEnd = true;
			stealingPoints = false;
		}
		update();
	}

	/**
	 * A <code>GameException</code> is thrown when a game-specific error occurs, e.g. the
	 * question file cannot be loaded or is invalid.
	 * 
	 * @author Simon May
	 * 
	 */
	public static class GameException extends Exception {

		private static final long serialVersionUID = 1L;

		public GameException(String message) {
			super(message);
		}

		public GameException(String message, Throwable cause) {
			super(message, cause);
		}

		public GameException(Throwable cause) {
			super(cause);
		}

	}

}
