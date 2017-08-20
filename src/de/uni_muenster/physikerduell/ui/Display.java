package de.uni_muenster.physikerduell.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
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
import kuusisto.tinysound.Music;
import de.uni_muenster.physikerduell.game.Game;
import de.uni_muenster.physikerduell.game.Game.RoundState;
import de.uni_muenster.physikerduell.game.GameListener;
import de.uni_muenster.physikerduell.game.Question;

/**
 * Die eigentliche Spielanzeige des Physikerduells der Fachschaft Physik an der WWU Münster.
 * 
 * @author Lutz Althüser
 * @author Simon May
 */
public class Display extends JFrame implements GameListener {
	private static final int WINDOW_WIDTH = 1024;
	private static final int WINDOW_HEIGHT = 768;
	private static final Dimension WINDOW_SIZE = new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT);
	private JPanel contentPanePause;
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
	private JLabel lblMultiplikator;
	private JLabel lblPunkteklau1;
	private JLabel lblPunkteklau2;
	private Image team1;
	private Image team2;
	private Image noteam;
	private Music introMusic;
	private boolean pause = true;
	private Game game;
	private LivesDisplay ldLeben;
	
	public Display(int screen) {
		this(null, screen);
	}
	
	/**
	 * Initialisieren des Anzeigefensters.
	 * 
	 * @param game
	 *            Ein Objekt vom Typ Game.
	 */
	public Display(Game game, int screen) {
		setGame(game);
		try {
			team1 = ImageIO.read(getClass().getResource("/Physikerduell-21.png"));
			team2 = ImageIO.read(getClass().getResource("/Physikerduell-22.png"));
			noteam = ImageIO.read(getClass().getResource("/Physikerduell-1.png"));
		}
		catch (IOException ex) {
			System.err.println("Error loading images: " + ex);
		}
		initializeUI();
	}
	
	public void setGame(Game game) {
		if (this.game != null) {
			this.game.removeListener(this);
		}
		this.game = game;
		if (game != null) {
			ldLeben = new LivesDisplay(game, lblLeben);
			game.addListener(this);
			gameUpdate();
		}
	}

	/**
	 * Aktualisiert zum anderen die auf dem Anzeigefenster befindlichen Objekte. Es wird
	 * der aktuelle Spielzustand übernommen.
	 * 
	 * Zusätzlich wird ein <code>repaint</code> aufgerufen.
	 */
	@Override
	public void gameUpdate() {
		if (game == null) {
			return;
		}
		if (game.getCurrentTeam() == 1) {
			contentPane.setImage(team1);
		}
		else if (game.getCurrentTeam() == 2) {
			contentPane.setImage(team2);
		}
		else {
			contentPane.setImage(noteam);
		}
		contentPane.repaint();
		// Anzeige der Labels der Antworten
		showAnswerLabels();
		Question curr = game.currentQuestion();
		// Anzeigen der Antworten
		for (int i = 0; i < Game.MAX_ANSWERS; i++) {
			if (i < curr.answerCount() && curr.answer(i).isRevealed()) {
				revealAnswer(i);
			}
			else {
				showBlank(i);
			}
		}
		ldLeben.update();
		lblSumme.setText(String.valueOf(game.currentScore()));
		txtFTeam1.setText(String.valueOf(game.getTeam1Score()));
		txtFTeam2.setText(String.valueOf(game.getTeam2Score()));
		lblTeam1.setText(game.getTeam1Name());
		lblTeam2.setText(game.getTeam2Name());
		String currText = curr.text();
		if (game.getCurrentQuestionIndex() != 0) {
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
		lblMultiplikator.setText(String.format("×%d", game.roundMultiplier()));
		if (game.getRoundState() == RoundState.STEALING_POINTS
				&& game.getCurrentTeam() != Game.NO_TEAM) {
			if (game.getCurrentTeam() == 1) {
				lblPunkteklau1.setVisible(true);
				lblPunkteklau2.setVisible(false);
			}
			else if (game.getCurrentTeam() == 2) {
				lblPunkteklau1.setVisible(false);
				lblPunkteklau2.setVisible(true);
			}
		}
		else {
			lblPunkteklau1.setVisible(false);
			lblPunkteklau2.setVisible(false);
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
		Question curr = game.currentQuestion();
		JLabel antwort = (JLabel) getComponentByName("lblAntwort" + (index + 1));
		JLabel punkte = (JLabel) getComponentByName("lblPunkte" + (index + 1));
		antwort.setText(curr.answerText(index));
		punkte.setText(String.valueOf(curr.answerScore(index)));
	}

	/**
	 * Blendet die entsprechende Anzahl an Antwortmöglichkeiten ein. Dies geschieht
	 * abgestimmt auf die Rundenzahl.
	 */
	private void showAnswerLabels() {
		int numberOfAnswers = game.numberOfAnswers();
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
			if (introMusic == null) {
				introMusic = GameSound.playMusic("intro.ogg");
			}
			else {
				setContentPane(outerPanel);
				outerPanel.revalidate();
				outerPanel.repaint();
				pause = false;
			}
		}
		else if (introMusic != null) {
			introMusic.stop();
			introMusic.unload();
			introMusic = null;
		}
	}

	/**
	 * Die Methode <code>playOutro</code> versetzt die Anzeige in einen Pausenbildschirm.
	 * Es wird abgeblendet.
	 */
	public void playOutro() {
		if (!pause) {
			setContentPane(outerPanelLabel);
			outerPanelLabel.revalidate();
			outerPanelLabel.repaint();
			pause = true;
		}
	}

	/**
	 * (Automatisch generiert) Hilfsmethode des Konstruktors. Erzeugt die Elemente der
	 * GUI.
	 */
	private void initializeUI() {
		contentPane = new ImagePanel();
		setIconImage(Toolkit.getDefaultToolkit().getImage(
				getClass().getResource("/Physikerduell-0.png")));
		contentPane.setImage(Toolkit.getDefaultToolkit().getImage(
				getClass().getResource("/Physikerduell-1.png")));
		setBackground(Color.BLACK);
		setName("Physikerduell-Anzeige");
		setTitle("Physikerduell");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setUndecorated(true);
		setLocationRelativeTo(null);

		contentPane.setBackground(Color.BLACK);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setPreferredSize(WINDOW_SIZE);
		contentPane.setLayout(null);
		
		contentPanePause = new JPanel();
		contentPanePause.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPanePause.setLayout(null);
		contentPanePause.setPreferredSize(WINDOW_SIZE);

		outerPanelLabel = new JPanel(new GridBagLayout());
		outerPanelLabel.setBackground(Color.BLACK);
		GridBagConstraints gbcLabel = new GridBagConstraints();
		outerPanelLabel.add(contentPanePause, gbcLabel);

		GridBagLayout gbl_outerPanel = new GridBagLayout();
		gbl_outerPanel.columnWeights = new double[] {0.0};
		outerPanel = new JPanel(gbl_outerPanel);
		outerPanel.setBackground(Color.BLACK);
		GridBagConstraints gbc = new GridBagConstraints();
		outerPanel.add(contentPane, gbc);

		JLabel lab = new JLabel(new ImageIcon(getClass().getResource("/Physikerduell-0.png")));
		lab.setBounds(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
		lab.setVisible(true);
		contentPanePause.add(lab);

		lblTeam1 = new JLabel("Team1");
		lblTeam1.setFont(new Font("Dialog", Font.PLAIN, 36));
		lblTeam1.setHorizontalAlignment(SwingConstants.LEFT);
		lblTeam1.setForeground(Color.WHITE);
		lblTeam1.setBounds(40, 520, 350, 45);
		contentPane.add(lblTeam1);

		lblFrageZ1 = new JLabel("Frage Zeile 1 Frage Zeile 1 Frage Zeile 1");
		lblFrageZ1.setHorizontalAlignment(SwingConstants.CENTER);
		lblFrageZ1.setForeground(Color.WHITE);
		lblFrageZ1.setFont(new Font("Dialog", Font.PLAIN, 36));
		lblFrageZ1.setBounds(0, 15, 1000, 45);
		contentPane.add(lblFrageZ1);

		lblAntwort1 = new JLabel("Antwort 1");
		lblAntwort1.setBackground(SystemColor.menu);
		lblAntwort1.setHorizontalAlignment(SwingConstants.CENTER);
		lblAntwort1.setFont(new Font("Dialog", Font.PLAIN, 24));
		lblAntwort1.setForeground(Color.YELLOW);
		lblAntwort1.setBounds(10, 134, 700, 37);
		contentPane.add(lblAntwort1);

		lblPunkte1 = new JLabel("67");
		lblPunkte1.setHorizontalAlignment(SwingConstants.CENTER);
		lblPunkte1.setFont(new Font("Dialog", Font.PLAIN, 24));
		lblPunkte1.setForeground(Color.YELLOW);
		lblPunkte1.setBounds(685, 134, 190, 37);
		contentPane.add(lblPunkte1);

		lblAntwort2 = new JLabel("Antwort 2");
		lblAntwort2.setHorizontalAlignment(SwingConstants.CENTER);
		lblAntwort2.setForeground(Color.YELLOW);
		lblAntwort2.setFont(new Font("Dialog", Font.PLAIN, 24));
		lblAntwort2.setBounds(10, 182, 700, 37);
		contentPane.add(lblAntwort2);

		lblPunkte2 = new JLabel("67");
		lblPunkte2.setHorizontalAlignment(SwingConstants.CENTER);
		lblPunkte2.setForeground(Color.YELLOW);
		lblPunkte2.setFont(new Font("Dialog", Font.PLAIN, 24));
		lblPunkte2.setBounds(685, 182, 190, 37);
		contentPane.add(lblPunkte2);

		lblAntwort4 = new JLabel("Antwort 4");
		lblAntwort4.setHorizontalAlignment(SwingConstants.CENTER);
		lblAntwort4.setForeground(Color.YELLOW);
		lblAntwort4.setFont(new Font("Dialog", Font.PLAIN, 24));
		lblAntwort4.setBounds(10, 278, 700, 37);
		contentPane.add(lblAntwort4);

		lblPunkte4 = new JLabel("67");
		lblPunkte4.setHorizontalAlignment(SwingConstants.CENTER);
		lblPunkte4.setForeground(Color.YELLOW);
		lblPunkte4.setFont(new Font("Dialog", Font.PLAIN, 24));
		lblPunkte4.setBounds(685, 278, 190, 37);
		contentPane.add(lblPunkte4);

		lblPunkte3 = new JLabel("67");
		lblPunkte3.setHorizontalAlignment(SwingConstants.CENTER);
		lblPunkte3.setForeground(Color.YELLOW);
		lblPunkte3.setFont(new Font("Dialog", Font.PLAIN, 24));
		lblPunkte3.setBounds(685, 230, 190, 37);
		contentPane.add(lblPunkte3);

		lblAntwort6 = new JLabel("Antwort 6");
		lblAntwort6.setHorizontalAlignment(SwingConstants.CENTER);
		lblAntwort6.setForeground(Color.YELLOW);
		lblAntwort6.setFont(new Font("Dialog", Font.PLAIN, 24));
		lblAntwort6.setBounds(10, 374, 700, 37);
		contentPane.add(lblAntwort6);

		lblAntwort5 = new JLabel("Antwort 5");
		lblAntwort5.setHorizontalAlignment(SwingConstants.CENTER);
		lblAntwort5.setForeground(Color.YELLOW);
		lblAntwort5.setFont(new Font("Dialog", Font.PLAIN, 24));
		lblAntwort5.setBounds(10, 326, 700, 37);
		contentPane.add(lblAntwort5);

		lblTeam2 = new JLabel("Team2");
		lblTeam2.setBounds(634, 520, 350, 45);
		contentPane.add(lblTeam2);
		lblTeam2.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTeam2.setForeground(Color.WHITE);
		lblTeam2.setFont(new Font("Dialog", Font.PLAIN, 36));

		lblPunkte6 = new JLabel("67");
		lblPunkte6.setHorizontalAlignment(SwingConstants.CENTER);
		lblPunkte6.setForeground(Color.YELLOW);
		lblPunkte6.setFont(new Font("Dialog", Font.PLAIN, 24));
		lblPunkte6.setBounds(685, 374, 190, 37);
		contentPane.add(lblPunkte6);

		lblPunkte5 = new JLabel("67");
		lblPunkte5.setHorizontalAlignment(SwingConstants.CENTER);
		lblPunkte5.setForeground(Color.YELLOW);
		lblPunkte5.setFont(new Font("Dialog", Font.PLAIN, 24));
		lblPunkte5.setBounds(685, 326, 190, 37);
		contentPane.add(lblPunkte5);

		lblSumme = new JLabel("Punkte");
		lblSumme.setHorizontalAlignment(SwingConstants.CENTER);
		lblSumme.setFont(new Font("Dialog", Font.PLAIN, 30));
		lblSumme.setForeground(Color.YELLOW);
		lblSumme.setBounds(685, 420, 190, 45);
		contentPane.add(lblSumme);

		JLabel lbltxtPunkte = new JLabel("Summe:");
		lbltxtPunkte.setHorizontalAlignment(SwingConstants.CENTER);
		lbltxtPunkte.setForeground(Color.YELLOW);
		lbltxtPunkte.setFont(new Font("Dialog", Font.PLAIN, 30));
		lbltxtPunkte.setBounds(585, 420, 135, 45);
		contentPane.add(lbltxtPunkte);

		lblLeben = new JLabel("XXX");
		lblLeben.setHorizontalAlignment(SwingConstants.LEFT);
		lblLeben.setForeground(Color.RED);
		lblLeben.setFont(new Font("Dialog", Font.BOLD, 56));
		lblLeben.setBounds(442, 580, 140, 45);
		contentPane.add(lblLeben);

		txtFTeam2 = new JTextField();
		txtFTeam2.setText("123");
		txtFTeam2.setHorizontalAlignment(SwingConstants.CENTER);
		txtFTeam2.setForeground(Color.YELLOW);
		txtFTeam2.setFont(new Font("Dialog", Font.PLAIN, 30));
		txtFTeam2.setColumns(10);
		txtFTeam2.setFocusable(false);
		txtFTeam2.setBackground(Color.DARK_GRAY);
		txtFTeam2.setBounds(831, 580, 90, 40);
		contentPane.add(txtFTeam2);

		txtFTeam1 = new JTextField();
		txtFTeam1.setForeground(Color.YELLOW);
		txtFTeam1.setBackground(Color.DARK_GRAY);
		txtFTeam1.setText("123");
		txtFTeam1.setColumns(10);
		txtFTeam1.setFocusable(false);
		txtFTeam1.setHorizontalAlignment(SwingConstants.CENTER);
		txtFTeam1.setFont(new Font("Dialog", Font.PLAIN, 30));
		txtFTeam1.setBounds(105, 580, 90, 40);
		contentPane.add(txtFTeam1);

		lblAntwort3 = new JLabel("Antwort 3");
		lblAntwort3.setBounds(10, 229, 700, 37);
		contentPane.add(lblAntwort3);
		lblAntwort3.setHorizontalAlignment(SwingConstants.CENTER);
		lblAntwort3.setForeground(Color.YELLOW);
		lblAntwort3.setFont(new Font("Dialog", Font.PLAIN, 24));

		lblFrageZ2 = new JLabel("Frage Zeile 2 Frage Zeile 2 Frage Zeile 2");
		lblFrageZ2.setHorizontalAlignment(SwingConstants.CENTER);
		lblFrageZ2.setForeground(Color.WHITE);
		lblFrageZ2.setFont(new Font("Dialog", Font.PLAIN, 36));
		lblFrageZ2.setBounds(0, 55, 1000, 45);
		contentPane.add(lblFrageZ2);

		lblMultiplikator = new JLabel("Multiplikator");
		lblMultiplikator.setHorizontalAlignment(SwingConstants.CENTER);
		lblMultiplikator.setForeground(Color.YELLOW);
		lblMultiplikator.setFont(new Font("Dialog", Font.PLAIN, 30));
		lblMultiplikator.setBounds(887, 420, 66, 45);
		contentPane.add(lblMultiplikator);

		lblPunkteklau1 = new GradientLabel("Punkteklau möglich!");
		lblPunkteklau1.setHorizontalAlignment(SwingConstants.CENTER);
		lblPunkteklau1.setForeground(Color.RED);
		lblPunkteklau1.setFont(new Font("Dialog", Font.PLAIN, 26));
		lblPunkteklau1.setBounds(100, 465, 320, 55);
		lblPunkteklau1.setVisible(false);
		contentPane.add(lblPunkteklau1);

		lblPunkteklau2 = new GradientLabel("Punkteklau möglich!");
		lblPunkteklau2.setHorizontalAlignment(SwingConstants.CENTER);
		lblPunkteklau2.setForeground(Color.RED);
		lblPunkteklau2.setFont(new Font("Dialog", Font.PLAIN, 26));
		lblPunkteklau2.setBounds(624, 465, 320, 55);
		lblPunkteklau2.setVisible(false);
		contentPane.add(lblPunkteklau2);

		setContentPane(outerPanelLabel);
	}
	
}
