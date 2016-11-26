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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.swing.JButton;
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

@SuppressWarnings("serial")
public class ServerGui extends JFrame implements ActionListener, ItemListener
{
	private Container area;
	private Server server;
	
	// Bereich für den Spielverlauf
	private CoursePanel gamearea;
	private SelectionPanel selarea;
	
	// Bereich für die Nachrichten
	private JPanel messagePanel;
	private JTextArea textfield;
	private JScrollPane scrollpane;
	
	// Bereich für die Einstellungen
	private JPanel setupPanel;
	private JLabel setupLabel;
	private JComboBox<String> modebox;
	private final static String[] MODES = {"Automatisch", "Manuell"};
	private JLabel codeLabel;
	private JComboBox<Integer> codebox;
	private JLabel colorLabel;
	private JTextField colorField;
	private JLabel guessLabel;
	private JTextField guessField;
	private JLabel portLabel;
	private JTextField portField;
	private JButton setupButton;
	private JLabel hostLabel;
	
	// Menü
	private JMenuBar menubar;
	private JMenu menu;
	private JMenuItem exitItem;
	private JMenuItem clearItem;
	private JMenuItem gamequitItem;
	private JMenuItem highscoreItem;
	
	public ServerGui(Server server)
	{
		super("Mastermind Server");
		this.server = server;
		
		initComponents();

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		setResizable(false);
		setLocationRelativeTo(null);
		addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e) {
				finish();
			}
		});
	}
	
	public void initComponents()
	{
		area = getContentPane();
		area.setLayout(new GridBagLayout());
		makeSetupPanel();
		makeInfoPanel();
		gamearea = new CoursePanel(this, 5*50+6*20);
		selarea = new SelectionPanel();
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.BOTH;
		area.add(selarea.getSelectionPanel(), c);
		c.gridx = 1;
		c.gridheight = 2;
		area.add(setupPanel, c);
		c.gridx = 0;
		c.gridy = 1;
		area.add(gamearea.getPanel(), c);
		c.gridx = 1;
		c.gridy = 2;
		c.gridheight = 1;
		area.add(messagePanel, c);
		makeMenu();
		pack();
	}
	
	public void makeSetupPanel()
	{
		setupPanel = new JPanel(new GridBagLayout());
		setupLabel = new JLabel("Einstellungen");
		setupLabel.setHorizontalAlignment(SwingConstants.CENTER);
		setupLabel.setFont(new Font("SansSerif", Font.BOLD, 30));
		modebox = new JComboBox<>(MODES);
		modebox.addItemListener(this);
		codeLabel = new JLabel("Codelänge:");
		Integer[] values = new Integer[14];
		int start = 2;
		for(int i=0; i<values.length; i++) values[i] = start++;
		codebox = new JComboBox<>(values);
		codebox.setSelectedIndex(2);
		colorLabel = new JLabel("Farben:");
		colorField = new JTextField("123456", 10);
		guessLabel = new JLabel("Rateversuche:");
		guessField = new JTextField("7", 10);
		portLabel = new JLabel("Netzwerkport:");
		portField = new JTextField(String.valueOf(Server.SERVERPORT), 10);
		setupButton = new JButton("Aktualisieren");
		setupButton.addActionListener(this);
		hostLabel = new JLabel("Hostadresse: localhost");
		hostLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(10,20,10,20);
		c.gridwidth = 2;
		setupPanel.add(setupLabel, c);
		c.gridy = 1;
		setupPanel.add(modebox, c);
		c.gridy = 2;
		c.gridwidth = 1;
		setupPanel.add(codeLabel, c);
		c.gridx = 1;
		setupPanel.add(codebox, c);
		c.gridy = 3;
		c.gridx = 0;
		setupPanel.add(colorLabel, c);
		c.gridx = 1;
		setupPanel.add(colorField, c);
		c.gridy = 4;
		c.gridx = 0;
		setupPanel.add(guessLabel, c);
		c.gridx = 1;
		setupPanel.add(guessField, c);
		c.gridy = 5;
		c.gridx = 0;
		setupPanel.add(portLabel, c);
		c.gridx = 1;
		setupPanel.add(portField, c);
		c.gridwidth = 2;
		c.gridy = 6;
		c.gridx = 0;
		setupPanel.add(setupButton, c);
		c.gridy = 7;
		setupPanel.add(hostLabel, c);
	}
	
	public void makeInfoPanel()
	{
		messagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 10));
		textfield = new JTextArea(10, 20);
		textfield.setLineWrap(true);
		scrollpane = new JScrollPane(textfield, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		messagePanel.add(scrollpane);
	}
	
	public void makeMenu()
	{
		menubar = new JMenuBar();
		menu = new JMenu("Optionen");
		exitItem = new JMenuItem("Beenden");
		clearItem = new JMenuItem("Konsole leeren");
		exitItem.addActionListener(this);
		clearItem.addActionListener(this);
		gamequitItem = new JMenuItem("Spiel abbrechen");
		gamequitItem.addActionListener(this);
		gamequitItem.setEnabled(false);
		highscoreItem = new JMenuItem("Highscore");
		highscoreItem.addActionListener(this);
		menu.add(exitItem);
		menu.add(clearItem);
		menu.add(gamequitItem);
		menu.add(highscoreItem);
		menubar.add(menu);
		setJMenuBar(menubar);
	}
	
	public void addNewColorCode(String code, String rescode)
	{
		Color[] colors = new Color[code.length()];
		for(int i=0; i<colors.length; i++)
			colors[i] = Command.representColorchar(code.charAt(i));
		gamearea.addButtons(colors, rescode);
	}
	
	public void appendText(String addition)
	{
		String text = textfield.getText();
		textfield.setText(addition + "\n" + text);
	}
	
	public void clearConsole()
	{
		textfield.setText("");
	}
	
	public void indicateHostAddress(String address)
	{
		hostLabel.setText("Hostadresse: " + address);
	}
	
	public void showErrorMessage(String title, String content)
	{
		JOptionPane.showMessageDialog(this, content, title, JOptionPane.ERROR_MESSAGE);
	}
	
	public void setSelectionColorCode(String colorcode)
	{
		selarea.setColorpanel(colorcode);
	}
	
	public String getSelectedCode()
	{
		return selarea.getColorCode();
	}
	
	public void setPlayMode()
	{
		modebox.setEnabled(false);
		codebox.setEnabled(false);
		colorField.setEnabled(false);
		guessField.setEnabled(false);
		portField.setEnabled(false);
		setupButton.setEnabled(false);
		gamequitItem.setEnabled(true);
	}
	
	public void setSetupMode()
	{
		modebox.setEnabled(true);
		codebox.setEnabled(true);
		colorField.setEnabled(true);
		guessField.setEnabled(true);
		portField.setEnabled(true);
		setupButton.setEnabled(true);
		gamequitItem.setEnabled(false);
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == setupButton)
		{
			boolean automatic = modebox.getSelectedIndex() == 0 ? true : false;
			String code = colorField.getText();
			int tryconfig = 7;
			try{
				tryconfig = Integer.parseInt(guessField.getText());
			}catch(NumberFormatException ex){}
			int tries = tryconfig >= 2 && tryconfig <= 15 ? tryconfig : 7;
			int port = 50003;
			try{
				port = Integer.parseInt(portField.getText());
			}catch(NumberFormatException ex){}
			try{
				tries = Integer.parseInt(guessField.getText());
				port = Integer.parseInt(portField.getText());
			}catch(NumberFormatException ex){}
			server.setConfiguration(automatic, (Integer)codebox.getSelectedItem(), code, tries, port);
			selarea.updateButtonNumber();
			selarea.toggleMode(automatic);
		}
		else if(e.getSource() == exitItem) finish();
		else if(e.getSource() == clearItem) clearConsole();
		else if(e.getSource() == gamequitItem) server.instantQuit();
		else if(e.getSource() == highscoreItem) new Scoreframe();
	}
	

	@Override
	public void itemStateChanged(ItemEvent e)
	{
		String item = (String) modebox.getSelectedItem();
		if(item.equalsIgnoreCase(MODES[0]))
			selarea.toggleMode(true);
		else if(item.equalsIgnoreCase(MODES[1]))
			selarea.toggleMode(false);
	}
	
	public void finish()
	{
		if(server != null)
			server.endConnection();
		System.exit(0);
	}
	
	private class SelectionPanel implements ActionListener
	{
		private JPanel selPanel;
		private JPanel headPanel;
		private JPanel buttonPanel;
		private Vector<JButton> buttons;
		
		public SelectionPanel()
		{
			selPanel = new JPanel(new BorderLayout(0, 0));
			headPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
			JLabel heading = new JLabel("Codewort");
			heading.setFont(new Font("SansSerif", Font.BOLD, 30));
			heading.setHorizontalAlignment(SwingConstants.CENTER);
			headPanel.add(heading);
			buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
			selPanel.add(headPanel, BorderLayout.NORTH);
			selPanel.add(buttonPanel, BorderLayout.SOUTH);
			updateButtonNumber();
		}
		
		public void updateButtonNumber()
		{
			int number = (Integer) codebox.getSelectedItem();
			if(buttons == null)
				buttons = new Vector<>();
			else
			{
				buttonPanel.removeAll();
				buttons.removeAllElements();
			}
			
			for(int i=0; i<number; i++)
			{
				JButton button = new JButton();
				button.setPreferredSize(new Dimension(50, 50));
				if(colorField.getText().length() >= 1)
					button.setBackground(Command.representColorchar(colorField.getText().charAt(0)));
				else
					button.setBackground(Command.COLORS[0]);
				buttonPanel.add(button);
				buttons.add(button);
			}
			buttonPanel.repaint();
			ServerGui.this.pack();
		}
		
		public JPanel getSelectionPanel()
		{
			return selPanel;
		}
		
		public void toggleMode(boolean automatic)
		{
			if(automatic)
			{
				for(JButton b : buttons)
					if(b.getActionListeners().length >= 1)
						b.removeActionListener(this);
			}
			else
			{
				for(JButton b : buttons)
					if(b.getActionListeners().length == 0)
						b.addActionListener(this);
			}
		}
		
		public void setColorpanel(String code)
		{
			for(int i=0; i<buttons.size()&&i<code.length(); i++)
				buttons.get(i).setBackground(Command.representColorchar(code.charAt(i)));
		}
		
		public String getColorCode()
		{
			StringBuilder builder = new StringBuilder();
			for(JButton b : buttons)
				builder.append(Command.getCodeChar(b.getBackground()));
			return builder.toString();
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if(!server.isRunning())
				for(JButton b : buttons)
				{
					if(e.getSource() == b)
					{
						JDialog dialog = new JDialog(ServerGui.this);
						dialog.setModal(true);
						dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
						dialog.setTitle("Farbauswahl");
						JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
						for(int i=0; i<colorField.getText().length(); i++)
						{
							JButton button = new JButton();
							button.setPreferredSize(new Dimension(50, 50));
							button.setBackground(Command.representColorchar(colorField.getText().charAt(i)));
							button.addActionListener(new ActionListener(){
								@Override
								public void actionPerformed(ActionEvent e){
									b.setBackground(button.getBackground());
									dialog.dispose();
								}
							});
							buttonPanel.add(button);
						}
						dialog.add(buttonPanel);
						dialog.pack();
						dialog.setLocationRelativeTo(ServerGui.this);
						dialog.setVisible(true);
						break;
					}
				}
		}
	}
}
