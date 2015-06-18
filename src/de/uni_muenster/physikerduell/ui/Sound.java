package de.uni_muenster.physikerduell.ui;

import java.io.IOException;
import java.util.HashMap;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import org.newdawn.easyogg.OggClip;

public class Sound {

	private static final HashMap<String, OggClip> oggSounds = new HashMap<>();

	/**
	 * 
	 * Plays the specified MP3 audio file (using JLayer).
	 * 
	 * @param name
	 *            Audio file name (in the "res" directory)
	 */
	public static Player playMP3(String name) {
		String resource = "/res/" + name;
		Player p = null;
		try {
			p = new Player(Sound.class.getResourceAsStream(resource));
		}
		catch (JavaLayerException ex) {
			System.err.println("Could not create MP3 sound player: " + ex);
		}
		if (p != null) {
			playMP3(p);
		}
		return p;
	}

	public static void playMP3(Player p) {
		final Player pl = p;
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					pl.play();
				}
				catch (JavaLayerException ex) {
					System.err.println("Could not play MP3 sound: " + ex);
				}
			}
		}).start();
	}

	/**
	 * 
	 * Plays the specified Ogg Vorbis audio file (using EasyOgg).
	 * 
	 * @param name
	 *            Audio file name (in the "res" directory)
	 * @param allowMultiple
	 *            Determines if the same sound file can be played multiple times
	 *            at the same time
	 */
	public static OggClip playOgg(String name, boolean allowMultiple) {
		String path = "/res/" + name;
		OggClip ogg = oggSounds.get(path);
		if (ogg == null) {
			try {
				ogg = new OggClip(Sound.class.getResourceAsStream(path));
				oggSounds.put(path, ogg);
				ogg.play();
			}
			catch (IOException ex) {
				System.err.println("Could not play Ogg sound: " + ex);
			}
		}
		else if (ogg.stopped() || allowMultiple) {
			ogg.play();
		}
		return ogg;
	}

	/**
	 * 
	 * Plays the specified Ogg Vorbis audio file (using EasyOgg).
	 * 
	 * @param name
	 *            Audio file name (in the "res" directory)
	 */
	public static OggClip playOgg(String name) {
		return playOgg(name, false);
	}

}
