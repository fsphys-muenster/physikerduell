package de.uni_muenster.physikerduell;

/**
 * A class implementing the GameListener interface can be registered to a Game instance to
 * receive events when the game state changes.
 * 
 * @author Simon May
 * 
 */
public interface GameListener {

	/**
	 * Called by the Game instance when the game state changes.
	 */
	public void gameUpdate();

}
