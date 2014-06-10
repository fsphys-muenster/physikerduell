package de.uni_muenster.physikerduell.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javazoom.jl.player.PlayerApplet;
import org.eclipse.wb.swing.FocusTraversalOnArray;
import de.uni_muenster.physikerduell.game.Answer;
import de.uni_muenster.physikerduell.game.Game;
import de.uni_muenster.physikerduell.game.Game.GameException;
import de.uni_muenster.physikerduell.game.GameListener;
import de.uni_muenster.physikerduell.game.Question;

/**
 * Hauptklasse des Physikerduells der Fachschaft Physik an der WWU Münster am <br>
 * 12.06.2013, 02.06.2014
 * <p>
 * 
 * Die Klasse ControlPanel verwaltet die Spielanzeige und den Spielstand (Klassen Anzeige
 * und Game) und stellt eine Benutzeroberfläche zur Verwaltung des Spiels bereit.
 * 
 * @author Lutz Althüser
 * @author Simon May
 */
public class ControlPanel implements ActionListener, GameListener {

	// Frame/Fenster der Bediener/Benutzeroberfläche des Physikerduells
	private JFrame frmBedienoberflche;
	private JTextField txtTeam1Name;
	private JTextField txtTeam2Name;
	private JTextField txtTeam1GPunkte;
	private JTextField txtTeam2GPunkte;
	private JLabel txtAPunkte;
	private JTextField txtALeben;
	private JButton btnFalscheAntwort;
	private JButton btnAbspannWechsel;
	private JButton btnNaechsteFrage;
	private JButton btnStart;
	private JButton btnOpenLog;
	private JComboBox<String> cbFragenauswahl;
	private JPanel pTeamauswahl;
	private JCheckBox chckbxAntwort1;
	private JCheckBox chckbxAntwort2;
	private JCheckBox chckbxAntwort3;
	private JCheckBox chckbxAntwort4;
	private JCheckBox chckbxAntwort5;
	private JCheckBox chckbxAntwort6;
	private JLabel lblAntwort1;
	private JLabel lblAntwort2;
	private JLabel lblAntwort3;
	private JLabel lblAntwort4;
	private JLabel lblAntwort5;
	private JLabel lblAntwort6;
	private JRadioButton rdbtnTeam1;
	private JRadioButton rdbtnTeam2;
	private JRadioButton rdbtnRunde1;
	private JRadioButton rdbtnRunde2;
	private JRadioButton rdbtnRunde3;
	private JRadioButton rdbtnRunde4;
	private JRadioButton rdbtnRunde5;
	private JRadioButton rdbtnNoTeam;
	// Zwei ButtonGroups, in denen die Radiobuttons organisiert werden.
	// Es kann nur ein Radiobutton pro Gruppe selektiert sein.
	private ButtonGroup activeTeam;
	private ButtonGroup activeRound;
	// Die beiden Objekte der Spielmechanik
	private Game duell;
	private Display fenster;
	// Das Objekt des SoundPlayers
	private PlayerApplet soundplayer;
	// Verschiedene Variablen der Spielmechanik
	private boolean punkteklau = false;
	private boolean rundenende;
	private int roundEndCtr;

	/**
	 * Die <code>main</code>-Methode soll zum Anstoß geben, dass die Objekte initialisiert
	 * werden, angefangen mit dem ControlPanel.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					ControlPanel window = new ControlPanel();
					window.frmBedienoberflche.setVisible(true);
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
		// Spielstand initialisieren
		duell = new Game(getClass().getResourceAsStream("/res/Katalog01.csv"));
		duell.setCurrentLives(0);
		duell.setCurrentQuestionIndex(0);
		duell.setCurrentRound(1);
		duell.setCurrentScore(0);
		duell.setTeam1Score(0);
		duell.setTeam2Score(0);
		duell.setCurrentTeam(-1);
		duell.setLogging(true);
		soundplayer = new PlayerApplet();
		// ComboBox mit Fragen auffüllen
		cbFragenauswahl.removeAllItems();
		for (int i = 0; i < duell.questionCount(); i++) {
			cbFragenauswahl.addItem(duell.getQuestion(i).getText());
		}
		// Spielfenster erstellen
		fenster = new Display(duell);
		fenster.setUndecorated(true);
		fenster.setAlwaysOnTop(!frmBedienoberflche.isAlwaysOnTop());
		fenster.setVisible(true);
		// Anzeige und Bedienoberfläche erhalten Spiel-Updates
		duell.addListener(fenster);
		duell.addListener(this);
	}

	/**
	 * ActionListener für Buttons und Textfelder.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnStart) {
			btnAbspannWechsel.setEnabled(true);
			fenster.playIntro();
		}
		else if (e.getSource() == btnAbspannWechsel) {
			fenster.playOutro();
		}
		else if (e.getSource() == btnFalscheAntwort) {
			eventWrongAnswer(e);
		}
		else if (e.getSource() == btnNaechsteFrage) {
			eventNextQuestion(e);
		}
		else if (e.getSource() == rdbtnTeam1 || e.getSource() == rdbtnTeam2) {
			if (e.getSource() == rdbtnTeam1) {
				duell.setCurrentTeam(1);
			}
			else {
				duell.setCurrentTeam(2);
			}
			duell.setCurrentLives(Game.MAX_LIVES);
		}
		else if (e.getSource() == rdbtnNoTeam) {
			duell.setCurrentTeam(-1);
			duell.setCurrentLives(Game.MAX_LIVES);
		}
		else if (e.getSource() == txtTeam1Name || e.getSource() == txtTeam2Name) {
			checkTeamNameInput();
		}
		else if (e.getSource() == txtTeam1GPunkte) {
			duell.setTeam1Score(Integer.parseInt(txtTeam1GPunkte.getText()));
		}
		else if (e.getSource() == txtTeam2GPunkte) {
			duell.setTeam2Score(Integer.parseInt(txtTeam2GPunkte.getText()));
		}
		else if (e.getSource() == txtALeben) {
			duell.setCurrentLives(Integer.parseInt(txtALeben.getText()));
		}
		else if (e.getSource() == btnOpenLog) {
			try {
				Desktop.getDesktop().open(new File(Game.LOG_FILE_PATH));
			}
			catch (Exception ex) {
				System.err.println("Error reading log file: " + ex);
				JOptionPane.showMessageDialog(frmBedienoberflche,
					"Could not read log file!");
			}
		}
		else if (e.getSource() == cbFragenauswahl) {
			int selected = cbFragenauswahl.getSelectedIndex();
			// Ausgewählte Frage übernehmen, wenn andere Frage ausgewählt wurde
			if (selected >= 0 && selected != duell.getCurrentQuestionIndex()) {
				duell.setCurrentQuestionIndex(selected);
				rundenende = false;
			}
		}
		// RadioButtons (rounds)
		for (int i = 1; i <= Game.NUM_ROUNDS; i++) {
			JRadioButton rdo = (JRadioButton) getComponentByName("rdbtnRunde" + i);
			if (e.getSource().equals(rdo)) {
				duell.setCurrentRound(i);
				break;
			}
		}
		// CheckBoxes (answers)
		for (int i = 0; i < Game.MAX_ANSWERS; i++) {
			JCheckBox chkbx = (JCheckBox) getComponentByName("chckbxAntwort" + (i + 1));
			if (e.getSource() == chkbx) {
				Answer ans = duell.getCurrentQuestion().getAnswer(i);
				ans.setRevealed(chkbx.isSelected());
				// Wenn richtig: Sound abspielen; Kein Sound, wenn die Runde vorbei ist!
				if (ans.isRevealed() && !rundenende) {
					playSound("Right.mp3");
				}
				// Bei Punkteklau: Erfolgreich geklaut, sonst normale richtige Antwort
				updateCurrentScore(punkteklau);
				break;
			}
		}
	}

	/**
	 * Update UI if the game state changes.
	 */
	@Override
	public void gameUpdate() {
		// Insert team data into TextFields
		txtTeam1Name.setText(duell.getTeam1Name());
		txtTeam2Name.setText(duell.getTeam2Name());
		txtTeam1GPunkte.setText(String.valueOf(duell.getTeam1Score()));
		txtTeam2GPunkte.setText(String.valueOf(duell.getTeam2Score()));
		// Insert current team data into TextFields
		txtALeben.setText(String.valueOf(duell.getCurrentLives()));
		txtAPunkte.setText(String.valueOf(duell.getCurrentScore()));
		// Select team RadioButton according to selected team
		int currTeam = duell.getCurrentTeam();
		switch (currTeam) {
		case 1:
			activeTeam.setSelected(rdbtnTeam1.getModel(), true);
			break;
		case 2:
			activeTeam.setSelected(rdbtnTeam2.getModel(), true);
			break;
		case -1:
			activeTeam.setSelected(rdbtnNoTeam.getModel(), true);
			break;
		}
		// Select round RadioButton according to selected round
		JRadioButton rdoRound =
			(JRadioButton) getComponentByName("rdbtnRunde" + duell.getCurrentRound());
		activeRound.setSelected(rdoRound.getModel(), true);
		// Select ComboBox item according to selected question
		int currIndex = duell.getCurrentQuestionIndex();
		cbFragenauswahl.setSelectedIndex(currIndex);
		// Set answer CheckBox text and score Label text
		// Enable/disable and select/deselect answer CheckBoxes
		Question currQuestion = duell.getCurrentQuestion();
		for (int i = 0; i < Game.MAX_ANSWERS; i++) {
			JCheckBox chkAns = (JCheckBox) getComponentByName("chckbxAntwort" + (i + 1));
			JLabel lblAns = (JLabel) getComponentByName("lblAntwort" + (i + 1));
			Answer ans = currQuestion.getAnswer(i);
			String chkText = ans.getText();
			String lblText = String.valueOf(ans.getScore());
			boolean enabled = i < duell.numberOfAnswers();
			// No text if question 0 (test question)
			if (currIndex == 0) {
				chkText = lblText = "";
			}
			// Deactivate if question 0 (test question) or if no team is selected
			enabled = currIndex != 0 && currTeam != -1;
			chkAns.setText(chkText);
			chkAns.setEnabled(enabled);
			chkAns.setSelected(ans.isRevealed());
			lblAns.setText(lblText);
		}
		// Enable/disable button for next question
		boolean nextQ = duell.getCurrentQuestionIndex() + 1 < duell.questionCount();
		btnNaechsteFrage.setEnabled(nextQ);
	}

	/**
	 * Checks if the inputted team names are valid.
	 */
	private void checkTeamNameInput() {
		txtTeam1Name.setBackground(Color.WHITE);
		txtTeam2Name.setBackground(Color.WHITE);
		String team1Name = txtTeam1Name.getText();
		String team2Name = txtTeam2Name.getText();
		if (!team1Name.isEmpty() && !team2Name.isEmpty() && team1Name.length() <= 20
			&& team2Name.length() <= 20) {
			duell.setTeam1Name(team1Name);
			duell.setTeam2Name(team2Name);
			rdbtnTeam1.setEnabled(true);
			rdbtnTeam2.setEnabled(true);
			rdbtnNoTeam.setEnabled(true);
			txtALeben.setEnabled(true);
			cbFragenauswahl.setEnabled(true);
			for (int i = 1; i <= Game.NUM_ROUNDS; i++) {
				JRadioButton rdo = (JRadioButton) getComponentByName("rdbtnRunde" + i);
				rdo.setEnabled(true);
			}
			btnFalscheAntwort.setEnabled(true);
			btnNaechsteFrage.setEnabled(true);
			btnStart.setEnabled(true);
		}
		else {
			btnStart.setEnabled(false);
			if (team1Name.isEmpty() || team1Name.length() >= 20) {
				txtTeam1Name.setBackground(Color.RED);
			}
			if (team2Name.isEmpty() || team2Name.length() >= 20) {
				txtTeam2Name.setBackground(Color.RED);
			}
		}
	}

	/**
	 * Event Handler for <code>btnNaechsteFrage</code>.
	 * 
	 * @param e
	 *            The event's ActionEvent
	 */
	private void eventNextQuestion(ActionEvent e) {
		if (rundenende && roundEndCtr == 2) {
			duell.setCurrentTeam(-1);
			duell.setCurrentLives(Game.MAX_LIVES);
			duell.setCurrentRound(duell.getCurrentRound() % Game.NUM_ROUNDS + 1);
			rundenende = false;
			roundEndCtr = 0;
		}
		int currIndex = duell.getCurrentQuestionIndex();
		if (currIndex + 1 < duell.questionCount()) {
			// Einführen der nächsten Frage
			duell.setCurrentQuestionIndex(currIndex + 1);
		}
	}

	/**
	 * Event Handler for <code>btnFalscheAntwort</code>.
	 * 
	 * @param e
	 *            The event's ActionEvent
	 */
	private void eventWrongAnswer(ActionEvent e) {
		// Keine Aktion wenn kein Team ausgewählt
		if (duell.getCurrentTeam() == -1) {
			return;
		}
		// Leben runtersetzen; wenn kein Leben: Teamwechsel
		if (duell.getCurrentLives() > 1 && !punkteklau) {
			duell.setCurrentLives(duell.getCurrentLives() - 1);
		}
		else if (punkteklau) {
			// Punkteklau nicht erfolgreich; Punkte aktualisieren
			updateCurrentScore(false);
		}
		else {
			duell.setCurrentLives(Game.MAX_LIVES);
			punkteklau = true;
			if (duell.getCurrentTeam() == 1) {
				duell.setCurrentTeam(2);
			}
			else if (duell.getCurrentTeam() == 2) {
				duell.setCurrentTeam(1);
			}
		}
		playSound("Wrong.mp3");
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
	 * (Automatisch generiert) Hilfsmethode des Konstruktors. Erzeugt die Elemente der
	 * GUI.
	 */
	private void initializeUI() {
		frmBedienoberflche = new JFrame();
		frmBedienoberflche.setIconImage(Toolkit.getDefaultToolkit().getImage(
			ControlPanel.class.getResource("/res/Physikerduell-0.png")));
		frmBedienoberflche.setAlwaysOnTop(true);
		frmBedienoberflche.setTitle("Physikerduell - Bedienoberfläche");
		frmBedienoberflche.setResizable(false);
		frmBedienoberflche.getContentPane().setBackground(Color.WHITE);
		frmBedienoberflche.setBounds(100, 100, 1024, 716);
		frmBedienoberflche.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmBedienoberflche.setLocationRelativeTo(null);

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
		frmBedienoberflche.getContentPane().setLayout(null);

		JLabel lblHeadline = new JLabel("Bedienoberfläche des Physikerduells");
		lblHeadline.setBounds(0, 0, 1018, 38);
		lblHeadline.setFont(new Font("Tahoma", Font.BOLD, 20));
		lblHeadline.setHorizontalAlignment(SwingConstants.CENTER);
		frmBedienoberflche.getContentPane().add(lblHeadline);

		JLabel lblTeam1Name = new JLabel("Teamname von Team 1");
		lblTeam1Name.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblTeam1Name.setBounds(10, 38, 150, 25);
		frmBedienoberflche.getContentPane().add(lblTeam1Name);

		txtTeam1Name = new JTextField();
		txtTeam1Name
			.setToolTipText("Teamname von Team 1 - Mit ENTER die Eingabe bestätigen!");
		txtTeam1Name.setBounds(20, 60, 472, 25);
		txtTeam1Name.setColumns(1);
		txtTeam1Name.setText("");
		frmBedienoberflche.getContentPane().add(txtTeam1Name);

		JLabel lblTeam1GPunkte = new JLabel("Gesamtpunkte von Team 1");
		lblTeam1GPunkte.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblTeam1GPunkte.setBounds(10, 100, 180, 25);
		frmBedienoberflche.getContentPane().add(lblTeam1GPunkte);

		txtTeam1GPunkte = new JTextField();
		txtTeam1GPunkte.setToolTipText("Mit ENTER die Eingabe bestätigen!");
		txtTeam1GPunkte.setText("0");
		txtTeam1GPunkte.setHorizontalAlignment(SwingConstants.CENTER);
		txtTeam1GPunkte.setFont(new Font("Tahoma", Font.BOLD, 15));
		txtTeam1GPunkte.setBounds(200, 100, 100, 25);
		txtTeam1GPunkte.setColumns(1);
		frmBedienoberflche.getContentPane().add(txtTeam1GPunkte);

		JLabel lblTeam2Name = new JLabel("Teamname von Team 2");
		lblTeam2Name.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblTeam2Name.setBounds(522, 38, 150, 25);
		frmBedienoberflche.getContentPane().add(lblTeam2Name);

		txtTeam2Name = new JTextField();
		txtTeam2Name
			.setToolTipText("Teamname von Team 2 - Mit ENTER die Eingabe bestätigen!");
		txtTeam2Name.setBounds(532, 60, 472, 25);
		txtTeam2Name.setColumns(1);
		txtTeam2Name.setText("");
		frmBedienoberflche.getContentPane().add(txtTeam2Name);

		JLabel lblTeam2GPunkte = new JLabel("Gesamtpunkte von Team 2");
		lblTeam2GPunkte.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblTeam2GPunkte.setBounds(522, 100, 180, 25);
		frmBedienoberflche.getContentPane().add(lblTeam2GPunkte);

		txtTeam2GPunkte = new JTextField();
		txtTeam2GPunkte.setToolTipText(" Mit ENTER die Eingabe bestätigen!");
		txtTeam2GPunkte.setText("0");
		txtTeam2GPunkte.setHorizontalAlignment(SwingConstants.CENTER);
		txtTeam2GPunkte.setFont(new Font("Tahoma", Font.BOLD, 15));
		txtTeam2GPunkte.setBounds(712, 100, 100, 25);
		txtTeam2GPunkte.setColumns(1);
		frmBedienoberflche.getContentPane().add(txtTeam2GPunkte);

		btnStart = new JButton("Vorspann und Start (Mehrfachfunktion)");
		btnStart.setEnabled(false);
		btnStart.setBounds(10, 150, 390, 50);
		frmBedienoberflche.getContentPane().add(btnStart);

		btnAbspannWechsel = new JButton("Abspann");
		btnAbspannWechsel.setEnabled(false);
		btnAbspannWechsel.setBounds(10, 211, 390, 50);
		frmBedienoberflche.getContentPane().add(btnAbspannWechsel);

		JLabel lblAktuellesTeam = new JLabel("Aktuelles Team");
		lblAktuellesTeam.setHorizontalAlignment(SwingConstants.CENTER);
		lblAktuellesTeam.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblAktuellesTeam.setBounds(410, 150, 100, 25);
		frmBedienoberflche.getContentPane().add(lblAktuellesTeam);

		pTeamauswahl = new JPanel();
		pTeamauswahl.setBounds(410, 175, 100, 90);
		frmBedienoberflche.getContentPane().add(pTeamauswahl);

		rdbtnTeam1 = new JRadioButton("Team 1");
		rdbtnTeam1.setEnabled(false);
		pTeamauswahl.add(rdbtnTeam1);

		rdbtnTeam2 = new JRadioButton("Team 2");
		rdbtnTeam2.setEnabled(false);
		pTeamauswahl.add(rdbtnTeam2);

		JLabel lblAPunkte = new JLabel("Punkte des Aktuellen Teams");
		lblAPunkte.setHorizontalAlignment(SwingConstants.LEFT);
		lblAPunkte.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblAPunkte.setBounds(522, 150, 210, 25);
		frmBedienoberflche.getContentPane().add(lblAPunkte);

		txtAPunkte = new JLabel();
		txtAPunkte.setToolTipText("");
		txtAPunkte.setHorizontalAlignment(SwingConstants.CENTER);
		txtAPunkte.setText("0");
		txtAPunkte.setFont(new Font("Tahoma", Font.BOLD, 15));
		txtAPunkte.setBounds(562, 175, 100, 25);
		frmBedienoberflche.getContentPane().add(txtAPunkte);

		JLabel lblALeben = new JLabel("Leben des Aktuellen Teams");
		lblALeben.setHorizontalAlignment(SwingConstants.LEFT);
		lblALeben.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblALeben.setBounds(522, 202, 210, 25);
		frmBedienoberflche.getContentPane().add(lblALeben);

		txtALeben = new JTextField();
		txtALeben.setToolTipText("Mit ENTER die Eingabe bestätigen!");
		txtALeben.setEnabled(false);
		txtALeben.setHorizontalAlignment(SwingConstants.CENTER);
		txtALeben.setText("0");
		txtALeben.setFont(new Font("Tahoma", Font.BOLD, 15));
		txtALeben.setBounds(562, 226, 100, 25);
		txtALeben.setColumns(1);
		frmBedienoberflche.getContentPane().add(txtALeben);

		JPanel pRundenauswahl = new JPanel();
		pRundenauswahl.setBounds(10, 280, 1000, 90);
		frmBedienoberflche.getContentPane().add(pRundenauswahl);
		pRundenauswahl.setLayout(null);

		JLabel lblRundenauswahl = new JLabel("Rundenauswahl");
		lblRundenauswahl.setBounds(412, 5, 174, 14);
		lblRundenauswahl.setHorizontalAlignment(SwingConstants.CENTER);
		lblRundenauswahl.setFont(new Font("Tahoma", Font.BOLD, 11));
		pRundenauswahl.add(lblRundenauswahl);

		rdbtnRunde1 = new JRadioButton("Runde 1");
		rdbtnRunde1.setSelected(true);
		rdbtnRunde1.setEnabled(false);
		rdbtnRunde1.setBounds(110, 32, 100, 25);
		pRundenauswahl.add(rdbtnRunde1);

		rdbtnRunde2 = new JRadioButton("Runde 2");
		rdbtnRunde2.setEnabled(false);
		rdbtnRunde2.setBounds(288, 33, 100, 25);
		pRundenauswahl.add(rdbtnRunde2);

		rdbtnRunde3 = new JRadioButton("Runde 3");
		rdbtnRunde3.setEnabled(false);
		rdbtnRunde3.setBounds(466, 33, 100, 25);
		pRundenauswahl.add(rdbtnRunde3);

		rdbtnRunde4 = new JRadioButton("Runde 4");
		rdbtnRunde4.setEnabled(false);
		rdbtnRunde4.setBounds(644, 33, 100, 25);
		pRundenauswahl.add(rdbtnRunde4);

		rdbtnRunde5 = new JRadioButton("Runde 5");
		rdbtnRunde5.setEnabled(false);
		rdbtnRunde5.setBounds(822, 33, 100, 25);
		pRundenauswahl.add(rdbtnRunde5);

		frmBedienoberflche.getContentPane().add(cbFragenauswahl);
		frmBedienoberflche.getContentPane().add(pAntwortmoeglichkeiten);
		frmBedienoberflche.getContentPane().add(btnFalscheAntwort);

		btnNaechsteFrage = new JButton(">> Nächste Frage <<");
		btnNaechsteFrage.setEnabled(false);
		btnNaechsteFrage.setBounds(10, 645, 492, 30);
		frmBedienoberflche.getContentPane().add(btnNaechsteFrage);

		rdbtnNoTeam = new JRadioButton("Kein Team");
		rdbtnNoTeam.setSelected(true);
		rdbtnNoTeam.setEnabled(false);
		pTeamauswahl.add(rdbtnNoTeam);

		// Gruppierung der RadioButtons zur Auswahl des Teams
		activeTeam = new ButtonGroup();
		activeTeam.add(rdbtnNoTeam);
		activeTeam.add(rdbtnTeam1);
		activeTeam.add(rdbtnTeam2);

		// Gruppierung der RadioButtons zur Auswahl der Runde
		activeRound = new ButtonGroup();
		activeRound.add(rdbtnRunde1);
		activeRound.add(rdbtnRunde2);
		activeRound.add(rdbtnRunde3);
		activeRound.add(rdbtnRunde4);
		activeRound.add(rdbtnRunde5);

		// Initialisierung der verschiedenen Komponenten 
		chckbxAntwort1.setText("");
		chckbxAntwort2.setText("");
		chckbxAntwort3.setText("");
		chckbxAntwort4.setText("");
		chckbxAntwort5.setText("");
		chckbxAntwort6.setText("");

		lblAntwort1.setText("");
		lblAntwort2.setText("");
		lblAntwort3.setText("");
		lblAntwort4.setText("");
		lblAntwort5.setText("");
		lblAntwort6.setText("");

		btnStart.addActionListener(this);
		btnAbspannWechsel.addActionListener(this);
		btnFalscheAntwort.addActionListener(this);
		btnNaechsteFrage.addActionListener(this);
		rdbtnTeam1.addActionListener(this);
		rdbtnTeam2.addActionListener(this);
		txtTeam1Name.addActionListener(this);
		txtTeam2Name.addActionListener(this);
		txtTeam1GPunkte.addActionListener(this);
		txtTeam2GPunkte.addActionListener(this);
		txtALeben.addActionListener(this);
		rdbtnRunde1.addActionListener(this);
		rdbtnRunde2.addActionListener(this);
		rdbtnRunde3.addActionListener(this);
		rdbtnRunde4.addActionListener(this);
		rdbtnRunde5.addActionListener(this);
		rdbtnNoTeam.addActionListener(this);

		chckbxAntwort1.addActionListener(this);
		chckbxAntwort2.addActionListener(this);
		chckbxAntwort3.addActionListener(this);
		chckbxAntwort4.addActionListener(this);
		chckbxAntwort5.addActionListener(this);
		chckbxAntwort6.addActionListener(this);

		cbFragenauswahl.addActionListener(this);

		btnOpenLog = new JButton("Open Logfile");
		btnOpenLog.setBounds(628, 649, 100, 23);
		frmBedienoberflche.getContentPane().add(btnOpenLog);
		btnOpenLog.addActionListener(this);

		JTextPane txtpnBeschreibung = new JTextPane();
		txtpnBeschreibung.setEditable(false);
		txtpnBeschreibung.setFont(new Font("Tahoma", Font.PLAIN, 13));
		txtpnBeschreibung.setText("Physikerduell 2014:\n\n"
			+ "Alle Eingaben müssen mit der ENTER-Taste bestätigt werden!\n"
			+ "Auflösung sollte 1024x768 sein!\n\nAblauf:\n"
			+ "1) Eintragen der Teamnamen.\n"
			+ "2) Starten des Spiels -> Nur Musik -> Spieloberfläche -> ggf. "
			+ "Musik aus.\n"
			+ "3) Frage wählen/Nächste Frage drücken. (Runden passen sich "
			+ "automatisch an.)\n" + "4) ggf. Teams wechseln\n"
			+ "5) Zwischen zwei Spielen den Abspann anzeigen.\n\n\n\n\n");
		txtpnBeschreibung.setBounds(512, 381, 496, 259);
		frmBedienoberflche.getContentPane().add(txtpnBeschreibung);

		JTextPane tytpnImpressum = new JTextPane();
		tytpnImpressum.setFont(new Font("Tahoma", Font.PLAIN, 13));
		tytpnImpressum.setEditable(false);
		tytpnImpressum.setText("Version 2014"
			+ "\t\t  Lutz Althüser, Maik Stappers, Simon May");
		tytpnImpressum.setBounds(512, 651, 496, 25);
		frmBedienoberflche.getContentPane().add(tytpnImpressum);
		frmBedienoberflche.getContentPane().setFocusTraversalPolicy(
			new FocusTraversalOnArray(new Component[] {lblHeadline, lblTeam1Name,
				txtTeam1Name, lblTeam1GPunkte, txtTeam1GPunkte, lblTeam2Name,
				txtTeam2Name, lblTeam2GPunkte, txtTeam2GPunkte, btnStart,
				btnAbspannWechsel, lblAktuellesTeam, pTeamauswahl, lblAPunkte,
				txtAPunkte, lblALeben, txtALeben, pRundenauswahl, lblRundenauswahl,
				cbFragenauswahl, pAntwortmoeglichkeiten, chckbxAntwort1, chckbxAntwort2,
				chckbxAntwort3, chckbxAntwort4, chckbxAntwort5, chckbxAntwort6,
				btnFalscheAntwort, btnNaechsteFrage}));
		frmBedienoberflche.setFocusTraversalPolicy(new FocusTraversalOnArray(
			new Component[] {txtTeam1Name, txtTeam2Name, txtTeam1GPunkte,
				txtTeam2GPunkte, btnStart, btnAbspannWechsel, txtAPunkte, txtALeben,
				cbFragenauswahl, chckbxAntwort1, chckbxAntwort2, chckbxAntwort3,
				chckbxAntwort4, chckbxAntwort5, chckbxAntwort6, btnFalscheAntwort,
				btnNaechsteFrage}));
	}

	/**
	 * 
	 * Plays the specified audio file.
	 * 
	 * @param name
	 *            Audio file name (in the "res" directory)
	 */
	private void playSound(String name) {
		final String resource = "/res/" + name;
		new Thread(new Runnable() {
			@Override
			public void run() {
				soundplayer.start(getClass().getResourceAsStream(resource));
			}
		}).start();
	}

	/**
	 * Updates the current round's accumulated score or the teams' scores at the end of a
	 * round.
	 */
	private void updateCurrentScore(boolean stealSuccess) {
		Question curr = duell.getCurrentQuestion();
		int answers = duell.numberOfAnswers();
		int score = 0;
		int revealedanswers = 0;
		for (int i = 0; i < Game.MAX_ANSWERS; i++) {
			if (curr.getAnswer(i).isRevealed()) {
				revealedanswers++;
				score += curr.getAnswerScore(i);
			}
		}
		duell.setCurrentScore(score);
		// Gesamtpunkte updaten
		// Bei falscher Antwort und Punkteklau erhält das andere Team die Punkte,
		// bei richtiger Antwort und Punkteklau oder bei bei Rundenende erhält das
		// aktuelle Team die Punkte
		boolean allAnswers = revealedanswers == answers;
		if (punkteklau || allAnswers) {
			int team = duell.getCurrentTeam();
			int updScore = duell.totalCurrentScore();
			if (stealSuccess || allAnswers) {
				if (team == 1) {
					duell.setTeam1Score(duell.getTeam1Score() + updScore);
				}
				else if (team == 2) {
					duell.setTeam2Score(duell.getTeam2Score() + updScore);
				}
			}
			// Punkteklau fehlgeschlagen => anderes Team erhält Punkte
			else {
				if (team == 1) {
					duell.setTeam2Score(duell.getTeam2Score() + updScore);
				}
				else if (team == 2) {
					duell.setTeam1Score(duell.getTeam1Score() + updScore);
				}
			}
			duell.setCurrentTeam(-1);
			rundenende = true;
			roundEndCtr++;
			punkteklau = false;
		}
	}
}
