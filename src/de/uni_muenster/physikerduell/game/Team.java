package de.uni_muenster.physikerduell.game;

/**
 * XXX Klasse bisher nicht benutzt
 */
public class Team {
	
	private int score;
	private String name;
	
	public String name() {
		return name;
	}
	protected void setName(String name) {
		if (name == null) {
			throw new IllegalArgumentException("Team name cannot be null!");
		}
		this.name = name;
	}
	
	public int score() {
		return score;
	}
	protected void setScore(int score) {
		this.score = score;
	}

}
