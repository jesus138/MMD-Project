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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import main.Command;
import main.Server;

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
	private JTextField guessField;
	private JLabel portLabel;
	private JTextField portField;
	private JButton setupBtn;
	private JLabel hostLabel;
	private JTextField hostField;
	
	private JMenuBar menubar;
	private JMenu menu;
	private JMenuItem exitItem, hideItem, showItem, highscoreItem, disconnectItem, clearMessageItem, clearCourseItem;
	
	private Server server;
	
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
		lengthLabel = new JLabel("Codelänge");
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
		guessField = new JTextField(String.valueOf(Command.DEFAULT_TRIES), 10);
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
		clearMessageItem = new JMenuItem("Nachrichten löschen");
		clearMessageItem.addActionListener(this);
		clearCourseItem = new JMenuItem("Verlauf löschen");
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
	
	public void appendMessage(String msg){
		String pre = messageBox.getText();
		messageBox.setText(msg + "\n" + pre);
	}
	
	public void clearMessages(){
		messageBox.setText("");
	}
	
	public void clearHistory(){
		historyPanel.clearHistory((Integer)lengthbox.getSelectedItem());
	}
	
	public void showMessage(String title, String msg){
		JOptionPane.showMessageDialog(this, msg, title, JOptionPane.INFORMATION_MESSAGE);
	}
	
	public String getChosenCode(){
		return codePanel.getCode();
	}
	
	public void setCode(String code){
		codePanel.setCode(code);
	}
	
	public void addGuess(Color[] colors, String rescode){
		historyPanel.addButtons(colors, rescode);
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

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == chooseBtn){
			new ServerColorChooser();
		}
		else if(e.getSource() == setupBtn){
			boolean automatic = modebox.getSelectedIndex() == 0;
			int codelength = (Integer)lengthbox.getSelectedItem();
			String colors = Command.getStringCode(palette);
			int tries = Integer.parseInt(guessField.getText());
			int port = Integer.parseInt(portField.getText());
			server.configure(automatic, codelength, colors, tries, port);
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
		else if(e.getSource() == highscoreItem){}
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

	@Override
	public void itemStateChanged(ItemEvent e) {
		if(e.getSource() == modebox){
			if(modebox.getSelectedIndex() == 0)
				codePanel.setDisabled(true);
			else
				codePanel.setDisabled(false);
		}
		else if(e.getSource() == lengthbox){
			codePanel.changePanel(palette, (Integer)lengthbox.getSelectedItem());
			historyPanel.clearHistory((Integer)lengthbox.getSelectedItem());
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
			setTitle("Farben auswwählen");
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
				codePanel.changePanel(palette, (Integer)lengthbox.getSelectedItem());
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
