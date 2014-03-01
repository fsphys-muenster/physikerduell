package de.uni_muenster.physikerduell.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javazoom.jl.player.PlayerApplet;
import de.uni_muenster.physikerduell.game.Game;
import de.uni_muenster.physikerduell.game.GameListener;
import de.uni_muenster.physikerduell.game.Question;

/**
 * Die eigentliche Spielanzeige des Physikerduells der Fachschaft Physik an der WWU <br>
 * Münster 12.06.2013
 * 
 * @author Lutz Althüser
 * @author Simon May
 */
public class Display extends JFrame implements GameListener {

	private static final long serialVersionUID = 1L;
	private JPanel contentPaneLabel;
	private ImagePanel contentPane;
	private JTextField txtFTeam1;
	private JTextField txtFTeam2;
	private JLabel lblFrageZ1;
	private JLabel lblAntwort1;
	private JLabel lblAntwort2;
	private JLabel lblAntwort3;
	private JLabel lblAntwort4;
	private JLabel lblAntwort5;
	private JLabel lblAntwort6;
	private JLabel lblPunkte1;
	private JLabel lblPunkte2;
	private JLabel lblPunkte3;
	private JLabel lblPunkte4;
	private JLabel lblPunkte5;
	private JLabel lblPunkte6;
	private JLabel lblFrageZ2;
	private JLabel lblSumme;
	private JLabel lblLeben;
	private JLabel lblTeam1;
	private JLabel lblTeam2;
	private JPanel outerPanel;
	private JPanel outerPanelLabel;
	private PlayerApplet soundplayer;
	private Image team1;
	private Image team2;
	private Image noteam;
	private Boolean pause = true;
	private Boolean musikPlayer = false;
	private Game duell;

	/**
	 * (Automatisch generiert) Hilfsmethode des Konstruktors. Erzeugt die Elemente der
	 * GUI.
	 */
	private void initializeUI() {
		contentPane = new ImagePanel();
		setIconImage(Toolkit.getDefaultToolkit().getImage(
			getClass().getResource("/res/Physikerduell-0.png")));
		contentPane.setImage(Toolkit.getDefaultToolkit().getImage(
			getClass().getResource("/res/Physikerduell-1.png")));
		setBackground(Color.BLACK);
		setName("Anzeige");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		DisplayMode dm =
			GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
				.getDisplayMode();
		setBounds(100, 100, dm.getWidth(), dm.getHeight());
		setLocationRelativeTo(null);

		contentPane.setBackground(Color.BLACK);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setPreferredSize(new Dimension(1024, 768));
		contentPaneLabel = new JPanel();
		contentPaneLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPaneLabel.setLayout(null);
		contentPaneLabel.setPreferredSize(new Dimension(1024, 768));
		contentPane.setLayout(null);

		outerPanelLabel = new JPanel(new GridBagLayout());
		outerPanelLabel.setBackground(Color.BLACK);
		GridBagConstraints gbcLabel = new GridBagConstraints();
		outerPanelLabel.add(contentPaneLabel, gbcLabel);

		GridBagLayout gbl_outerPanel = new GridBagLayout();
		gbl_outerPanel.columnWeights = new double[] {0.0};
		outerPanel = new JPanel(gbl_outerPanel);
		outerPanel.setBackground(Color.BLACK);
		GridBagConstraints gbc = new GridBagConstraints();
		outerPanel.add(contentPane, gbc);

		JLabel lab =
			new JLabel(new ImageIcon(getClass().getResource("/res/Physikerduell-0.png")));
		lab.setBounds(0, 0, 1024, 768);
		lab.setVisible(true);
		contentPaneLabel.add(lab);

		lblTeam1 = new JLabel("Team1");
		lblTeam1.setFont(new Font("Tahoma", Font.PLAIN, 36));
		lblTeam1.setHorizontalAlignment(SwingConstants.CENTER);
		lblTeam1.setForeground(Color.WHITE);
		lblTeam1.setBounds(0, 520, 300, 45);
		contentPane.add(lblTeam1);

		lblFrageZ1 = new JLabel("Frage Zeile 1 Frage Zeile 1 Frage Zeile 1");
		lblFrageZ1.setHorizontalAlignment(SwingConstants.CENTER);
		lblFrageZ1.setForeground(Color.WHITE);
		lblFrageZ1.setFont(new Font("Tahoma", Font.PLAIN, 36));
		lblFrageZ1.setBounds(0, 15, 1000, 45);
		contentPane.add(lblFrageZ1);

		lblAntwort1 = new JLabel("Antwort 1");
		lblAntwort1.setBackground(SystemColor.menu);
		lblAntwort1.setHorizontalAlignment(SwingConstants.CENTER);
		lblAntwort1.setFont(new Font("Tahoma", Font.PLAIN, 24));
		lblAntwort1.setForeground(Color.YELLOW);
		lblAntwort1.setBounds(10, 134, 700, 37);
		contentPane.add(lblAntwort1);

		lblPunkte1 = new JLabel("67");
		lblPunkte1.setHorizontalAlignment(SwingConstants.CENTER);
		lblPunkte1.setFont(new Font("Tahoma", Font.PLAIN, 24));
		lblPunkte1.setForeground(Color.YELLOW);
		lblPunkte1.setBounds(685, 134, 190, 37);
		contentPane.add(lblPunkte1);

		lblAntwort2 = new JLabel("Antwort 2");
		lblAntwort2.setHorizontalAlignment(SwingConstants.CENTER);
		lblAntwort2.setForeground(Color.YELLOW);
		lblAntwort2.setFont(new Font("Tahoma", Font.PLAIN, 24));
		lblAntwort2.setBounds(10, 182, 700, 37);
		contentPane.add(lblAntwort2);

		lblPunkte2 = new JLabel("67");
		lblPunkte2.setHorizontalAlignment(SwingConstants.CENTER);
		lblPunkte2.setForeground(Color.YELLOW);
		lblPunkte2.setFont(new Font("Tahoma", Font.PLAIN, 24));
		lblPunkte2.setBounds(685, 182, 190, 37);
		contentPane.add(lblPunkte2);

		lblAntwort4 = new JLabel("Antwort 4");
		lblAntwort4.setHorizontalAlignment(SwingConstants.CENTER);
		lblAntwort4.setForeground(Color.YELLOW);
		lblAntwort4.setFont(new Font("Tahoma", Font.PLAIN, 24));
		lblAntwort4.setBounds(10, 278, 700, 37);
		contentPane.add(lblAntwort4);

		lblPunkte4 = new JLabel("67");
		lblPunkte4.setHorizontalAlignment(SwingConstants.CENTER);
		lblPunkte4.setForeground(Color.YELLOW);
		lblPunkte4.setFont(new Font("Tahoma", Font.PLAIN, 24));
		lblPunkte4.setBounds(685, 278, 190, 37);
		contentPane.add(lblPunkte4);

		lblPunkte3 = new JLabel("67");
		lblPunkte3.setHorizontalAlignment(SwingConstants.CENTER);
		lblPunkte3.setForeground(Color.YELLOW);
		lblPunkte3.setFont(new Font("Tahoma", Font.PLAIN, 24));
		lblPunkte3.setBounds(685, 230, 190, 37);
		contentPane.add(lblPunkte3);

		lblAntwort6 = new JLabel("Antwort 6");
		lblAntwort6.setHorizontalAlignment(SwingConstants.CENTER);
		lblAntwort6.setForeground(Color.YELLOW);
		lblAntwort6.setFont(new Font("Tahoma", Font.PLAIN, 24));
		lblAntwort6.setBounds(10, 374, 700, 37);
		contentPane.add(lblAntwort6);

		lblAntwort5 = new JLabel("Antwort 5");
		lblAntwort5.setHorizontalAlignment(SwingConstants.CENTER);
		lblAntwort5.setForeground(Color.YELLOW);
		lblAntwort5.setFont(new Font("Tahoma", Font.PLAIN, 24));
		lblAntwort5.setBounds(10, 326, 700, 37);
		contentPane.add(lblAntwort5);

		lblTeam2 = new JLabel("Team2");
		lblTeam2.setBounds(724, 520, 300, 45);
		contentPane.add(lblTeam2);
		lblTeam2.setHorizontalAlignment(SwingConstants.CENTER);
		lblTeam2.setForeground(Color.WHITE);
		lblTeam2.setFont(new Font("Tahoma", Font.PLAIN, 36));

		lblPunkte6 = new JLabel("67");
		lblPunkte6.setHorizontalAlignment(SwingConstants.CENTER);
		lblPunkte6.setForeground(Color.YELLOW);
		lblPunkte6.setFont(new Font("Tahoma", Font.PLAIN, 24));
		lblPunkte6.setBounds(685, 374, 190, 37);
		contentPane.add(lblPunkte6);

		lblPunkte5 = new JLabel("67");
		lblPunkte5.setHorizontalAlignment(SwingConstants.CENTER);
		lblPunkte5.setForeground(Color.YELLOW);
		lblPunkte5.setFont(new Font("Tahoma", Font.PLAIN, 24));
		lblPunkte5.setBounds(685, 326, 190, 37);
		contentPane.add(lblPunkte5);

		lblSumme = new JLabel("Punkte");
		lblSumme.setHorizontalAlignment(SwingConstants.CENTER);
		lblSumme.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lblSumme.setForeground(Color.YELLOW);
		lblSumme.setBounds(685, 420, 190, 45);
		contentPane.add(lblSumme);

		JLabel lbltxtPunkte = new JLabel("Summe:");
		lbltxtPunkte.setHorizontalAlignment(SwingConstants.CENTER);
		lbltxtPunkte.setForeground(Color.YELLOW);
		lbltxtPunkte.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lbltxtPunkte.setBounds(600, 420, 120, 45);
		contentPane.add(lbltxtPunkte);

		lblLeben = new JLabel("Leben");
		lblLeben.setHorizontalAlignment(SwingConstants.CENTER);
		lblLeben.setForeground(Color.YELLOW);
		lblLeben.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lblLeben.setBounds(452, 580, 120, 45);
		contentPane.add(lblLeben);

		txtFTeam2 = new JTextField();
		txtFTeam2.setText("123");
		txtFTeam2.setHorizontalAlignment(SwingConstants.CENTER);
		txtFTeam2.setForeground(Color.YELLOW);
		txtFTeam2.setFont(new Font("Tahoma", Font.PLAIN, 30));
		txtFTeam2.setColumns(10);
		txtFTeam2.setFocusable(false);
		txtFTeam2.setBackground(Color.DARK_GRAY);
		txtFTeam2.setBounds(831, 580, 90, 40);
		contentPane.add(txtFTeam2);

		txtFTeam1 = new JTextField();
		txtFTeam1.setForeground(Color.YELLOW);
		txtFTeam1.setBackground(Color.DARK_GRAY);
		txtFTeam1.setText("123");
		txtFTeam1.setFocusable(false);
		txtFTeam1.setHorizontalAlignment(SwingConstants.CENTER);
		txtFTeam1.setFont(new Font("Tahoma", Font.PLAIN, 30));
		txtFTeam1.setBounds(105, 580, 90, 40);
		contentPane.add(txtFTeam1);
		txtFTeam1.setColumns(10);

		lblAntwort3 = new JLabel("Antwort 3");
		lblAntwort3.setBounds(10, 229, 700, 37);
		contentPane.add(lblAntwort3);
		lblAntwort3.setHorizontalAlignment(SwingConstants.CENTER);
		lblAntwort3.setForeground(Color.YELLOW);
		lblAntwort3.setFont(new Font("Tahoma", Font.PLAIN, 24));

		lblFrageZ2 = new JLabel("Frage Zeile 2 Frage Zeile 2 Frage Zeile 2");
		lblFrageZ2.setHorizontalAlignment(SwingConstants.CENTER);
		lblFrageZ2.setForeground(Color.WHITE);
		lblFrageZ2.setFont(new Font("Tahoma", Font.PLAIN, 36));
		lblFrageZ2.setBounds(0, 55, 1000, 45);
		contentPane.add(lblFrageZ2);

		//setContentPane(contentPaneLabel);
		setContentPane(outerPanelLabel);
	}

	/**
	 * Initialisieren des Anzeigefensters.
	 * 
	 * @param G
	 *            Ein Objekt vom Typ Game.
	 */
	public Display(Game G) {
		duell = G;
		soundplayer = new PlayerApplet();
		try {
			team1 = ImageIO.read(getClass().getResource("/res/Physikerduell-21.png"));
			team2 = ImageIO.read(getClass().getResource("/res/Physikerduell-22.png"));
			noteam = ImageIO.read(getClass().getResource("/res/Physikerduell-1.png"));
		}
		catch (IOException ex) {
			System.err.println("Error reading images: " + ex);
		}
		initializeUI();
	}

	/**
	 * Aktualisiert zum anderen die auf dem Anzeigefenster befindlichen Objekte. Es wird
	 * der aktuelle Spielzustand übernommen.
	 * 
	 * Zusätzlich wird ein <code>repaint</code> aufgerufen.
	 */
	@Override
	public void gameUpdate() {
		Question curr = duell.getCurrentQuestion();
		if (duell.getCurrentTeam() == 1) {
			contentPane.setImage(team1);
		}
		else if (duell.getCurrentTeam() == 2) {
			contentPane.setImage(team2);
		}
		else {
			contentPane.setImage(noteam);
		}
		contentPane.repaint();

		// Anzeige der Labels der Antworten
		showAnswerLabels();
		// Anzeigen der Antworten
		for (int i = 0; i < Game.MAX_ANSWERS; i++) {
			if (curr.getAnswer(i).isRevealed()) {
				revealAnswer(i);
			}
			else {
				showBlank(i);
			}
		}
		switch (duell.getCurrentLives()) {
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
		lblSumme.setText(String.valueOf(duell.getCurrentScore()));
		txtFTeam1.setText(String.valueOf(duell.getTeam1Score()));
		txtFTeam2.setText(String.valueOf(duell.getTeam2Score()));
		lblTeam1.setText(duell.getTeam1Name());
		lblTeam2.setText(duell.getTeam2Name());

		String currText = curr.getText();
		if (duell.getCurrentQuestionIndex() != 0) {
			if (currText.length() > 50) {
				int schnitt = 50;
				String sub = currText.substring(0, schnitt);
				if (sub.contains(" ")) {
					schnitt = sub.lastIndexOf(" ");
				}
				lblFrageZ1.setText(currText.substring(0, schnitt));
				lblFrageZ2.setText(currText.substring(schnitt));
			}
			else {
				lblFrageZ1.setText(currText);
				lblFrageZ2.setText("");
			}
		}
		else {
			lblFrageZ1.setText("Physikerduell");
			lblFrageZ2.setText("");
		}
		repaint();
	}

	/**
	 * Zeigt auf der Spielanzeige für ein mit dem Parameter bestimmte Antwortmöglichkeit
	 * nur die Maske für eine unbekannte Antwort an.
	 * 
	 * @param index
	 *            Der Index der Antwortmöglichkeit von 0 bis 5
	 */
	private void showBlank(int index) {
		JLabel antwort = (JLabel) getComponentByName("lblAntwort" + (index + 1));
		JLabel punkte = (JLabel) getComponentByName("lblPunkte" + (index + 1));
		antwort.setText("_______________________________");
		punkte.setText("____");
	}

	/**
	 * Returns the value of a field in this instance, specified by name (as a String).
	 * 
	 * @param name
	 *            The name of the field
	 * @return The field's value, or null if the field was not found or could not be
	 *         accessed.
	 */
	private Object getComponentByName(String name) {
		try {
			return getClass().getDeclaredField(name).get(this);
		}
		catch (Exception ex) {
			System.err.println("Could not get UI component: " + ex);
		}
		return null;
	}

	/**
	 * Zeigt die Antwort und Punkte einer Antwortmöglichkeit auf der Spielanzeige an.
	 * 
	 * @param index
	 *            Index der Antwortmöglichkeit von 0 bis 5.
	 */
	private void revealAnswer(int index) {
		Question curr = duell.getCurrentQuestion();
		JLabel antwort = (JLabel) getComponentByName("lblAntwort" + (index + 1));
		JLabel punkte = (JLabel) getComponentByName("lblPunkte" + (index + 1));
		antwort.setText(curr.getAnswerText(index));
		punkte.setText(String.valueOf(curr.getAnswerScore(index)));
	}

	/**
	 * Blendet die entsprechende Anzahl an Antwortmöglichkeiten ein. Dies geschieht
	 * abgestimmt auf die Rundenzahl.
	 */
	private void showAnswerLabels() {
		int numberOfAnswers = duell.numberOfAnswers();
		for (int i = 0; i < Game.MAX_ANSWERS; i++) {
			boolean visible = i < numberOfAnswers;
			JLabel antwort = (JLabel) getComponentByName("lblAntwort" + (i + 1));
			JLabel punkte = (JLabel) getComponentByName("lblPunkte" + (i + 1));
			antwort.setVisible(visible);
			punkte.setVisible(visible);
		}
	}

	/**
	 * Die Methode <code>playIntro</code> ist der Methode <code>resume</code> übergeordnet
	 * und spielt die Intromusik ab, bzw. wechselt von dem Pausenbild oder auch Startbild
	 * zur Spielanzeige.
	 */
	public void playIntro() {
		if (pause) {
			if (musikPlayer == false) {
				soundplayer.start(getClass().getResourceAsStream("/res/Intro.mp3"));
				musikPlayer = true;
			}
			else {
				resume();
				musikPlayer = false;
			}
		}
		else {
			soundplayer.stop();
			musikPlayer = false;
		}
	}

	/**
	 * Die Methode <code>playOutro</code> versetzt die Anzeige in ein Pausenbildschirm. Es
	 * wird abgeblendet.
	 */
	public void playOutro() {
		if (!pause) {
			pause();
		}
	}

	/**
	 * Methode zum Anzeigen des Pausenlabels.
	 */
	public void pause() {
		setContentPane(outerPanelLabel);
		outerPanelLabel.setVisible(true);
		outerPanelLabel.repaint();
		outerPanelLabel.revalidate();
		setVisible(true);
		pause = true;
	}

	/**
	 * Methode zum Anzeigen der Spielanzeige.
	 */
	public void resume() {
		setContentPane(outerPanel);
		outerPanel.setVisible(true);
		outerPanel.repaint();
		outerPanel.revalidate();
		setVisible(true);
		pause = false;
	}

	/**
	 * Die Klasse ImagePanel wird zum Verwalten der Anzeigen Spiel und Pause verwendet.
	 * 
	 * @author Lutz Althüser
	 * 
	 */
	private static class ImagePanel extends JPanel {

		private static final long serialVersionUID = 1L;
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
}