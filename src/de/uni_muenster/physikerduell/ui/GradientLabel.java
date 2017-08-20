package de.uni_muenster.physikerduell.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import javax.swing.Icon;
import javax.swing.JLabel;

/**
 * @author Simon May
 * 
 */
public class GradientLabel extends JLabel {
	public GradientLabel() {
		super();
	}

	public GradientLabel(String text) {
		super(text);
	}

	public GradientLabel(Icon image) {
		super(image);
	}

	public GradientLabel(String text, int horizontalAlignment) {
		super(text, horizontalAlignment);
	}

	public GradientLabel(Icon image, int horizontalAlignment) {
		super(image, horizontalAlignment);
	}

	public GradientLabel(String text, Icon icon, int horizontalAlignment) {
		super(text, icon, horizontalAlignment);
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
			RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_ON);
		int width = getWidth();
		int height = getHeight();
		// semi-transparent red
		Color mainColor = new Color(102, 0, 0);
		Color fadeColor = Color.BLACK;
		RadialGradientPaint gp =
			new RadialGradientPaint(width / 2.f, height / 2.f, Math.max(width, height),
				new float[] {0.f, 1.f}, new Color[] {mainColor, fadeColor});
		g2d.setPaint(gp);
		g2d.fillOval(0, 0, width, height);
		super.paintComponent(g);
	}

}
