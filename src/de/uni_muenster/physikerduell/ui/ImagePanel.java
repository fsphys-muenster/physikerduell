package de.uni_muenster.physikerduell.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JPanel;

/**
 * Die Klasse ImagePanel wird zum Verwalten der Anzeigen Spiel und Pause verwendet.
 * 
 * @author Lutz Althüser
 * 
 */
public class ImagePanel extends JPanel {
	private Image img;

	public ImagePanel() {
		this(null);
	}

	public ImagePanel(Image img) {
		if (img != null) {
			setImage(img);
		}
		setLayout(null);
	}

	public void setImage(Image img) {
		this.img = img;
		Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
		setSize(size);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(img, 0, 0, this);
	}

}

