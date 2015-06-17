package de.uni_muenster.physikerduell.ui;

import java.io.IOException;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import org.newdawn.easyogg.OggClip;

public class Sound {
	// XXX
	//HashMap<Player> mp3Sounds;
	//HashMap<OggClip> oggSounds;

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
		return p;
	}

	/**
	 * 
	 * Plays the specified Ogg Vorbis audio file (using EasyOgg).
	 * 
	 * @param name
	 *            Audio file name (in the "res" directory)
	 */
	public static OggClip playOgg(String name) {
		OggClip ogg = null;
		try {
			ogg = new OggClip(Sound.class.getResourceAsStream("/res/" + name));
			ogg.play();
		}
		catch (IOException ex) {
			System.err.println("Could not play Ogg sound: " + ex);
		}
		return ogg;
	}

}
