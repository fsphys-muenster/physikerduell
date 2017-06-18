package de.uni_muenster.physikerduell.ui;

import java.util.HashMap;
import kuusisto.tinysound.Music;
import kuusisto.tinysound.Sound;
import kuusisto.tinysound.TinySound;

public class GameSound {

	private static final HashMap<String, Sound> sounds = new HashMap<>();

	/**
	 * 
	 * Plays the specified music file (using TinySound).
	 * 
	 * @param name
	 *            Audio file name (resource in the “res” directory)
	 */
	public static Music playMusic(String name) {
		String path = "/res/" + name;
		Music music = TinySound.loadMusic(path);
		music.play(false);
		return music;
	}

	/**
	 * 
	 * Plays the specified sound effect (using TinySound).
	 * 
	 * @param name
	 *            Audio file name (resource in the “res” directory)
	 */
	public static Sound playSound(String name) {
		String path = "/res/" + name;
		Sound sound = sounds.get(path);
		if (sound == null) {
			sound = TinySound.loadSound(path);
			sounds.put(path, sound);
		}
		sound.play();
		return sound;
	}

}
