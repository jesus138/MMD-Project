package mastermind;

import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
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
public class ServerGui extends JFrame implements ActionListener
{
	private Container area;
	private Server server;
	
	// Bereich für den Spielverlauf
	private CoursePanel gamearea;
	
	// Bereich für die Nachrichten
	private JPanel messagePanel;
	private JTextArea textfield;
	private JScrollPane scrollpane;
	
	// Bereich für die Einstellungen
	private JPanel setupPanel;
	private JLabel setupLabel;
	private JComboBox<String> modebox;
	private final static String[] MODES = {"Manuell", "Automatisch"};
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
	
	public ServerGui(Server server)
	{
		super("Mastermind Server");
		this.server = server;
		
		initComponents();

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		setResizable(false);
		setLocationRelativeTo(null);
	}
	
	public void initComponents()
	{
		area = getContentPane();
		area.setLayout(new GridBagLayout());
		makeMenu();
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.BOTH;
		c.gridheight = 3;
		gamearea = new CoursePanel(this);
		area.add(gamearea.getPanel(), c);
		c.gridx = 1;
		c.gridheight = 2;
		makeSetupPanel();
		area.add(setupPanel, c);
		c.gridy = 2;
		c.gridheight = 1;
		makeInfoPanel();
		area.add(messagePanel, c);	
		pack();
	}
	
	public void makeSetupPanel()
	{
		setupPanel = new JPanel(new GridBagLayout());
		setupLabel = new JLabel("Einstellungen");
		setupLabel.setHorizontalAlignment(SwingConstants.CENTER);
		setupLabel.setFont(new Font("SansSerif", Font.BOLD, 30));
		modebox = new JComboBox<>(MODES);
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
		menu = new JMenu("Menü");
		exitItem = new JMenuItem("Beenden");
		exitItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				System.exit(0);
			}
		});
		menu.add(exitItem);
		menubar.add(menu);
		setJMenuBar(menubar);
	}
	
	public void addNewColorCode(String code)
	{
		Color[] colors = new Color[code.length()];
		for(int i=0; i<colors.length; i++)
			colors[i] = Command.representColorchar(code.charAt(i));
		gamearea.addButtons(colors);
	}
	
	public void appendText(String addition)
	{
		String text = textfield.getText();
		textfield.setText(addition + "\n" + text);
	}
	
	public void setText(String str)
	{
		textfield.setText(str);
	}
	
	public void showErrorMessage(String title, String content)
	{
		JOptionPane.showMessageDialog(this, content, title, JOptionPane.ERROR_MESSAGE);
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == setupButton)
		{
			int index = modebox.getSelectedIndex();
			if(index == 0){
				server.setMode(false);
				String code = JOptionPane.showInputDialog(this, "Farbcode eingeben:");
				int codelength = code.length();
				appendText("Ihr eingegebener Farbcode: " + code);
				boolean error = false;
				if(code.length() != codelength)
					error = true;
				for(int i=0; i<code.length(); i++)
				{
					int j;
					for(j=0; j<Command.COLORSET.length; j++)
						if(code.charAt(i) == Command.COLORSET[j])
							break;
					if(j == Command.COLORSET.length)
						error = true;
				}
				if(!error)
					server.setColorCode(code);
				else
					showErrorMessage("Farbfehler", "Ihr eingegebener Farbcode ist falsch.");
			}
			else
				server.setMode(true);
			
			boolean error = false;
			String code = colorField.getText();
			int tries = 7;
			int port = 50003;
			try{
				tries = Integer.parseInt(guessField.getText());
				port = Integer.parseInt(portField.getText());
			}catch(NumberFormatException ex){
				error = true;
			}
			if(tries <= 0)
				guessField.setText(String.valueOf(0));
			if(!error)
			{
				server.setAvailableColors(code);
				server.setTries(tries);
				server.setPort(port);
			} else JOptionPane.showMessageDialog(this, "Überprüfen Sie Ihre Einstellungen.", "Fehler", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	// Test
	public static void main(String[] args)
	{
		ServerGui gui = new ServerGui(null);
		gui.addNewColorCode("1234");
		gui.addNewColorCode("a82f");
		gui.addNewColorCode("95b3");
		gui.addNewColorCode("12345678");
		gui.addNewColorCode("123");
		gui.addNewColorCode("12");
		gui.addNewColorCode("12345");
		gui.addNewColorCode("12345");
		gui.addNewColorCode("12345");
		gui.addNewColorCode("12345");
		gui.addNewColorCode("12345");
		gui.addNewColorCode("12345");
		gui.addNewColorCode("12345");
		gui.addNewColorCode("12345");
		gui.addNewColorCode("12345");
		gui.addNewColorCode("12345");
	}
}
