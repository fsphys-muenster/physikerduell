package de.uni_muenster.physikerduell.ui;

import javax.swing.JLabel;
import javax.swing.SwingWorker;
import de.uni_muenster.physikerduell.game.Game;

public class LivesDisplay {
	
	// Time to delay after lives have changed (in ms)
	public final int WAIT_TIME = 2000;
	private final Game game;
	private final JLabel lblLeben;
	private boolean blockUpdates;
	private int currentLives;
	
	public LivesDisplay(Game game, JLabel lblLeben) {
		this.game = game;
		this.lblLeben = lblLeben;
	}
	
	public void update() {
		int newLives = game.getCurrentLives();
		if (newLives == currentLives) {
			return;
		}
		currentLives = newLives;
		if (!blockUpdates) {
			blockUpdates = true;
			new SwingWorker<Object, Object>() {
				@Override
				protected Object doInBackground() {
					try {
						Thread.sleep(WAIT_TIME);
					}
					catch (InterruptedException ex) {
						Thread.currentThread().interrupt();
					}
					return null;
				}
				
				@Override
				protected void done() {
					blockUpdates = false;
					updateLabel();
				}
			}.execute();
			updateLabel();
		}
	}
	
	private void updateLabel() {
		switch (currentLives) {
		case 0:
			lblLeben.setText("XXX");
			break;
		case 1:
			lblLeben.setText("XX ");
			break;
		case 2:
			lblLeben.setText("X  ");
			break;
		case 3:
			lblLeben.setText("   ");
			break;
		}
	}

}
