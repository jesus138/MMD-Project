package mastermind;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
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

@SuppressWarnings("serial")
public class ClientGui extends JFrame implements ActionListener
{
	private static final Font CAPFONT = new Font("SansSerif", Font.BOLD, 30);
	private Client client;
	private String colorcode = "123456";
	
	private Container area;
	private GameArea gameArea;
	private JPanel setupPanel;
	private JPanel setLabelPanel;
	private JLabel settingsLabel;
	
	// Einstellungsmöglichkeiten
	private JPanel configPanel;
	private JLabel nameLabel;
	private JLabel hostLabel;
	private JLabel portLabel;
	private JTextField nameField;
	private JTextField hostField;
	private JTextField portField;
	private JButton connectBtn;
	private JButton newBtn;
	private JButton guessBtn;
	private JButton quitBtn;
	private JButton autoBtn;
	
	// Informationspanel
	private JPanel infoPanel;
	private JScrollPane scrollpane;
	private JTextArea textfield;
	
	// Menü
	private JMenuBar menuBar;
	private JMenu menu;
	private JMenuItem itemQuit;
	
	public ClientGui(Client client)
	{
		super("Mastermind Client");
		this.client = client;
		area = getContentPane();
		area.setLayout(new BorderLayout());
		initComponents();
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	public void initComponents()
	{
		addMenu();
		makeConfigPanel();
		makeInfoPanel();
		makeSetupPanel();
		gameArea = new GameArea();
		setupPanel.add(setLabelPanel, BorderLayout.NORTH);
		setupPanel.add(configPanel, BorderLayout.CENTER);
		setupPanel.add(infoPanel, BorderLayout.SOUTH);
		area.add(gameArea.getPanel(), BorderLayout.WEST);
		area.add(setupPanel, BorderLayout.EAST);
		pack();
	}
	
	public void makeConfigPanel()
	{
		configPanel = new JPanel(new GridBagLayout());
		nameLabel = new JLabel("Spielername");
		hostLabel = new JLabel("Host-/IP-Adresse");
		portLabel = new JLabel("Port");
		nameField = new JTextField("Client03", 10);
		hostField = new JTextField("localhost", 10);
		portField = new JTextField("50003", 10);
		connectBtn = new JButton("Verbinden");
		connectBtn.addActionListener(this);
		newBtn = new JButton("Neues Spiel");
		newBtn.addActionListener(this);
		guessBtn = new JButton("Raten");
		guessBtn.addActionListener(this);
		quitBtn = new JButton("Spiel Beenden");
		quitBtn.addActionListener(this);
		autoBtn = new JButton("Automatikmodus");
		autoBtn.addActionListener(this);
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(10, 10, 10, 10);
		configPanel.add(nameLabel, c);
		c.gridx = 1;
		configPanel.add(nameField, c);
		c.gridx = 0;
		c.gridy = 1;
		configPanel.add(hostLabel, c);
		c.gridx = 1;
		configPanel.add(hostField, c);
		c.gridx = 0;
		c.gridy = 2;
		configPanel.add(portLabel, c);
		c.gridx = 1;
		configPanel.add(portField, c);
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 2;
		configPanel.add(connectBtn, c);
		c.gridy = 4;
		configPanel.add(newBtn, c);
		c.gridy = 5;
		configPanel.add(guessBtn, c);
		c.gridy = 6;
		configPanel.add(quitBtn, c);
		c.gridy = 7;
		configPanel.add(autoBtn, c);
		setToConnectMode();
	}
	
	public void makeInfoPanel()
	{
		infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
		textfield = new JTextArea(10, 20);
		textfield.setLineWrap(true);
		scrollpane = new JScrollPane(textfield, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		infoPanel.add(scrollpane);
	}
	
	public void makeSetupPanel()
	{
		setLabelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
		settingsLabel = new JLabel("Einstellungen");
		settingsLabel.setHorizontalAlignment(SwingConstants.CENTER);
		settingsLabel.setFont(CAPFONT);
		setLabelPanel.add(settingsLabel);
		setupPanel = new JPanel(new BorderLayout());
	}
	
	public void addMenu()
	{
		menuBar = new JMenuBar();
		menu = new JMenu("Optionen");
		itemQuit = new JMenuItem("Beenden");
		itemQuit.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				System.exit(0);
			}
		});
		menu.add(itemQuit);
		menuBar.add(menu);
		setJMenuBar(menuBar);
	}
	
	public void setMessage(String msg)
	{
		textfield.setText(msg);
	}
	
	public void appendMessage(String msg)
	{
		String text = textfield.getText();
		textfield.setText(msg + "\n" + text);
	}
	
	public void showErrorMessage(String title, String msg)
	{
		JOptionPane.showMessageDialog(this, msg, title, JOptionPane.ERROR_MESSAGE);
	}
	
	public void setToConnectMode()
	{
		connectBtn.setEnabled(true);
		newBtn.setEnabled(false);
		guessBtn.setEnabled(false);
		quitBtn.setEnabled(false);
		autoBtn.setEnabled(false);
	}
	
	public void setToSetupMode()
	{
		connectBtn.setEnabled(false);
		newBtn.setEnabled(true);
		guessBtn.setEnabled(false);
		quitBtn.setEnabled(true);
		autoBtn.setEnabled(true);
	}
	
	public void setGuessMode()
	{
		connectBtn.setEnabled(false);
		newBtn.setEnabled(false);
		guessBtn.setEnabled(true);
		quitBtn.setEnabled(true);
		autoBtn.setEnabled(false);
	}
	
	public void setColorPanel(String code)
	{
		this.colorcode = code;
		gameArea.makeSelectionPanel();
	}
	
	@Override
	public void actionPerformed(ActionEvent event)
	{
		if(event.getSource() == guessBtn)
		{
			client.makeGuess(gameArea.getStringCode());
			gameArea.archiveCode();
		}
		else if(event.getSource() == connectBtn)
		{
			try{
				client.connect(hostField.getText(), Integer.parseInt(portField.getText()));
			}catch(NumberFormatException ex){
				showErrorMessage("Falsche Porteingabe", ex.getMessage());
			}
		}
		else if(event.getSource() == newBtn){
			client.newGame(nameField.getText());
		}
		else if(event.getSource() == quitBtn){
			client.quitServer();
		}
		else if(event.getSource() == autoBtn){
			client.autoPlay();
		}
	}
	
	class GameArea implements ActionListener
	{
		private JPanel gamePanel;
		private CoursePanel coursePanel;
		private final int COURSEHEIGHT = 5*50+6*20;
		
		// Selection Area
		private JPanel selectionPanel;
		private JPanel labelPanel;
		private JLabel selectionLabel;
		private JPanel buttonPanel;
		private JButton[] buttons;
		
		public GameArea()
		{
			gamePanel = new JPanel(new BorderLayout());
			coursePanel = new CoursePanel(ClientGui.this, COURSEHEIGHT);
			makeSelectionPanel();
			gamePanel.add(coursePanel.getPanel(), BorderLayout.CENTER);
		}
		
		public void makeSelectionPanel()
		{
			if(selectionPanel != null)
				gamePanel.remove(selectionPanel);
			selectionPanel = new JPanel(new BorderLayout());
			labelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
			selectionLabel = new JLabel("Codeauswahl");
			selectionLabel.setFont(CAPFONT);
			labelPanel.add(selectionLabel);
			buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
			buttonPanel.setPreferredSize(new Dimension(client.getCodelength()*50+(client.getCodelength()+1)*20, 90));
			buttons = new JButton[client.getCodelength()];
			for(int i=0; i<buttons.length; i++)
			{
				JButton b = buttons[i] = new JButton();
				b.setBackground(Command.representColorchar(colorcode.charAt(0)));
				b.setPreferredSize(new Dimension(50, 50));
				b.addActionListener(this);
				buttonPanel.add(b);
			}
			selectionPanel.add(labelPanel, BorderLayout.NORTH);
			selectionPanel.add(buttonPanel, BorderLayout.CENTER);
			gamePanel.add(selectionPanel, BorderLayout.NORTH);
			pack();
		}
		
		public void colorDialog(JButton button)
		{
			JDialog dialog = new JDialog(ClientGui.this, "Wähle die Farbe", true);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			Container dc = dialog.getContentPane();
			dc.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 30));
			for(int i=0; i<colorcode.length(); i++)
			{
				Color color = Command.representColorchar(colorcode.charAt(i));
				JButton b = new JButton();
				b.setBackground(color);
				b.setPreferredSize(new Dimension(50, 50));
				b.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e){
						button.setBackground(b.getBackground());
						dialog.dispose();
					}
				});
				dc.add(b);
			}
			dialog.pack();
			dialog.setLocationRelativeTo(ClientGui.this);
			dialog.setVisible(true);
		}
		
		public void archiveCode()
		{
			Color[] colors = new Color[buttons.length];
			for(int i=0; i<colors.length; i++)
				colors[i] = buttons[i].getBackground();
			coursePanel.addButtons(colors);
		}
		
		public String getStringCode()
		{
			StringBuilder builder = new StringBuilder();
			Color[] colors = new Color[buttons.length];
			for(int i=0; i<colors.length; i++)
			{
				colors[i] = buttons[i].getBackground();
				builder.append(Command.getCodeChar(colors[i]));
			}
			return builder.toString();
		}
		
		public JPanel getPanel()
		{
			return gamePanel;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			colorDialog((JButton) e.getSource());
		}
	}
}
