package de.uni_muenster.physikerduell.game;

import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class GameLog implements Closeable {
	/**
	 * File path for the log file.
	 */
	public static final String LOG_FILE_PATH = "physikerduell_log.txt";
	private final Game game;
	private final FileWriter logWriter;
	private String lastLine;

	public GameLog(Game game) throws IOException {
		this.game = game;
		File logFile = new File(LOG_FILE_PATH);
		// append if file exists
		logWriter = new FileWriter(logFile, true);
		String timeStamp =
			new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(Calendar.getInstance()
				.getTime());
		String newLine = "\n\n\n[" + timeStamp + "]\n----- Neues Spiel -----\n";
		logWriter.write(newLine);
		logWriter.flush();
	}

	@Override
	public void close() throws IOException {
		logWriter.close();
	}

	public void update() throws IOException {
		Question curr = game.currentQuestion();
		String team1Name = game.getTeam1Name();
		int team1Score = game.getTeam1Score();
		String team2Name = game.getTeam2Name();
		int team2Score = game.getTeam2Score();
		int currentScore = game.currentScore();
		int currentTeam = game.getCurrentTeam();
		int currentLives = game.getCurrentLives();
		int currentRound = game.getCurrentRound();
		String timeStamp =
			new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(Calendar.getInstance()
				.getTime());
		String newLine =
			String.format("[%s]\n%s: %d | %s: %d | CurrentScore:    %d"
				+ " | CurrentTeam:     %d | CurrentLives:    %d | CurrentRound:    %d"
				+ " | CurrentQuestion: %s\n", timeStamp, team1Name, team1Score,
				team2Name, team2Score, currentScore, currentTeam, currentLives,
				currentRound, curr.text());
		for (int i = 0; i < Game.MAX_ANSWERS; i++) {
			Answer ans = curr.answer(i);
			String revealed = ans.isRevealed() ? "X" : " ";
			newLine += revealed + " " + ans.text() + " [" + ans.score() + "]\n";
		}
		if (!newLine.equals(lastLine)) {
			logWriter.write("\n\n");
			logWriter.write(newLine);
		}
		logWriter.flush();
		lastLine = newLine;
	}

}
