package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import main.Command;
import main.Server;

/**
 * Repraesentiert die Benutzeroberflaeche des Server-Programms und liefert dessen UI-Thread.
 * Es untergliedert sich in einen rechten Einstellungsbereich und einen linken Spielbereich,
 * der ein CodePanel und ein HistoryPanel beinhaltet. Ausserdem wird eine symmetrische
 * Assoziation zur Server Klasse hergestellt. Nebensaechliche Nachrichten bezueglich der Client-Server
 * Kommunikation werden in der Nachrichtenbox unten rechts angezeigt. Wichtige Nachrichten werden
 * ueber einen Dialog repraesentiert.<br/>
 * Der Einstellungsbereich besitzt einen Aktualisierungsbutton der alle Einstellungen gueltig macht
 * und ansonsten JComboBoxes fuer den Modus und die Codelaenge, sowie einen JSpinner fuer die Anzahl
 * der Rateversuche. Ausserdem kann der Port manuell geaendert werden. Weitere Funktionen, wie das
 * Anzeigen der Highscore oder trennen einer aktuell bestehenden Verbindung sind ueber die JMenuBar zu
 * erreichen.<br/>
 * Die ServerGui beinhaltet ausserdem eine private JDialog Subklasse namens ServerColorChooser. Mit
 * diesem JDialog laesst sich komfortabel die Farbpalette einstellen.<br/>
 * <b>Wichtig:</b> Das Schliessen der ServerGui veranlasst auch das Beenden des Server-Programms und
 * beendet somit ein eventuell laufendes Spiel.
 * @author Chris
 * @category UI-Thread
 */
@SuppressWarnings("serial")
public class ServerGui extends JFrame implements ActionListener, ItemListener
{	
	private Color[] palette;
	
	private ServerContainer c;
	
	private JPanel westPanel;
	private CodePanel codePanel;
	private HistoryPanel historyPanel;
	
	private JPanel eastPanel;
	private JPanel labelPanel, messagePanel;
	private JLabel settingsLabel;
	private JTextArea messageBox;
	private JScrollPane scroller;
	
	private JPanel settingsPanel;
	private JComboBox<String> modebox;
	private final static String[] MODES = {"Automatisch", "Manuell"};
	private JLabel lengthLabel;
	private JComboBox<Integer> lengthbox;
	private JButton chooseBtn;
	private JLabel guessLabel;
	private JSpinner guessField;
	private JLabel portLabel;
	private JTextField portField;
	private JButton setupBtn;
	private JLabel hostLabel;
	private JTextField hostField;
	
	private JMenuBar menubar;
	private JMenu menu;
	private JMenuItem exitItem, hideItem, showItem, highscoreItem, disconnectItem, clearMessageItem, clearCourseItem;
	
	private Server server;
	
	/**
	 * Konstruktor der ServerGui initialisert alle grafischen Komponenten des Serverprogramms.
	 * Es werden Listener fuer die Benutzerinteraktion registriert und private Methoden zur
	 * Erstellung der Teilbereiche aufgerufen. Diese Methoden sind initWest(), initEast() und
	 * initMenu(). Es werden auch alle Defaulteinstellungen des Mastermind-Servers beruecksichtigt.
	 * @param server Serverprogramm
	 */
	public ServerGui(Server server)
	{
		super("Mastermind Server");
		this.server = server;
		palette = Command.DEFAULT_SET;
		c = new ServerContainer();
		setContentPane(c);
		c.setLayout(new BorderLayout());
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowCloser());
		setResizable(false);
		initWest();
		initEast();
		initMenu();
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	private void initWest()
	{
		westPanel = new JPanel(new BorderLayout());
		westPanel.setOpaque(false);
		codePanel = new CodePanel("Farbcode", this, palette, Command.DEFAULT_CODELENGTH);
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				codePanel.setDisabled(true);
			}
		});
		historyPanel = new HistoryPanel(Command.COURSE_HEIGHT, this, Command.DEFAULT_CODELENGTH);
		westPanel.add(codePanel, BorderLayout.NORTH);
		westPanel.add(historyPanel, BorderLayout.CENTER);
		c.add(westPanel, BorderLayout.WEST);
	}
	
	private void initEast()
	{
		eastPanel = new JPanel(new BorderLayout());
		eastPanel.setOpaque(false);
		
		labelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
		labelPanel.setOpaque(false);
		settingsLabel = new JLabel("Einstellungen");
		settingsLabel.setFont(Command.CAPTION_FONT);
		settingsLabel.setHorizontalAlignment(SwingConstants.CENTER);
		labelPanel.add(settingsLabel);
		
		settingsPanel = new JPanel(new GridBagLayout());
		settingsPanel.setOpaque(false);
		modebox = new JComboBox<>(MODES);
		modebox.addItemListener(this);
		lengthLabel = new JLabel("Codelaenge");
		Vector<Integer> lengths = new Vector<>();
		for(int i=2; i<=15; i++) lengths.add(i);
		lengthbox = new JComboBox<>(lengths);
		lengthbox.setSelectedIndex(2);
		lengthbox.addItemListener(this);
		chooseBtn = new JButton("Farbpalette");
		chooseBtn.addActionListener(this);
		chooseBtn.setBackground(Color.white);
		chooseBtn.setPreferredSize(new Dimension(150, 40));
		guessLabel = new JLabel("Rateversuche");
		SpinnerModel model = new SpinnerNumberModel(Command.DEFAULT_TRIES, 0, Integer.MAX_VALUE, 1);
		guessField = new JSpinner(model);
		portLabel = new JLabel("Netzwerkport");
		portField = new JTextField(String.valueOf(Command.DEFAULT_PORT), 10);
		setupBtn = new JButton("Aktualisieren");
		setupBtn.addActionListener(this);
		setupBtn.setBackground(Color.white);
		setupBtn.setPreferredSize(new Dimension(150, 40));
		hostLabel = new JLabel("Host-/IP-Adresse");
		hostField = new JTextField(Command.DEFAULT_HOST, 10);
		hostField.setEnabled(false);
		GridBagConstraints cons = new GridBagConstraints();
		cons.insets = new Insets(10, 0, 10, 20);
		cons.fill = GridBagConstraints.HORIZONTAL;
		cons.ipady = 10;
		cons.gridwidth = 2;
		settingsPanel.add(modebox, cons);
		cons.gridy = 1;
		settingsPanel.add(chooseBtn, cons);
		cons.gridwidth = 1;
		cons.gridx = 0;
		cons.gridy = 2;
		settingsPanel.add(lengthLabel, cons);
		cons.gridx = 1;
		settingsPanel.add(lengthbox, cons);
		cons.gridx = 0;
		cons.gridy = 3;
		settingsPanel.add(guessLabel, cons);
		cons.gridx = 1;
		settingsPanel.add(guessField, cons);
		cons.gridx = 0;
		cons.gridy = 4;
		settingsPanel.add(portLabel, cons);
		cons.gridx = 1;
		settingsPanel.add(portField, cons);
		cons.gridx = 0;
		cons.gridy = 5;
		settingsPanel.add(hostLabel, cons);
		cons.gridx = 1;
		settingsPanel.add(hostField, cons);
		cons.gridx = 0;
		cons.gridy = 6;
		cons.gridwidth = 2;
		settingsPanel.add(setupBtn, cons);
		
		messagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
		messagePanel.setOpaque(false);
		messageBox = new JTextArea(10, 25);
		messageBox.setLineWrap(true);
		messageBox.setWrapStyleWord(true);
		scroller = new JScrollPane(messageBox, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		messagePanel.add(scroller);
		
		eastPanel.add(labelPanel, BorderLayout.NORTH);
		eastPanel.add(settingsPanel, BorderLayout.CENTER);
		eastPanel.add(messagePanel, BorderLayout.SOUTH);
		c.add(eastPanel, BorderLayout.EAST);
	}
	
	private void initMenu()
	{
		menubar = new JMenuBar();
		menu = new JMenu("Optionen");
		exitItem = new JMenuItem("Beenden");
		exitItem.addActionListener(this);
		hideItem = new JMenuItem("Code verstecken");
		hideItem.addActionListener(this);
		showItem = new JMenuItem("Code anzeigen");
		showItem.addActionListener(this);
		highscoreItem = new JMenuItem("Highscore");
		highscoreItem.addActionListener(this);
		disconnectItem = new JMenuItem("Sitzung beenden");
		disconnectItem.addActionListener(this);
		clearMessageItem = new JMenuItem("Nachrichten loeschen");
		clearMessageItem.addActionListener(this);
		clearCourseItem = new JMenuItem("Verlauf loeschen");
		clearCourseItem.addActionListener(this);
		menu.add(exitItem);
		menu.add(hideItem);
		menu.add(showItem);
		menu.add(highscoreItem);
		menu.add(disconnectItem);
		menu.add(clearMessageItem);
		menu.add(clearCourseItem);
		menubar.add(menu);
		setJMenuBar(menubar);
	}
	
	/**
	 * Versetzt die GUI in den Spielemodus.
	 * Waehrend diesem Zustand koennen keine weiteren
	 * Einstellungen vorgenommen werden. Nur ueber das Menue
	 * kann das aktuelle Spiel abgebrochen werden.
	 * @category Zustand
	 */
	public void setToPlayMode()
	{
		modebox.setEnabled(false);
		lengthbox.setEnabled(false);
		chooseBtn.setEnabled(false);
		guessField.setEnabled(false);
		portField.setEnabled(false);
		setupBtn.setEnabled(false);
		disconnectItem.setEnabled(true);
	}
	
	/**
	 * Im Einstellungsmodus werden alle Spieleparameter
	 * bis auf das zu erratende Codewort eingestellt.
	 * Damit die EInstellungen in Kraft treten muss der Aktualisierungsbutton
	 * gedrueckt werden. Beim Programmstart muss dies auch geschehen um den
	 * Server auf dem gewuenschten Port zu starten.
	 * @category Zustand
	 */
	public void setToSetupMode()
	{
		modebox.setEnabled(true);
		lengthbox.setEnabled(true);
		chooseBtn.setEnabled(true);
		guessField.setEnabled(true);
		portField.setEnabled(true);
		setupBtn.setEnabled(true);
		disconnectItem.setEnabled(false);
	}
	
	/**
	 * Fuegt eine Client-Server Nachricht in die Nachrichtenbox oben ein.
	 * @param msg Nachricht
	 */
	public void appendMessage(String msg){
		String pre = messageBox.getText();
		messageBox.setText(msg + "\n" + pre);
	}
	
	/**
	 * Entfernt alle Nachrichten aus der Nachrichtenbox.
	 */
	public void clearMessages(){
		messageBox.setText("");
	}
	
	/**
	 * Leert den Verlaufsbereich.
	 * Wird beim Start jeder Spielrunde automatisch aufgerufen. Kann
	 * aber auch manuell waehrend eines Spiels vom Menue aus aufgerufen werden.
	 */
	public void clearHistory(){
		historyPanel.clearHistory((Integer)lengthbox.getSelectedItem());
	}
	
	/**
	 * Erzeugt einen JOptionPane Nachrichtendialog.
	 * Findet bei der ServerGui hauptsaechlich fuer Fehlermeldungen bzw.
	 * getrennte Verbindungen Verwendung.
	 * @param title Titel der Dialogbox
	 * @param msg Nachricht im Dialogfenster
	 */
	public void showMessage(String title, String msg){
		JOptionPane.showMessageDialog(this, msg, title, JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**
	 * Liefert den vom Benutzer eingestellten Farbcode des CodePanels.
	 * Wird von der Server-Klasse im manuellen Modus aufgerufen.
	 * @return eingestellter Farbcode als String
	 */
	public String getChosenCode(){
		return codePanel.getCode();
	}
	
	/**
	 * Stellt die tatsaechliche Adresse des Servers dar.
	 * Dient zu administrativen Zwecken.
	 * @param address Hostadresse des Servers
	 */
	public void showHostAddress(String address){
		hostField.setText(address);
	}
	
	/**
	 * Dient dem Server zum setzten des zu erratenden Farbcodes.
	 * Wird im automatischen Modus verwendet, um das zufaellig generierte
	 * Codewort anzuzeigen.
	 * @param code Farbcode als Zeichenkette
	 */
	public void setCode(String code){
		codePanel.setCode(code);
	}
	
	/**
	 * Fuegt dem HistoryPanel eine neue Zeile hinzu.
	 * @param colors geratene Farben des Clients als Farbwerte
	 * @param rescode Resultat des Servers
	 */
	public void addGuess(Color[] colors, String rescode){
		historyPanel.addButtons(colors, rescode);
	}
	
	/**
	 * Versteckt das zu erratene Codewort.
	 */
	public void disableButtons()
	{
		codePanel.setDisabled(true);
	}
	
	private static class ServerContainer extends JPanel
	{
		@Override
		protected void paintComponent(Graphics gr) {
			super.paintComponent(gr);
			double r = 220D, g = 255D;
			double rx = (r-150.0)/(double)getHeight();
			double gx = (g-200.0)/(double)getHeight();
			for(int y=getHeight()-1; y>=0; y--){
				gr.setColor(new Color((int)r, (int)g, 255));
				gr.fillRect(0, y, getWidth(), 1);
				r-=rx;
				g-=gx;
			}
		}
	}

	/**
	 * Delegiert Klickereignisse an den Server oder startet entsprechende Dialoge.
	 * ueber die JMenuBar laesst sich das Codewort verstecken bzw. wieder anzeigen.
	 * Der Aktualisierungsbutton startet den Server neu auf den angegeben Port und
	 * initialisiert ihn mit den gemachten Einstellungen. Unter anderem laesst sich
	 * ueber das Menue auch die HighscoreGui oeffnen.
	 * @param e Ereignisobjekt
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == chooseBtn){
			new ServerColorChooser();
		}
		else if(e.getSource() == setupBtn){
			boolean automatic = modebox.getSelectedIndex() == 0;
			int codelength = (Integer)lengthbox.getSelectedItem();
			String colors = Command.getStringCode(palette);
			int tries = (Integer) guessField.getValue();
			int port = Integer.parseInt(portField.getText());
			server.configure(automatic, codelength, colors, tries, port);
			codePanel.changePanel(palette, codelength);
			historyPanel.clearHistory(codelength);
		}
		else if(e.getSource() == exitItem){
			server.disconnect();
			System.exit(0);
		}
		else if(e.getSource() == hideItem){
			codePanel.hideCode(true);
		}
		else if(e.getSource() == showItem){
			codePanel.hideCode(false);
		}
		else if(e.getSource() == highscoreItem){
			new HighscoreGui();
		}
		else if(e.getSource() == disconnectItem){
			server.disconnect();
		}
		else if(e.getSource() == clearMessageItem){
			messageBox.setText("");
		}
		else if(e.getSource() == clearCourseItem){
			clearHistory();
		}
	}

	/**
	 * Dient dazu dem Benutzer manuell das Ratewort einstellen zu lassen.
	 * @param e Ereignisobjekt
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	@Override
	public void itemStateChanged(ItemEvent e) {
		if(e.getSource() == modebox){
			if(modebox.getSelectedIndex() == 0)
				codePanel.setDisabled(true);
			else
				codePanel.setDisabled(false);
		}
	}
	
	private class ServerColorChooser extends JDialog implements ActionListener, ItemListener
	{
		private JPanel northPanel;
		private JPanel southPanel;
		private JPanel choosePanel;
		private JButton okBtn, caBtn;
		private JCheckBox[] checks;
		private JButton[] buttons;
		
		public ServerColorChooser()
		{
			setTitle("Farben auswwaehlen");
			setLayout(new BorderLayout());
			setModal(true);
			makeTop();
			makeBottom();
			pack();
			setLocationRelativeTo(ServerGui.this);
			setVisible(true);
			setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		}
		
		private void makeTop()
		{
			northPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
			choosePanel = new JPanel(new GridLayout(3, 5, 10, 10));
			
			checks = new JCheckBox[Command.COLORS.length];
			buttons = new JButton[Command.COLORS.length];
			for(int i=0; i<checks.length; i++)
			{
				JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
				panel.setPreferredSize(new Dimension(Command.BUTTON_SIZE, Command.BUTTON_SIZE+20));
				JButton button = buttons[i] = new JButton();
				button.setPreferredSize(new Dimension(Command.BUTTON_SIZE, Command.BUTTON_SIZE));
				button.setBackground(Command.COLORS[i]);
				boolean in = false;
				for(Color c : palette)
					if(c.equals(Command.COLORS[i])){
						in = true;
						break;
					}
				JCheckBox box = checks[i] = new JCheckBox();
				box.addItemListener(this);
				if(in) box.setSelected(true);
				panel.add(button);
				panel.add(box);
				choosePanel.add(panel);
			}
			
			northPanel.add(choosePanel);
			add(northPanel, BorderLayout.CENTER);
		}
		
		private void makeBottom()
		{
			southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
			okBtn = new JButton("Ok");
			okBtn.addActionListener(this);
			caBtn = new JButton("Abbrechen");
			caBtn.addActionListener(this);
			southPanel.add(okBtn);
			southPanel.add(caBtn);
			add(southPanel, BorderLayout.SOUTH);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == okBtn){
				Vector<Color> colors = new Vector<>();
				for(int i=0; i<checks.length; i++)
					if(checks[i].isSelected())
						colors.add(Command.COLORS[i]);
				palette = new Color[colors.size()];
				for(int i=0; i<palette.length; i++)
					palette[i] = colors.get(i);
				dispose();
			}
			else if(e.getSource() == caBtn)
				dispose();
		}

		@Override
		public void itemStateChanged(ItemEvent e) {
			if(e.getStateChange() == ItemEvent.DESELECTED)
			{
				int num = 0;
				for(JCheckBox box : checks)
					if(box.isSelected()) num++;
				if(num < 2) ((JCheckBox)e.getSource()).setSelected(true);
			}
		}
	}
	
	private class WindowCloser extends WindowAdapter
	{
		@Override
		public void windowClosing(WindowEvent e) {
			server.disconnect();
			System.exit(0);
		}
	}
}
