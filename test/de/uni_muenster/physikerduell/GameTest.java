package de.uni_muenster.physikerduell;

import static org.junit.Assert.assertEquals;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import de.uni_muenster.physikerduell.game.Answer;
import de.uni_muenster.physikerduell.game.Game;
import de.uni_muenster.physikerduell.game.Game.RoundState;
import de.uni_muenster.physikerduell.game.GameException;
import de.uni_muenster.physikerduell.game.Question;

public class GameTest {
	
	@Rule
	public ExpectedException except = ExpectedException.none();

	@Test
	public void testSetCurrentQuestionIndex() throws GameException, IOException {
		Game g;
		// switch question during buzzer round
		g = createGame();
		g.setCurrentRound(3);
		g.setCurrentQuestionIndex(1);
		assertEquals(RoundState.BUZZER, g.getRoundState());
		assertEquals(Game.NO_TEAM, g.getCurrentTeam());
		// switch question during normal round
		g = createGame();
		g.setCurrentTeam(1);
		g.setRoundState(RoundState.NORMAL);
		g.correctAnswer(1);
		g.setCurrentRound(3);
		g.setCurrentQuestionIndex(1);
		assertEquals(RoundState.NORMAL, g.getRoundState());
		assertEquals(Game.NO_TEAM, g.getCurrentTeam());
		assertEquals(0, g.currentScore());
		assertEquals(0, g.getTeam1Score());
		assertEquals(0, g.getTeam2Score());
		// switch question after round has ended
		g = createGame();
		g.setCurrentRound(3);
		g.setRoundState(RoundState.NORMAL);
		g.setCurrentTeam(1);
		g.correctAnswer(2);
		g.setRoundState(RoundState.STEALING_POINTS);
		g.correctAnswer(1);
		assertEquals(RoundState.ROUND_ENDED, g.getRoundState());
		g.setCurrentQuestionIndex(1);
		assertEquals(RoundState.BUZZER, g.getRoundState());
		assertEquals(4, g.getCurrentRound());
		assertEquals(Game.NO_TEAM, g.getCurrentTeam());
		assertEquals(0, g.currentScore());
		assertEquals(61 * 2, g.getTeam1Score());
		assertEquals(0, g.getTeam2Score());
		// change question after wrong answer in buzzer mode
		g = createGame();
		g.setCurrentTeam(1);
		g.wrongAnswer();
		assertEquals(2, g.getCurrentTeam());
		g.setCurrentRound(3);
		g.setCurrentQuestionIndex(1);
		assertEquals(RoundState.BUZZER, g.getRoundState());
		assertEquals(Game.NO_TEAM, g.getCurrentTeam());
		g.setCurrentTeam(1);
		g.wrongAnswer();
		assertEquals(2, g.getCurrentTeam());
		g.setCurrentQuestionIndex(0);
		assertEquals(Game.NO_TEAM, g.getCurrentTeam());
	}
	
	@Test
	public void testAnswer() throws GameException, IOException {
		testAnswerBUZZER();
		testAnswerNORMAL();
		testAnswerSTEALING_POINTS();
		// general tests
		Game g = createGame();
		except.expect(IllegalStateException.class);
		g.wrongAnswer();
	}
	
	private void testAnswerBUZZER() throws GameException, IOException {
		// Test Buzzer state
		Game g;
		// Scenario 1: First team gets top answer
		g = createGame();
		g.setRoundState(RoundState.BUZZER);
		g.setCurrentTeam(1);
		g.correctAnswer(0);
		assertEquals(RoundState.NORMAL, g.getRoundState());
		assertEquals(1, g.getCurrentTeam());
		assertEquals(81, g.currentScore());
		assertEquals(0, g.getTeam1Score());
		assertEquals(0, g.getTeam2Score());
		// Scenario 2: First team answers correctly (but not top), second incorrectly;
		// also: further incorrect answers at that point should have no effect
		g = createGame();
		g.setRoundState(RoundState.BUZZER);
		g.setCurrentTeam(1);
		g.correctAnswer(2);
		g.wrongAnswer();
		g.wrongAnswer();
		assertEquals(RoundState.NORMAL, g.getRoundState());
		assertEquals(1, g.getCurrentTeam());
		assertEquals(61, g.currentScore());
		assertEquals(0, g.getTeam1Score());
		assertEquals(0, g.getTeam2Score());
		// Scenario 3: Second team has better answer than first team
		g = createGame();
		g.setRoundState(RoundState.BUZZER);
		g.setCurrentTeam(1);
		g.correctAnswer(2);
		g.correctAnswer(1);
		assertEquals(RoundState.NORMAL, g.getRoundState());
		assertEquals(2, g.getCurrentTeam());
		assertEquals(71 + 61, g.currentScore());
		assertEquals(0, g.getTeam1Score());
		assertEquals(0, g.getTeam2Score());
		// Scenario 4: Team 2 starts; both teams give wrong answers;
		// team 1 gives first correct answer => team 1 plays
		g = createGame();
		g.setRoundState(RoundState.BUZZER);
		g.setCurrentTeam(2);
		g.wrongAnswer();
		g.wrongAnswer();
		g.wrongAnswer();
		g.correctAnswer(3);
		assertEquals(RoundState.NORMAL, g.getRoundState());
		assertEquals(1, g.getCurrentTeam());
		assertEquals(51, g.currentScore());
		assertEquals(0, g.getTeam1Score());
		assertEquals(0, g.getTeam2Score());
	}
	
	private void testAnswerNORMAL() throws GameException, IOException {
		// Test Normal state
		Game g;
		// Scenario 1: A team gets all answers correct
		g = createGame();
		g.setRoundState(RoundState.NORMAL);
		g.setCurrentTeam(2);
		g.correctAnswer(0);
		g.correctAnswer(2);
		g.correctAnswer(1);
		g.correctAnswer(4);
		g.correctAnswer(5);
		g.correctAnswer(3);
		assertEquals(RoundState.ROUND_ENDED, g.getRoundState());
		assertEquals(Game.NO_TEAM, g.getCurrentTeam());
		assertEquals(81 + 71 + 61 + 51 + 41 + 31, g.currentScore());
		assertEquals(0, g.getTeam1Score());
		assertEquals(81 + 71 + 61 + 51 + 41 + 31, g.getTeam2Score());
		// Scenario 2: A team gets some answers and the other team fails to steal
		g = createGame();
		g.setRoundState(RoundState.NORMAL);
		g.setCurrentTeam(1);
		g.correctAnswer(2);
		g.wrongAnswer();
		g.correctAnswer(0);
		g.correctAnswer(5);
		g.wrongAnswer();
		g.wrongAnswer();
		assertEquals(RoundState.STEALING_POINTS, g.getRoundState());
		assertEquals(2, g.getCurrentTeam());
		assertEquals(81 + 61 + 31, g.currentScore());
		assertEquals(0, g.getTeam1Score());
		assertEquals(0, g.getTeam2Score());
		g.wrongAnswer();
		assertEquals(RoundState.ROUND_ENDED, g.getRoundState());
		assertEquals(Game.NO_TEAM, g.getCurrentTeam());
		assertEquals(81 + 61 + 31, g.currentScore());
		assertEquals(81 + 61 + 31, g.getTeam1Score());
		assertEquals(0, g.getTeam2Score());
		// Scenario 3: Stealing points successfully
		g = createGame();
		g.setRoundState(RoundState.NORMAL);
		g.setCurrentTeam(1);
		g.wrongAnswer();
		g.correctAnswer(3);
		g.wrongAnswer();
		g.correctAnswer(1);
		g.correctAnswer(5);
		g.wrongAnswer();
		assertEquals(RoundState.STEALING_POINTS, g.getRoundState());
		assertEquals(2, g.getCurrentTeam());
		assertEquals(71 + 51 + 31, g.currentScore());
		assertEquals(0, g.getTeam1Score());
		assertEquals(0, g.getTeam2Score());
		g.correctAnswer(2);
		assertEquals(RoundState.ROUND_ENDED, g.getRoundState());
		assertEquals(Game.NO_TEAM, g.getCurrentTeam());
		assertEquals(71 + 51 + 61 + 31, g.currentScore());
		assertEquals(0, g.getTeam1Score());
		assertEquals(71 + 51 + 31, g.getTeam2Score());
	}
	
	private void testAnswerSTEALING_POINTS() throws GameException, IOException {
		// Test Stealing state
		Game g;
		// Scenario 1: Failing to steal
		g = createGame();
		g.setCurrentRound(3);
		g.setRoundState(RoundState.NORMAL);
		g.setCurrentQuestionIndex(1);
		g.setCurrentTeam(1);
		g.correctAnswer(1);
		g.setRoundState(RoundState.STEALING_POINTS);
		g.setCurrentTeam(2);
		g.setCurrentTeam(1);
		g.wrongAnswer();
		assertEquals(RoundState.ROUND_ENDED, g.getRoundState());
		assertEquals(Game.NO_TEAM, g.getCurrentTeam());
		assertEquals(91, g.currentScore());
		assertEquals(0, g.getTeam1Score());
		assertEquals(91 * 2, g.getTeam2Score());
		// Scenario 2: Stealing points successfully (not last round)
		g = createGame();
		g.setCurrentRound(3);
		g.setRoundState(RoundState.NORMAL);
		g.setCurrentTeam(1);
		g.setCurrentQuestionIndex(1);
		g.setCurrentTeam(1);
		g.correctAnswer(2);
		g.correctAnswer(1);
		g.setCurrentTeam(2);
		g.setRoundState(RoundState.STEALING_POINTS);
		g.correctAnswer(3);
		assertEquals(RoundState.ROUND_ENDED, g.getRoundState());
		assertEquals(Game.NO_TEAM, g.getCurrentTeam());
		assertEquals(91 + 44 + 32, g.currentScore());
		assertEquals(0, g.getTeam1Score());
		assertEquals((91 + 44) * 2, g.getTeam2Score());
		// Scenario 3: Stealing points successfully (last round)
		g = createGame();
		g.setCurrentRound(5);
		g.setRoundState(RoundState.NORMAL);
		g.setCurrentTeam(1);
		g.setCurrentQuestionIndex(1);
		g.setCurrentTeam(1);
		g.correctAnswer(2);
		g.correctAnswer(1);
		g.setCurrentTeam(2);
		g.setRoundState(RoundState.STEALING_POINTS);
		g.correctAnswer(4);
		assertEquals(5, g.getCurrentRound());
		assertEquals(RoundState.ROUND_ENDED, g.getRoundState());
		assertEquals(Game.NO_TEAM, g.getCurrentTeam());
		assertEquals(91 + 44 + 12, g.currentScore());
		assertEquals(0, g.getTeam1Score());
		assertEquals((91 + 44 + 12) * 3, g.getTeam2Score());
	}

	@Test
	public void testGetQuestion() throws GameException, IOException {
		Game g = createGame();
		List<Answer> answers = new ArrayList<>();
		for (int i = 1; i < 9; i++) {
			answers.add(new Answer("Antwort " + i, i * 10 + 1));
		}
		// check if first test question is OK
		Question testQuestion = new Question("Frage A", answers);
		assertEquals(testQuestion, g.getQuestion(0));
		answers.clear();
		answers.add(new Answer("Antwort I", 12));
		answers.add(new Answer("Antwort ii", 44));
		answers.add(new Answer("Antwort iii", 32));
		answers.add(new Answer("Ant, wort,,", 91));
		answers.add(new Answer(", ,p, \"un\"kte, \"s", 734));
		// check if question stays OK when the list of answers used to create
		// it is modified
		assertEquals(testQuestion, g.getQuestion(0));
		// check if second test question is OK
		testQuestion = new Question("Frage B", answers);
		assertEquals(testQuestion, g.getQuestion(1));
		answers.clear();
		answers.add(new Answer("Antwort", 10));
		// check if third test question is OK
		testQuestion = new Question("Frage C", answers);
		assertEquals(testQuestion, g.getQuestion(2));
		// check if IndexOutOfBoundsException is thrown
		testGetQuestionException(g, 3);
	}
	
	private void testGetQuestionException(Game g, int questionIdx) {
		except.expect(IndexOutOfBoundsException.class);
		g.getQuestion(questionIdx);
	}
	
	private Game createGame() throws GameException, IOException {
		return new Game(Files.newInputStream(Paths.get("test/game_test.csv")));
	}
	
}
