package de.uni_muenster.physikerduell;

import static org.junit.Assert.assertEquals;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import de.uni_muenster.physikerduell.game.Answer;
import de.uni_muenster.physikerduell.game.Game;
import de.uni_muenster.physikerduell.game.Question;
import de.uni_muenster.physikerduell.game.Game.GameException;

public class GameTest {

	@Test
	public void testGetQuestion() throws GameException, IOException {
		Game g = new Game(Files.newInputStream(Paths.get("game test.csv")));
		List<Answer> answers = new ArrayList<>();
		for (int i = 1; i < 9; i++) {
			answers.add(new Answer("Antwort " + i, i * 10 + 1));
		}
		assertEquals(g.getQuestion(0), new Question("Frage A", answers));
		answers.clear();
		answers.add(new Answer("Antwort I", 12));
		answers.add(new Answer("Antwort ii", 44));
		answers.add(new Answer("Antwort iii", 32));
		answers.add(new Answer("Ant, wort,,", 91));
		answers.add(new Answer(", ,p, \"un\"kte, \"s", 734));
	}
}
