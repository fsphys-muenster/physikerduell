package de.uni_muenster.physikerduell.game;

/**
 * A <code>GameException</code> is thrown when a game-specific error occurs, e.g. the
 * question file cannot be loaded or is invalid.
 * 
 * @author Simon May
 * 
 */
public class GameException extends Exception {

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
