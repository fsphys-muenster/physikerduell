package de.uni_muenster.physikerduell.game;

import static de.uni_muenster.physikerduell.game.Game.RoundState.BUZZER;
import static de.uni_muenster.physikerduell.game.Game.RoundState.NORMAL;
import static de.uni_muenster.physikerduell.game.Game.RoundState.ROUND_ENDED;
import static de.uni_muenster.physikerduell.game.Game.RoundState.STEALING_POINTS;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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
	 * Value signifying that no team is selected.
	 */
	public static final int NO_TEAM = -1;
	private final List<GameListener> listeners = new ArrayList<>();
	private final List<Question> questions = new ArrayList<>();
	private String team1Name = "Team 1";
	private String team2Name = "Team 2";
	private int team1Score;
	private int team2Score;
	private int currentLives = MAX_LIVES;
	private int currentRound = 1;
	private int currentScore;
	private int currentQuestionIndex;
	private int currentTeam = NO_TEAM;
	private RoundState state = RoundState.BUZZER;
	// Team which started the last buzzer round
	private int teamStartedBuzzer = NO_TEAM;
	private boolean updating = true;
	private GameLog log;

	/**
	 * Creates the Game instance and reads the questions from the specified stream. The
	 * question file must be a CSV file with exactly two columns. Each question appears on
	 * its own row in the first column (with an empty second column) and the answers are
	 * given in the following rows with the answer text in the first and the score (a
	 * positive integer) in the second row. A question is terminated by a single row with
	 * empty columns or the end of the file.
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
		// If last line wasn't empty, the question still has to be added to the list
		if (!questionRow) {
			questions.add(new Question(questionText, answers));
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
	 *            &lt; questionCount())
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
		return log != null;
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
	
	private void checkAnswerGiven() {
		if (state == ROUND_ENDED) {
			throw new IllegalStateException("No answers possible in state ROUND_ENDED!");
		}
		if (currentTeam == NO_TEAM) {
			throw new IllegalStateException("Answer given, but no team selected!");
		}
		if (state == BUZZER && teamStartedBuzzer == NO_TEAM) {
			teamStartedBuzzer = currentTeam;
		}
	}

	/**
	 * Updates the game if a correct answer to the current question has been given.
	 * 
	 * @param index
	 *            The index of the correctly given answer to the current question
	 */
	public void correctAnswer(int index) {
		checkAnswerGiven();
		// index of the previously best revealed answer
		int best = highestRevealedAnswer();
		// update answer and score
		currentQuestion().answer(index).setRevealed(true);
		updateCurrentScore();
		if (state == BUZZER) {
			// if top answer or better than the previous revealed answer:
			// Current team plays
			if (index == 0 || index < best) {
				state = NORMAL;
			}
			// current team had a chance, but their answer was worse: other team plays
			else if (index > best && best != -1) {
				currentTeam = otherTeam();
				state = NORMAL;
			}
			else if (index == best) {
				throw new IllegalArgumentException("Answer " + (index + 1)
					+ " already revealed!");
			}
			// First correct answer after other team already gave a wrong one:
			// Current team plays
			else if (teamStartedBuzzer != currentTeam) {
				state = NORMAL;
			}
			// first correct answer, but not top answer: other team gets a
			// chance at the top answer
			else {
				currentTeam = otherTeam();
			}
		}
		// not buzzer mode
		// point steal successful or all answers found? Else normal correct answer
		else if (state == STEALING_POINTS || revealedAnswers() == numberOfAnswers()) {
			endRound(true, index);
		}
		update();
	}

	/**
	 * Returns the index of the currently revealed answer with the highest point value (=>
	 * lowest index).
	 * 
	 * @return the index of the best revealed answer
	 */
	private int highestRevealedAnswer() {
		Question curr = currentQuestion();
		int num = numberOfAnswers();
		for (int i = 0; i < num; i++) {
			if (curr.answer(i).isRevealed()) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Updates the game if an incorrect answer to the current question has been given.
	 */
	public void wrongAnswer() {
		checkAnswerGiven();
		// swap teams when given a wrong answer during buzzer mode
		if (state == BUZZER) {
			currentTeam = otherTeam();
			// if other team had already revealed an answer, it's their turn
			if (revealedAnswers() == 1) {
				setRoundState(NORMAL);
			}
		}
		// Not in buzzer mode:
		// decrease lives; if no lives: change team
		else if (currentLives > 1 && state != STEALING_POINTS) {
			currentLives--;
		}
		else if (state == STEALING_POINTS) {
			// points could not be stolen; update score
			endRound(false, -1);
		}
		// 3 lives lost
		else {
			currentLives = 0;
			state = STEALING_POINTS;
			update();
			currentLives = MAX_LIVES;
			currentTeam = otherTeam();
		}
		update();
	}

	private int otherTeam() {
		if (currentTeam == 1) {
			return 2;
		}
		else if (currentTeam == 2) {
			return 1;
		}
		else {
			return NO_TEAM;
		}
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
	 *            &ge; 0 and &lt; questionCount())
	 */
	public void setCurrentQuestionIndex(int currentQuestionIndex) {
		if (currentQuestionIndex < 0 || currentQuestionIndex >= questions.size()) {
			throw new IndexOutOfBoundsException("Invalid question index: " + currentQuestionIndex);
		}
		if (questions.get(currentQuestionIndex).answerCount() < numberOfAnswers()) {
			throw new IllegalArgumentException("Selected question does not have enough answers!");
		}
		if (currentQuestionIndex != this.currentQuestionIndex) {
			this.currentQuestionIndex = currentQuestionIndex;
			updating = false;
			// new question => no answer revealed
			currentScore = 0;
			currentQuestion().setRevealedAllAnswers(false);
			updating = true;
		}
		if (state == ROUND_ENDED) {
			currentRound++;
			if (currentRound > NUM_ROUNDS) {
				currentRound = NUM_ROUNDS;
			}
			currentLives = MAX_LIVES;
			state = BUZZER;
		}
		if (state != BUZZER) {
			state = NORMAL;
		}
		currentTeam = NO_TEAM;
		teamStartedBuzzer = NO_TEAM;
		update();
	}

	/**
	 * Sets the current round.
	 * 
	 * @param currentRound
	 *            The new current round (&ge; 1)
	 */
	public void setCurrentRound(int currentRound) {
		if (currentRound < 1) {
			throw new IllegalArgumentException("Invalid round number: " + currentRound);
		}
		this.currentRound = currentRound;
		teamStartedBuzzer = NO_TEAM;
		update();
	}

	/**
	 * Sets the current team.
	 * 
	 * @param teamNumber
	 *            The number of the new current team. Valid values are 1, 2 or
	 *            <code>NO_TEAM</code>
	 */
	public void setCurrentTeam(int teamNumber) {
		if (teamNumber != 1 && teamNumber != 2 && teamNumber != NO_TEAM) {
			throw new IllegalArgumentException(
				"Team Number has to be 1, 2 or NO_TEAM, was " + teamNumber);
		}
		this.currentTeam = teamNumber;
		update();
	}

	/**
	 * Activate or deactivate logging changes to the game to a file
	 * (<code>LOG_FILE_PATH</code>). If logging is activated, the log file is created in
	 * the current working directory. If it already exists, it is appended to.
	 * 
	 * @param logging
	 *            Activate (true) or deactivate (false) logging
	 */
	public void setLogging(boolean logging) {
		if (logging && log == null) {
			try {
				log = new GameLog(this);
			}
			catch (IOException ex) {
				log = null;
				System.err.println("Error starting log: " + ex);
			}
		}
		else if (!logging && log != null) {
			try {
				log.close();
			}
			catch (IOException ex) {
				System.err.println("Error closing log: " + ex);
			}
			log = null;
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
		if (isLogging()) {
			try {
				log.update();
			}
			catch (IOException ex) {
				System.err.println("Error writing to log: " + ex);
			}
		}
	}

	/**
	 * Called when the game state has changed. Updates all attached
	 * <code>GameListener</code>s and the log.
	 */
	void update() {
		if (!updating) {
			return;
		}
		updateCurrentScore();
		if (isLogging()) {
			updateLog();
		}
		for (GameListener listener : listeners) {
			listener.gameUpdate();
		}
	}

	/**
	 * 
	 * Updates the current round's accumulated score.
	 */
	private void updateCurrentScore() {
		Question curr = currentQuestion();
		int score = 0;
		for (Answer answer : curr.allAnswers()) {
			if (answer.isRevealed()) {
				score += answer.score();
			}
		}
		currentScore = score;
	}

	/**
	 * Returns the number of currently revealed answers.
	 * 
	 * @return The number of currently revealed answers
	 */
	private int revealedAnswers() {
		Question curr = currentQuestion();
		int numOfAnswers = numberOfAnswers();
		int revealedanswers = 0;
		for (int i = 0; i < numOfAnswers; i++) {
			if (curr.answer(i).isRevealed()) {
				revealedanswers++;
			}
		}
		return revealedanswers;
	}

	/**
	 * 
	 * Ends the current round and updates the teams' scores.
	 * 
	 * @param stealSuccess
	 *            Whether points have successfully been stolen
	 * @param answerIndex
	 *            Index of the answer that was correctly given
	 */
	private void endRound(boolean stealSuccess, int answerIndex) {
		boolean allAnswers = (revealedAnswers() == numberOfAnswers()) && state == NORMAL;
		if (state != STEALING_POINTS && !allAnswers) {
			throw new IllegalStateException("Round should not have ended at this point.");
		}
		int updScore = totalCurrentScore();
		// point steal successful or all answers => current team gets points
		if ((stealSuccess && currentRound == 5) || allAnswers) {
			if (currentTeam == 1) {
				team1Score += updScore;
			}
			else if (currentTeam == 2) {
				team2Score += updScore;
			}
		}
		// not last round: point steal does not award points of answered question
		else if (stealSuccess) {
			int answerPoints = currentQuestion().answerScore(answerIndex) * roundMultiplier();
			if (currentTeam == 1) {
				team1Score += updScore - answerPoints;
			}
			else if (currentTeam == 2) {
				team2Score += updScore - answerPoints;
			}
		}
		// point steal unsuccessful => other team gets points
		else {
			if (currentTeam == 1) {
				team2Score += updScore;
			}
			else if (currentTeam == 2) {
				team1Score += updScore;
			}
		}
		currentTeam = NO_TEAM;
		state = ROUND_ENDED;
		update();
	}

	/**
	 * Returns the current state of the round.
	 * 
	 * @return Whether current round state
	 */
	public RoundState getRoundState() {
		return state;
	}

	/**
	 * Sets the round state.
	 * 
	 * @param state
	 *            The new state of the round (cannot be <code>null</code> or
	 *            <code>ROUND_ENDED</code>)
	 */
	public void setRoundState(RoundState state) {
		if (state == null) {
			throw new IllegalArgumentException("Round state cannot be null!");
		}
		else if (state == ROUND_ENDED) {
			throw new IllegalArgumentException("Round state cannot be set to ROUND_ENDED!");
		}
		this.state = state;
		update();
	}

	/**
	 * Possible values for the current state of the round.
	 * 
	 * @author Simon May
	 * 
	 */
	public static enum RoundState {
		/**
		 * Buzzer mode, i.e. when a representative of each team is at the buzzer at the
		 * beginning of a round and it is not yet decided which team will play the round.
		 */
		BUZZER,
		/**
		 * Normal progress during a round.
		 */
		NORMAL,
		/**
		 * The currently selected team can steal the current round's points by answering
		 * correctly.
		 */
		STEALING_POINTS,
		/**
		 * The current round has ended (after all answers were found or points were
		 * successfully or unsuccessully stolen).
		 */
		ROUND_ENDED;
	}

}
