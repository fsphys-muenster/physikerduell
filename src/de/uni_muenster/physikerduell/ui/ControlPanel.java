package de.uni_muenster.physikerduell.ui;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import org.newdawn.easyogg.OggClip;
import de.uni_muenster.physikerduell.game.Answer;
import de.uni_muenster.physikerduell.game.Game;
import de.uni_muenster.physikerduell.game.Game.RoundState;
import de.uni_muenster.physikerduell.game.GameException;
import de.uni_muenster.physikerduell.game.GameListener;
import de.uni_muenster.physikerduell.game.GameLog;
import de.uni_muenster.physikerduell.game.Question;

/**
 * Hauptklasse des Physikerduells der Fachschaft Physik an der WWU Münster am
 * <ul>
 * <li>12.06.2013</li>
 * <li>05.06.2014</li>
 * <li>18.06.2015</li>
 * </ul>
 * <p>
 * 
 * Die Klasse ControlPanel verwaltet die Spielanzeige und den Spielstand (Klassen Anzeige
 * und Game) und stellt eine Benutzeroberfläche zur Verwaltung des Spiels bereit.
 * 
 * @author Lutz Althüser
 * @author Simon May
 */
public class ControlPanel implements ActionListener, GameListener {

	private static final String QUESTIONS_PATH_EXT = "fragen.csv";
	private static final String QUESTIONS_PATH_INT = "/res/fragen.csv";
	private static final Action PLAY_BUZZER = new AbstractAction() {
		private static final long serialVersionUID = 1L;
		private OggClip ogg;
		@Override
		public void actionPerformed(ActionEvent e) {
			if (ogg == null) {
				try {
					ogg = new OggClip(getClass().getResourceAsStream("/res/buzzer.ogg"));
				} catch (IOException ex) {
					System.err.println("Buzzer sound could not be loaded: " + ex);
				}
			}
			if (ogg.stopped()) {
				ogg.play();
			}
		}
	};
	private JFrame frmControl;
	private JTextField txtTeam1Name;
	private JTextField txtTeam2Name;
	private JTextField txtTeam1GPunkte;
	private JTextField txtTeam2GPunkte;
	private JTextField txtALeben;
	private JButton btnFalscheAntwort;
	private JButton btnAbspannWechsel;
	private JButton btnNaechsteFrage;
	private JButton btnStart;
	private JButton btnOpenLog;
	private JButton btnOpenQuestions;
	private JButton btnBuzzer;
	private JComboBox<String> cbFragenauswahl;
	private JLabel lblMultiplier;
	private JLabel txtAPunkte;
	private JLabel lblAntwort1;
	private JLabel lblAntwort2;
	private JLabel lblAntwort3;
	private JLabel lblAntwort4;
	private JLabel lblAntwort5;
	private JLabel lblAntwort6;
	private JRadioButton rdbtnTeam1;
	private JRadioButton rdbtnTeam2;
	private JRadioButton rdbtnNoTeam;
	private JRadioButton rdbtnPunkteklau;
	private JRadioButton rdbtnRundenende;
	private JRadioButton rdbtnNormaleRunde;
	private JRadioButton rdbtnBuzzermodus;
	private JRadioButton rdbtnRunde1;
	private JRadioButton rdbtnRunde2;
	private JRadioButton rdbtnRunde3;
	private JRadioButton rdbtnRunde4;
	private JRadioButton rdbtnRunde5;
	private JCheckBox chckbxAntwort1;
	private JCheckBox chckbxAntwort2;
	private JCheckBox chckbxAntwort3;
	private JCheckBox chckbxAntwort4;
	private JCheckBox chckbxAntwort5;
	private JCheckBox chckbxAntwort6;
	private JTextPane txtpnImpressum;
	private ButtonGroup activeTeam;
	private ButtonGroup activeRound;
	private ButtonGroup roundStatus;
	private Game game;
	private final Display display;

	/**
	 * Die <code>main</code>-Methode soll zum Anstoß geben, dass die Objekte initialisiert
	 * werden, angefangen mit dem ControlPanel.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					ControlPanel window = new ControlPanel();
					window.frmControl.setVisible(true);
				}
				catch (GameException ex) {
					System.err.println("Game could not be started: " + ex);
					System.exit(1);
				}
			}
		});
	}

	/**
	 * Konstruktion des ControlPanel.
	 * 
	 * @throws GameException
	 *             Falls während der Initialisierung der Klasse ein Fehler auftritt.
	 */
	public ControlPanel() throws GameException {
		initializeUI();
		// register key combo for buzzer sound (CTRL + SHIFT + B)
		InputMap im = btnBuzzer.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		im.put(KeyStroke.getKeyStroke("control shift B"), "play_buzzer");
		btnBuzzer.getActionMap().put("play_buzzer", PLAY_BUZZER);
		// initialize game state
		InputStream questionFile;
		try {
			questionFile = Files.newInputStream(Paths.get(QUESTIONS_PATH_EXT));
		}
		catch (IOException ex) {
			questionFile = getClass().getResourceAsStream(QUESTIONS_PATH_INT);
		}
		// create display window
		display = new Display();
		resetGame(questionFile);
		display.setUndecorated(true);
		display.setAlwaysOnTop(!frmControl.isAlwaysOnTop());
		display.setVisible(true);
		display.setGame(game);
	}
	
	private void resetGame(InputStream questionFile) throws GameException {
		game = new Game(questionFile);
		game.setLogging(true);
		// ComboBox mit Fragen auffüllen
		cbFragenauswahl.removeAllItems();
		for (int i = 0; i < game.questionCount(); i++) {
			cbFragenauswahl.addItem(game.getQuestion(i).text());
		}
		// Bedienoberfläche erhält Spiel-Updates
		game.addListener(this);
	}

	/**
	 * ActionListener für Buttons und Textfelder.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnStart) {
			btnAbspannWechsel.setEnabled(true);
			display.playIntro();
		}
		else if (e.getSource() == btnAbspannWechsel) {
			display.playOutro();
		}
		else if (e.getSource() == btnFalscheAntwort) {
			game.wrongAnswer();
			Sound.playOgg("wrong.ogg");
		}
		else if (e.getSource() == btnNaechsteFrage) {
			int currIndex = game.getCurrentQuestionIndex();
			if (currIndex + 1 < game.questionCount()) {
				// introduce next question
				game.setCurrentQuestionIndex(currIndex + 1);
			}
		}
		else if (e.getSource() == rdbtnTeam1) {
			game.setCurrentTeam(1);
		}
		else if (e.getSource() == rdbtnTeam2) {
			game.setCurrentTeam(2);
		}
		else if (e.getSource() == rdbtnNoTeam) {
			game.setCurrentTeam(Game.NO_TEAM);
		}
		else if (e.getSource() == txtTeam1Name || e.getSource() == txtTeam2Name) {
			checkTeamNameInput();
		}
		else if (e.getSource() == txtTeam1GPunkte) {
			game.setTeam1Score(Integer.parseInt(txtTeam1GPunkte.getText()));
		}
		else if (e.getSource() == txtTeam2GPunkte) {
			game.setTeam2Score(Integer.parseInt(txtTeam2GPunkte.getText()));
		}
		else if (e.getSource() == txtALeben) {
			int input = Integer.parseInt(txtALeben.getText());
			if (input >= 0 && input <= 3) {
				game.setCurrentLives(input);
			}
			else {
				txtALeben.setText(Integer.toString(input));
			}
		}
		else if (e.getSource() == rdbtnBuzzermodus) {
			game.setRoundState(RoundState.BUZZER);
		}
		else if (e.getSource() == rdbtnNormaleRunde) {
			game.setRoundState(RoundState.NORMAL);
		}
		else if (e.getSource() == rdbtnPunkteklau) {
			game.setRoundState(RoundState.STEALING_POINTS);
		}
		else if (e.getSource() == cbFragenauswahl) {
			int selected = cbFragenauswahl.getSelectedIndex();
			// choose selected question if selected question changed
			if (selected >= 0 && selected != game.getCurrentQuestionIndex()) {
				game.setCurrentQuestionIndex(selected);
			}
		}
		else if (e.getSource() == btnOpenQuestions) {
			try {
				JFileChooser chooser = new JFileChooser(".");
				int returnVal = chooser.showOpenDialog(this.display);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					Path questionPath = chooser.getSelectedFile().toPath();
					resetGame(Files.newInputStream(questionPath));
				}
			}
			catch (Exception ex) {
				System.err.println("Error reading question file: " + ex);
				JOptionPane.showMessageDialog(frmControl, "Could not read question file!");
			}
		}
		else if (e.getSource() == btnOpenLog) {
			try {
				Desktop.getDesktop().open(new File(GameLog.LOG_FILE_PATH));
			}
			catch (Exception ex) {
				System.err.println("Error reading log file: " + ex);
				JOptionPane.showMessageDialog(frmControl, "Could not read log file!", "File not found", 
						JOptionPane.WARNING_MESSAGE);
			}
		}
		// RadioButtons (rounds)
		for (int i = 1; i <= Game.NUM_ROUNDS; i++) {
			JRadioButton rdo = (JRadioButton) getComponentByName("rdbtnRunde" + i);
			if (e.getSource().equals(rdo)) {
				game.setCurrentRound(i);
				break;
			}
		}
		// CheckBoxes (answers)
		for (int i = 0; i < Game.MAX_ANSWERS; i++) {
			JCheckBox chkbx = (JCheckBox) getComponentByName("chckbxAntwort" + (i + 1));
			Question currentQ = game.currentQuestion();
			if (e.getSource() == chkbx) {
				boolean selected = chkbx.isSelected();
				if (selected) {
					Sound.playOgg("right.ogg");
					if (game.getRoundState() == RoundState.ROUND_ENDED) {
						currentQ.answer(i).setRevealed(true);
					}
					else {
						game.correctAnswer(i);
					}
				}
				else {
					currentQ.answer(i).setRevealed(false);
				}
				break;
			}
		}
	}

	/**
	 * Update UI if the game state changes.
	 */
	@Override
	public void gameUpdate() {
		boolean roundEnd = game.getRoundState() == RoundState.ROUND_ENDED;
		// insert data into corresponding fields
		txtTeam1Name.setText(game.getTeam1Name());
		txtTeam2Name.setText(game.getTeam2Name());
		txtTeam1GPunkte.setText(String.valueOf(game.getTeam1Score()));
		txtTeam2GPunkte.setText(String.valueOf(game.getTeam2Score()));
		txtALeben.setText(String.valueOf(game.getCurrentLives()));
		txtAPunkte.setText(String.valueOf(game.currentScore()));
		lblMultiplier.setText(String.format("×%d", game.roundMultiplier()));
		// select appropriate round state radio button
		switch (game.getRoundState()) {
		case BUZZER:
			roundStatus.setSelected(rdbtnBuzzermodus.getModel(), true);
			break;
		case NORMAL:
			roundStatus.setSelected(rdbtnNormaleRunde.getModel(), true);
			break;
		case STEALING_POINTS:
			roundStatus.setSelected(rdbtnPunkteklau.getModel(), true);
			break;
		case ROUND_ENDED:
			roundStatus.setSelected(rdbtnRundenende.getModel(), true);
			break;
		}
		// select team RadioButton according to selected team
		int currTeam = game.getCurrentTeam();
		switch (currTeam) {
		case 1:
			activeTeam.setSelected(rdbtnTeam1.getModel(), true);
			break;
		case 2:
			activeTeam.setSelected(rdbtnTeam2.getModel(), true);
			break;
		default:
			activeTeam.setSelected(rdbtnNoTeam.getModel(), true);
			break;
		}
		// disable team buttons if round ended
		rdbtnTeam1.setEnabled(!roundEnd);
		rdbtnTeam2.setEnabled(!roundEnd);
		rdbtnNoTeam.setEnabled(!roundEnd);
		// select round radiobutton according to selected round
		JRadioButton rdoRound =
			(JRadioButton) getComponentByName("rdbtnRunde" + game.getCurrentRound());
		activeRound.setSelected(rdoRound.getModel(), true);
		// disable point stealing radiobutton if no team is selected
		rdbtnPunkteklau.setEnabled(game.getCurrentTeam() != -1);
		// select ComboBox item according to selected question
		int currIndex = game.getCurrentQuestionIndex();
		cbFragenauswahl.setSelectedIndex(currIndex);
		// set answer CheckBox text and score Label text
		// enable/disable and select/deselect answer CheckBoxes
		Question currQuestion = game.currentQuestion();
		for (int i = 0; i < Game.MAX_ANSWERS; i++) {
			JCheckBox chkAns = (JCheckBox) getComponentByName("chckbxAntwort" + (i + 1));
			JLabel lblAns = (JLabel) getComponentByName("lblAntwort" + (i + 1));
			boolean answersLeft = i < currQuestion.answerCount();
			if (answersLeft) {
				Answer ans = currQuestion.answer(i);
				String chkText = ans.text();
				String lblText = String.valueOf(ans.score());
				// No text if question 0 (test question)
				if (currIndex == 0) {
					chkText = lblText = "";
				}
				chkAns.setText(chkText);
				chkAns.setSelected(ans.isRevealed());
				lblAns.setText(lblText);
			}
			boolean validAnswer = i < game.numberOfAnswers() && currIndex != 0 && answersLeft;
			// Activate if valid answer and during a round with a team selected
			// or at round end (so the answers can still be revealed to the audience)
			boolean enabled = validAnswer && ((!roundEnd && currTeam != -1) || roundEnd);
			chkAns.setEnabled(enabled);
		}
		// Enable/disable button for next question
		boolean nextQ = game.getCurrentQuestionIndex() + 1 < game.questionCount();
		btnNaechsteFrage.setEnabled(nextQ);
		// Enable/disable lives text field
		txtALeben.setEnabled(currTeam != Game.NO_TEAM);
	}

	/**
	 * Checks if the inputted team names are valid.
	 */
	private void checkTeamNameInput() {
		int MAX_NAME_LEN = 18;
		txtTeam1Name.setBackground(Color.WHITE);
		txtTeam2Name.setBackground(Color.WHITE);
		String team1Name = txtTeam1Name.getText();
		String team2Name = txtTeam2Name.getText();
		boolean valid = !team1Name.isEmpty() && !team2Name.isEmpty() && team1Name.length() <= MAX_NAME_LEN
				&& team2Name.length() <= MAX_NAME_LEN;
		if (valid) {
			game.setTeam1Name(team1Name);
			game.setTeam2Name(team2Name);
		}
		else {
			if (team1Name.isEmpty() || team1Name.length() > MAX_NAME_LEN) {
				txtTeam1Name.setBackground(Color.RED);
			}
			if (team2Name.isEmpty() || team2Name.length() > MAX_NAME_LEN) {
				txtTeam2Name.setBackground(Color.RED);
			}
		}
		txtTeam1GPunkte.setEnabled(valid);
		txtTeam2GPunkte.setEnabled(valid);
		cbFragenauswahl.setEnabled(valid);
		btnFalscheAntwort.setEnabled(valid);
		btnNaechsteFrage.setEnabled(valid);
		btnStart.setEnabled(valid);
		rdbtnBuzzermodus.setEnabled(valid);
		rdbtnNormaleRunde.setEnabled(valid);
		for (int i = 1; i <= Game.NUM_ROUNDS; i++) {
			JRadioButton rdo = (JRadioButton) getComponentByName("rdbtnRunde" + i);
			rdo.setEnabled(valid);
		}
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
	 * <strong>(Automatisch generiert!)</strong>
	 * <p>
	 * Hilfsmethode des Konstruktors. Erzeugt die Elemente der
	 * GUI.
	 */
	private void initializeUI() {
		frmControl = new JFrame();
		frmControl.setIconImage(Toolkit.getDefaultToolkit().getImage(
			getClass().getResource("/res/Physikerduell-0.png")));
		frmControl.setAlwaysOnTop(true);
		frmControl.setTitle("Physikerduell – Bedienoberfläche");
		frmControl.setResizable(false);
		frmControl.getContentPane().setBackground(Color.WHITE);
		frmControl.setBounds(100, 100, 1024, 716);
		frmControl.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmControl.setLocationRelativeTo(null);

		cbFragenauswahl = new JComboBox<String>();
		cbFragenauswahl.setEnabled(false);
		cbFragenauswahl.setBounds(10, 380, 492, 30);
		cbFragenauswahl.setModel(new DefaultComboBoxModel<String>(new String[] {
			"Was ist denn die Frage?", "A?", "B?", "C?", "D?", "E?"}));

		JPanel pAntwortmoeglichkeiten = new JPanel();
		pAntwortmoeglichkeiten.setBounds(10, 415, 492, 190);
		pAntwortmoeglichkeiten.setLayout(null);

		chckbxAntwort1 = new JCheckBox();
		chckbxAntwort1.setEnabled(false);
		chckbxAntwort1.setBounds(5, 5, 440, 30);
		pAntwortmoeglichkeiten.add(chckbxAntwort1);

		lblAntwort1 = new JLabel("");
		lblAntwort1.setBounds(450, 5, 35, 30);
		pAntwortmoeglichkeiten.add(lblAntwort1);

		chckbxAntwort2 = new JCheckBox();
		chckbxAntwort2.setEnabled(false);
		chckbxAntwort2.setBounds(5, 35, 440, 30);
		pAntwortmoeglichkeiten.add(chckbxAntwort2);

		lblAntwort2 = new JLabel("");
		lblAntwort2.setBounds(450, 35, 35, 30);
		pAntwortmoeglichkeiten.add(lblAntwort2);

		chckbxAntwort3 = new JCheckBox();
		chckbxAntwort3.setEnabled(false);
		chckbxAntwort3.setBounds(5, 65, 440, 30);
		pAntwortmoeglichkeiten.add(chckbxAntwort3);

		lblAntwort3 = new JLabel("");
		lblAntwort3.setBounds(450, 65, 35, 30);
		pAntwortmoeglichkeiten.add(lblAntwort3);

		chckbxAntwort4 = new JCheckBox();
		chckbxAntwort4.setEnabled(false);
		chckbxAntwort4.setBounds(5, 95, 440, 30);
		pAntwortmoeglichkeiten.add(chckbxAntwort4);

		lblAntwort4 = new JLabel("");
		lblAntwort4.setBounds(450, 95, 35, 30);
		pAntwortmoeglichkeiten.add(lblAntwort4);

		chckbxAntwort5 = new JCheckBox();
		chckbxAntwort5.setEnabled(false);
		chckbxAntwort5.setBounds(5, 125, 440, 30);
		pAntwortmoeglichkeiten.add(chckbxAntwort5);

		lblAntwort5 = new JLabel("");
		lblAntwort5.setBounds(450, 125, 35, 30);
		pAntwortmoeglichkeiten.add(lblAntwort5);

		chckbxAntwort6 = new JCheckBox();
		chckbxAntwort6.setEnabled(false);
		chckbxAntwort6.setBounds(5, 155, 440, 30);
		pAntwortmoeglichkeiten.add(chckbxAntwort6);

		lblAntwort6 = new JLabel("");
		lblAntwort6.setBounds(450, 155, 35, 30);
		pAntwortmoeglichkeiten.add(lblAntwort6);

		btnFalscheAntwort = new JButton("Falsche Antwort");
		btnFalscheAntwort.setEnabled(false);
		btnFalscheAntwort.setBounds(10, 610, 492, 30);
		frmControl.getContentPane().setLayout(null);

		JLabel lblHeadline = new JLabel("Bedienoberfläche des Physikerduells");
		lblHeadline.setBounds(12, 0, 1000, 38);
		lblHeadline.setFont(new Font("Dialog", Font.BOLD, 20));
		lblHeadline.setHorizontalAlignment(SwingConstants.CENTER);
		frmControl.getContentPane().add(lblHeadline);

		JLabel lblTeam1Name = new JLabel("Teamname von Team 1");
		lblTeam1Name.setFont(new Font("Dialog", Font.BOLD, 11));
		lblTeam1Name.setBounds(10, 38, 150, 25);
		frmControl.getContentPane().add(lblTeam1Name);

		txtTeam1Name = new JTextField();
		txtTeam1Name.setToolTipText("Teamname von Team 1 – Mit ENTER die Eingabe bestätigen!");
		txtTeam1Name.setBounds(20, 60, 472, 25);
		frmControl.getContentPane().add(txtTeam1Name);

		JLabel lblTeam1GPunkte = new JLabel("Gesamtpunkte von Team 1:");
		lblTeam1GPunkte.setFont(new Font("Dialog", Font.BOLD, 11));
		lblTeam1GPunkte.setBounds(10, 100, 180, 25);
		frmControl.getContentPane().add(lblTeam1GPunkte);

		txtTeam1GPunkte = new JTextField();
		txtTeam1GPunkte.setEnabled(false);
		txtTeam1GPunkte.setToolTipText("Mit ENTER die Eingabe bestätigen!");
		txtTeam1GPunkte.setText("0");
		txtTeam1GPunkte.setHorizontalAlignment(SwingConstants.CENTER);
		txtTeam1GPunkte.setFont(new Font("Dialog", Font.BOLD, 15));
		txtTeam1GPunkte.setBounds(190, 100, 100, 25);
		frmControl.getContentPane().add(txtTeam1GPunkte);

		JLabel lblTeam2Name = new JLabel("Teamname von Team 2");
		lblTeam2Name.setFont(new Font("Dialog", Font.BOLD, 11));
		lblTeam2Name.setBounds(522, 38, 150, 25);
		frmControl.getContentPane().add(lblTeam2Name);

		txtTeam2Name = new JTextField();
		txtTeam2Name.setToolTipText("Teamname von Team 2 – Mit ENTER die Eingabe bestätigen!");
		txtTeam2Name.setBounds(532, 60, 472, 25);
		frmControl.getContentPane().add(txtTeam2Name);

		JLabel lblTeam2GPunkte = new JLabel("Gesamtpunkte von Team 2:");
		lblTeam2GPunkte.setFont(new Font("Dialog", Font.BOLD, 11));
		lblTeam2GPunkte.setBounds(522, 100, 180, 25);
		frmControl.getContentPane().add(lblTeam2GPunkte);

		txtTeam2GPunkte = new JTextField();
		txtTeam2GPunkte.setEnabled(false);
		txtTeam2GPunkte.setToolTipText(" Mit ENTER die Eingabe bestätigen!");
		txtTeam2GPunkte.setText("0");
		txtTeam2GPunkte.setHorizontalAlignment(SwingConstants.CENTER);
		txtTeam2GPunkte.setFont(new Font("Dialog", Font.BOLD, 15));
		txtTeam2GPunkte.setBounds(702, 100, 100, 25);
		frmControl.getContentPane().add(txtTeam2GPunkte);

		btnStart = new JButton("Vorspann und Start (Mehrfachfunktion)");
		btnStart.setEnabled(false);
		btnStart.setBounds(10, 150, 390, 50);
		frmControl.getContentPane().add(btnStart);

		btnAbspannWechsel = new JButton("Abspann");
		btnAbspannWechsel.setEnabled(false);
		btnAbspannWechsel.setBounds(10, 211, 390, 50);
		frmControl.getContentPane().add(btnAbspannWechsel);

		JLabel lblAktuellesTeam = new JLabel("Aktuelles Team");
		lblAktuellesTeam.setHorizontalAlignment(SwingConstants.CENTER);
		lblAktuellesTeam.setFont(new Font("Dialog", Font.BOLD, 11));
		lblAktuellesTeam.setBounds(457, 137, 116, 25);
		frmControl.getContentPane().add(lblAktuellesTeam);

		JPanel pTeamauswahl = new JPanel();
		pTeamauswahl.setBounds(457, 162, 116, 90);
		frmControl.getContentPane().add(pTeamauswahl);

		rdbtnTeam1 = new JRadioButton("Team 1");
		rdbtnTeam1.setEnabled(false);
		pTeamauswahl.add(rdbtnTeam1);

		rdbtnTeam2 = new JRadioButton("Team 2");
		rdbtnTeam2.setEnabled(false);
		pTeamauswahl.add(rdbtnTeam2);

		JLabel lblAPunkte = new JLabel("Punkte des aktuellen Teams");
		lblAPunkte.setHorizontalAlignment(SwingConstants.CENTER);
		lblAPunkte.setFont(new Font("Dialog", Font.BOLD, 11));
		lblAPunkte.setBounds(614, 137, 188, 25);
		frmControl.getContentPane().add(lblAPunkte);

		txtAPunkte = new JLabel();
		txtAPunkte.setToolTipText("");
		txtAPunkte.setHorizontalAlignment(SwingConstants.CENTER);
		txtAPunkte.setText("0");
		txtAPunkte.setFont(new Font("Dialog", Font.BOLD, 15));
		txtAPunkte.setBounds(614, 162, 188, 25);
		frmControl.getContentPane().add(txtAPunkte);

		JLabel lblALeben = new JLabel("Leben des aktuellen Teams");
		lblALeben.setHorizontalAlignment(SwingConstants.CENTER);
		lblALeben.setFont(new Font("Dialog", Font.BOLD, 11));
		lblALeben.setBounds(614, 189, 188, 25);
		frmControl.getContentPane().add(lblALeben);

		txtALeben = new JTextField();
		txtALeben.setToolTipText("Mit ENTER die Eingabe bestätigen!");
		txtALeben.setEnabled(false);
		txtALeben.setHorizontalAlignment(SwingConstants.CENTER);
		txtALeben.setText("3");
		txtALeben.setFont(new Font("Dialog", Font.BOLD, 15));
		txtALeben.setBounds(614, 212, 188, 25);
		txtALeben.setColumns(1);
		frmControl.getContentPane().add(txtALeben);

		JPanel pRundenauswahl = new JPanel();
		pRundenauswahl.setBounds(10, 280, 530, 90);
		frmControl.getContentPane().add(pRundenauswahl);
		pRundenauswahl.setLayout(null);

		JLabel lblRundenauswahl = new JLabel("Rundenauswahl");
		lblRundenauswahl.setBounds(200, 5, 130, 14);
		lblRundenauswahl.setHorizontalAlignment(SwingConstants.CENTER);
		lblRundenauswahl.setFont(new Font("Dialog", Font.BOLD, 11));
		pRundenauswahl.add(lblRundenauswahl);

		rdbtnRunde1 = new JRadioButton("Runde 1");
		rdbtnRunde1.setSelected(true);
		rdbtnRunde1.setEnabled(false);
		rdbtnRunde1.setBounds(8, 33, 100, 25);
		pRundenauswahl.add(rdbtnRunde1);

		rdbtnRunde2 = new JRadioButton("Runde 2");
		rdbtnRunde2.setEnabled(false);
		rdbtnRunde2.setBounds(112, 33, 100, 25);
		pRundenauswahl.add(rdbtnRunde2);

		rdbtnRunde3 = new JRadioButton("Runde 3");
		rdbtnRunde3.setEnabled(false);
		rdbtnRunde3.setBounds(216, 33, 100, 25);
		pRundenauswahl.add(rdbtnRunde3);

		rdbtnRunde4 = new JRadioButton("Runde 4");
		rdbtnRunde4.setEnabled(false);
		rdbtnRunde4.setBounds(320, 33, 100, 25);
		pRundenauswahl.add(rdbtnRunde4);

		rdbtnRunde5 = new JRadioButton("Runde 5");
		rdbtnRunde5.setEnabled(false);
		rdbtnRunde5.setBounds(424, 33, 100, 25);
		pRundenauswahl.add(rdbtnRunde5);

		frmControl.getContentPane().add(cbFragenauswahl);
		frmControl.getContentPane().add(pAntwortmoeglichkeiten);
		frmControl.getContentPane().add(btnFalscheAntwort);

		btnNaechsteFrage = new JButton(">> Nächste Frage <<");
		btnNaechsteFrage.setEnabled(false);
		btnNaechsteFrage.setBounds(10, 645, 492, 30);
		frmControl.getContentPane().add(btnNaechsteFrage);

		rdbtnNoTeam = new JRadioButton("Kein Team");
		rdbtnNoTeam.setSelected(true);
		rdbtnNoTeam.setEnabled(false);
		pTeamauswahl.add(rdbtnNoTeam);

		activeTeam = new ButtonGroup();
		activeTeam.add(rdbtnNoTeam);
		activeTeam.add(rdbtnTeam1);
		activeTeam.add(rdbtnTeam2);

		activeRound = new ButtonGroup();
		activeRound.add(rdbtnRunde1);
		activeRound.add(rdbtnRunde2);
		activeRound.add(rdbtnRunde3);
		activeRound.add(rdbtnRunde4);
		activeRound.add(rdbtnRunde5);

		btnOpenLog = new JButton("Log-Datei öffnen");
		btnOpenLog.setBounds(824, 625, 180, 23);
		frmControl.getContentPane().add(btnOpenLog);
		btnOpenLog.addActionListener(this);

		JTextPane txtpnBeschreibung = new JTextPane();
		txtpnBeschreibung.setEditable(false);
		txtpnBeschreibung.setFont(new Font("Dialog", Font.PLAIN, 13));
		txtpnBeschreibung.setText("Physikerduell:\n\n"
			+ "Alle Eingaben müssen mit der ENTER-Taste bestätigt werden!\n"
			+ "Auflösung sollte 1024×768 sein!\n\nAblauf:\n"
			+ "1) Eintragen der Teamnamen.\n"
			+ "2) Starten des Spiels → Nur Musik → Spieloberfläche → ggf. Musik aus.\n"
			+ "3) Frage wählen/Nächste Frage drücken. (Runden passen sich automatisch an.)\n"
			+ "4) ggf. Teams wechseln\n"
			+ "5) Zwischen zwei Spielen den Abspann anzeigen.\n\n\n\n\n");
		txtpnBeschreibung.setBounds(512, 381, 496, 228);
		frmControl.getContentPane().add(txtpnBeschreibung);

		txtpnImpressum = new JTextPane();
		txtpnImpressum.setFont(new Font("Dialog", Font.PLAIN, 13));
		txtpnImpressum.setEditable(false);
		txtpnImpressum.setText("Version 2015\t\t  Lutz Althüser, Simon May");
		txtpnImpressum.setBounds(512, 651, 496, 25);
		frmControl.getContentPane().add(txtpnImpressum);

		JLabel lblMultiplierText = new JLabel("Multiplikator");
		lblMultiplierText.setHorizontalAlignment(SwingConstants.CENTER);
		lblMultiplierText.setFont(new Font("Dialog", Font.BOLD, 11));
		lblMultiplierText.setBounds(814, 137, 85, 25);
		frmControl.getContentPane().add(lblMultiplierText);

		lblMultiplier = new JLabel();
		lblMultiplier.setToolTipText("");
		lblMultiplier.setText("×1");
		lblMultiplier.setHorizontalAlignment(SwingConstants.CENTER);
		lblMultiplier.setFont(new Font("Dialog", Font.BOLD, 15));
		lblMultiplier.setBounds(814, 162, 85, 25);
		frmControl.getContentPane().add(lblMultiplier);
		
		JPanel pRundenstatus = new JPanel();
		pRundenstatus.setBounds(552, 280, 332, 90);
		frmControl.getContentPane().add(pRundenstatus);
		pRundenstatus.setLayout(null);

		JLabel lblRundenstatus = new JLabel("Rundenstatus");
		lblRundenstatus.setBounds(0, 5, 332, 14);
		pRundenstatus.add(lblRundenstatus);
		lblRundenstatus.setHorizontalAlignment(SwingConstants.CENTER);
		lblRundenstatus.setFont(new Font("Dialog", Font.BOLD, 11));
		
		roundStatus = new ButtonGroup();
		rdbtnBuzzermodus = new JRadioButton("Buzzer-Modus");
		rdbtnBuzzermodus.setBounds(10, 32, 133, 23);
		roundStatus.add(rdbtnBuzzermodus);
		rdbtnBuzzermodus.setSelected(true);
		rdbtnBuzzermodus.setEnabled(false);
		pRundenstatus.add(rdbtnBuzzermodus);

		rdbtnNormaleRunde = new JRadioButton("Normale Runde");
		rdbtnNormaleRunde.setBounds(10, 59, 133, 23);
		rdbtnNormaleRunde.setEnabled(false);
		roundStatus.add(rdbtnNormaleRunde);
		pRundenstatus.add(rdbtnNormaleRunde);

		rdbtnPunkteklau = new JRadioButton("Punkteklau");
		rdbtnPunkteklau.setBounds(203, 32, 115, 23);
		roundStatus.add(rdbtnPunkteklau);
		rdbtnPunkteklau.setEnabled(false);
		pRundenstatus.add(rdbtnPunkteklau);

		rdbtnRundenende = new JRadioButton("Rundenende");
		rdbtnRundenende.setBounds(203, 59, 115, 23);
		roundStatus.add(rdbtnRundenende);
		rdbtnRundenende.setEnabled(false);
		pRundenstatus.add(rdbtnRundenende);
		
		btnOpenQuestions = new JButton("Fragenkatalog öffnen");
		btnOpenQuestions.setBounds(514, 624, 200, 23);
		frmControl.getContentPane().add(btnOpenQuestions);
	
		btnBuzzer = new JButton("Buzzer");
		btnBuzzer.setBounds(895, 297, 109, 55);
		frmControl.getContentPane().add(btnBuzzer);

		btnBuzzer.addActionListener(PLAY_BUZZER);
		btnOpenQuestions.addActionListener(this);
		btnStart.addActionListener(this);
		btnAbspannWechsel.addActionListener(this);
		btnFalscheAntwort.addActionListener(this);
		btnNaechsteFrage.addActionListener(this);
		txtTeam1Name.addActionListener(this);
		txtTeam2Name.addActionListener(this);
		txtTeam1GPunkte.addActionListener(this);
		txtTeam2GPunkte.addActionListener(this);
		txtALeben.addActionListener(this);
		rdbtnTeam1.addActionListener(this);
		rdbtnTeam2.addActionListener(this);
		rdbtnRunde1.addActionListener(this);
		rdbtnRunde2.addActionListener(this);
		rdbtnRunde3.addActionListener(this);
		rdbtnRunde4.addActionListener(this);
		rdbtnRunde5.addActionListener(this);
		rdbtnNoTeam.addActionListener(this);
		rdbtnBuzzermodus.addActionListener(this);
		rdbtnNormaleRunde.addActionListener(this);
		rdbtnPunkteklau.addActionListener(this);
		rdbtnRundenende.addActionListener(this);
		chckbxAntwort1.addActionListener(this);
		chckbxAntwort2.addActionListener(this);
		chckbxAntwort3.addActionListener(this);
		chckbxAntwort4.addActionListener(this);
		chckbxAntwort5.addActionListener(this);
		chckbxAntwort6.addActionListener(this);
		cbFragenauswahl.addActionListener(this);
	}

}
