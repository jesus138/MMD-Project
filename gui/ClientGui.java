package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import main.Client;
import main.Command;

@SuppressWarnings("serial")
public class ClientGui extends JFrame implements ActionListener
{
	private ClientContainer c;
	
	private JPanel westPanel;
	private CodePanel guessPanel;
	private HistoryPanel historyPanel;
	
	private JPanel eastPanel;
	private JPanel messagePanel, labelPanel;
	private JLabel settingsLabel;
	private JTextArea messageBox;
	private JScrollPane scroller;
	
	private JPanel settingsPanel;
	private JLabel playerlabel, hostlabel, portlabel;
	private JTextField playerfield, hostfield, portfield;
	private JButton connectBtn, newBtn, guessBtn, quitBtn, autoBtn;
	
	private JMenuBar menubar;
	private JMenu menu;
	private JMenuItem quitItem, infoclearItem, historyClearItem;
	
	private Client client;
	
	public ClientGui(Client client)
	{
		super("Mastermind Client");
		this.client = client;
		c = new ClientContainer();
		setContentPane(c);
		c.setLayout(new BorderLayout());
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowCloser());
		initWest();
		initEast();
		initMenu();
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		setResizable(false);
	}
	
	private void initWest()
	{
		westPanel = new JPanel(new BorderLayout());
		westPanel.setOpaque(false);
		guessPanel = new CodePanel("Farbauswahl", this, Command.DEFAULT_SET, Command.DEFAULT_CODELENGTH);
		historyPanel = new HistoryPanel(Command.COURSE_HEIGHT, this, Command.DEFAULT_CODELENGTH);
		westPanel.add(guessPanel, BorderLayout.NORTH);
		westPanel.add(historyPanel, BorderLayout.CENTER);
		c.add(westPanel, BorderLayout.WEST);
	}
	
	private void initEast()
	{
		eastPanel = new JPanel(new BorderLayout());
		eastPanel.setOpaque(false);
		
		// Überschriften Panel
		labelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
		labelPanel.setOpaque(false);
		settingsLabel = new JLabel("Einstellungen");
		settingsLabel.setFont(Command.CAPTION_FONT);
		settingsLabel.setHorizontalAlignment(SwingConstants.CENTER);
		labelPanel.add(settingsLabel);
		
		// Einstellungs Panel
		settingsPanel = new JPanel(new GridBagLayout());
		settingsPanel.setOpaque(false);
		playerlabel = new JLabel("Spielername");
		hostlabel = new JLabel("Host-/IP-Adresse");
		portlabel = new JLabel("Netzwerkport");
		playerfield = new JTextField(Command.DEFAULT_NAME, 10);
		hostfield = new JTextField(Command.DEFAULT_HOST, 10);
		portfield = new JTextField(String.valueOf(Command.DEFAULT_PORT), 10);
		connectBtn = new JButton("Verbinden");
		connectBtn.addActionListener(this);
		connectBtn.setPreferredSize(new Dimension(150, 60));
		connectBtn.setBackground(Color.WHITE);
		newBtn = new JButton("Neues Spiel");
		newBtn.addActionListener(this);
		newBtn.setPreferredSize(new Dimension(150, 60));
		newBtn.setBackground(Color.WHITE);
		guessBtn = new JButton("Raten");
		guessBtn.addActionListener(this);
		guessBtn.setPreferredSize(new Dimension(150, 60));
		guessBtn.setBackground(Color.WHITE);
		quitBtn = new JButton("Beenden");
		quitBtn.addActionListener(this);
		quitBtn.setPreferredSize(new Dimension(150, 60));
		quitBtn.setBackground(Color.WHITE);
		autoBtn = new JButton("Automatikmodus");
		autoBtn.addActionListener(this);
		autoBtn.setPreferredSize(new Dimension(150, 60));
		autoBtn.setBackground(Color.WHITE);
		GridBagConstraints cons = new GridBagConstraints();
		cons.insets = new Insets(10, 0, 10, 20);
		cons.fill = GridBagConstraints.HORIZONTAL;
		cons.ipady = 10;
		settingsPanel.add(playerlabel, cons);
		cons.gridx = 1;
		settingsPanel.add(playerfield, cons);
		cons.gridx = 0;
		cons.gridy = 1;
		settingsPanel.add(hostlabel, cons);
		cons.gridx = 1;
		settingsPanel.add(hostfield, cons);
		cons.gridx = 0;
		cons.gridy = 2;
		settingsPanel.add(portlabel, cons);
		cons.gridx = 1;
		settingsPanel.add(portfield, cons);
		cons.gridx = 0;
		cons.gridy = 3;
		settingsPanel.add(connectBtn, cons);
		cons.gridx = 1;
		settingsPanel.add(quitBtn, cons);
		cons.gridy = 4;
		cons.gridx = 0;
		settingsPanel.add(newBtn, cons);
		cons.gridx = 1;
		settingsPanel.add(autoBtn, cons);
		cons.gridy = 5;
		cons.gridx = 0;
		settingsPanel.add(guessBtn, cons);
		
		// Info Panel
		messagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
		messagePanel.setOpaque(false);
		messageBox = new JTextArea(10, 30);
		messageBox.setLineWrap(true);
		messageBox.setWrapStyleWord(true);
		scroller = new JScrollPane(messageBox, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		messagePanel.add(scroller);
		
		// alle drei Panels hinzufügen
		eastPanel.add(labelPanel, BorderLayout.NORTH);
		eastPanel.add(settingsPanel, BorderLayout.CENTER);
		eastPanel.add(messagePanel, BorderLayout.SOUTH);
		c.add(eastPanel, BorderLayout.EAST);
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
	
	public void setToGuessMode()
	{
		connectBtn.setEnabled(false);
		newBtn.setEnabled(false);
		guessBtn.setEnabled(true);
		quitBtn.setEnabled(true);
		autoBtn.setEnabled(false);
	}
	
	public void setToAutoMode()
	{
		connectBtn.setEnabled(false);
		newBtn.setEnabled(false);
		guessBtn.setEnabled(false);
		quitBtn.setEnabled(true);
		autoBtn.setEnabled(false);
	}
	
	private void initMenu()
	{
		menubar = new JMenuBar();
		menu = new JMenu("Optionen");
		quitItem = new JMenuItem("Beenden");
		quitItem.addActionListener(this);
		infoclearItem = new JMenuItem("Nachrichten löschen");
		infoclearItem.addActionListener(this);
		historyClearItem = new JMenuItem("Verlauf löschen");
		historyClearItem.addActionListener(this);
		menu.add(quitItem);
		menu.add(infoclearItem);
		menu.add(historyClearItem);
		menubar.add(menu);
		setJMenuBar(menubar);
	}
	
	public void appendMessage(String msg){
		String pre = messageBox.getText();
		messageBox.setText(msg + "\n" + pre);
	}
	
	public void clearMessages(){
		messageBox.setText("");
	}
	
	public void showMessage(String title, String msg){
		JOptionPane.showMessageDialog(this, msg, title, JOptionPane.INFORMATION_MESSAGE);
	}
	
	public void setupGui(Color[] colors, int codelength){
		guessPanel.changePanel(colors, codelength);
		historyPanel.clearHistory(codelength);
	}
	
	public void addGuess(Color[] colors, String rescode){
		historyPanel.addButtons(colors, rescode);
	}
	
	public void clearHistory(){
		historyPanel.clearHistory(guessPanel.getCode().length());
	}
	
	public void again(){
		int resp = JOptionPane.showConfirmDialog(this, "Möchten Sie nochmal spielen?", "Spielende",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if(resp == JOptionPane.YES_OPTION)
			client.newGame(playerfield.getText());
		else
			client.disconnect();
	}
	
	private void autoplayDialog()
	{
		JDialog dialog = new JDialog(this);
		dialog.setTitle("Automatikmodus");
		dialog.setModal(true);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		Container dc = dialog.getContentPane();
		dc.setLayout(new BorderLayout());
		JPanel dialogCenter = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
		JPanel dialogSouth = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
		JLabel label = new JLabel("Anzahl Runden");
		SpinnerModel model = new SpinnerNumberModel(1, 0, Integer.MAX_VALUE, 1);
		JSpinner spinner = new JSpinner(model);
		dialogCenter.add(label);
		dialogCenter.add(spinner);
		JButton okBtn = new JButton("Ok");
		okBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				int rounds = (Integer)spinner.getValue();
				if(rounds > 0)
					client.autoPlay(rounds, playerfield.getText());
				dialog.dispose();
			}
		});
		JButton caBtn = new JButton("Abbrechen");
		caBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				dialog.dispose();
			}
		});
		dialogSouth.add(okBtn);
		dialogSouth.add(caBtn);
		dc.add(dialogCenter, BorderLayout.CENTER);
		dc.add(dialogSouth, BorderLayout.SOUTH);
		dialog.pack();
		dialog.setResizable(false);
		dialog.setLocationRelativeTo(this);
		dialog.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == connectBtn){
			try{
				String ipaddress = hostfield.getText();
				int port = Integer.parseInt(portfield.getText());
				client.connect(ipaddress, port);
			}catch(Exception ex){
				showMessage("Falsche Eingabe", "Fehler: " + ex.getMessage());
			}
		}
		else if(e.getSource() == newBtn){
			String playername = playerfield.getText();
			client.newGame(playername);
		}
		else if(e.getSource() == guessBtn){
			client.makeGuess(guessPanel.getCode());
		}
		else if(e.getSource() == quitBtn){
			client.endGame(null);
		}
		else if(e.getSource() == autoBtn){
			autoplayDialog();
		}
		else if(e.getSource() == quitItem){
			client.endGame(null);
			System.exit(0);
		}
		else if(e.getSource() == infoclearItem){
			clearMessages();
		}
		else if(e.getSource() == historyClearItem){
			clearHistory();
		}
	}
	
	private static class ClientContainer extends JPanel
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
	
	private class WindowCloser extends WindowAdapter
	{
		@Override
		public void windowClosing(WindowEvent e) {
			client.endGame(null);
			System.exit(0);
		}
	}
}
