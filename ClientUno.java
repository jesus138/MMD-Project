package networkingtrials;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class ClientUno extends JFrame implements ActionListener
{
	private Container c;
	private JPanel north;
	private JPanel main;
	private JTextArea shell;
	private JScrollPane scroller;
	private JLabel addressLabel;
	private JLabel portLabel;
	private JTextField address;
	private JTextField portfield;
	private JButton connect;
	private JButton send;
	
	private Socket server;
	private BufferedReader reader;
	private PrintWriter writer;
	
	public ClientUno()
	{
		super("Client Uno");
		makeLayout();
	}
	
	public void makeLayout()
	{
		c = getContentPane();
		c.setLayout(new BorderLayout());;
		north = new JPanel(new GridBagLayout());
		main = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
		shell = new JTextArea(10, 20);
		shell.setLineWrap(true);
		scroller = new JScrollPane(shell, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		addressLabel = new JLabel("Hostadresse:");
		portLabel = new JLabel("Port:");
		address = new JTextField("0.0.0.0", 10);
		portfield = new JTextField("50003", 10);
		connect = new JButton("Verbinden");
		connect.addActionListener(this);
		send = new JButton("Senden");
		send.addActionListener(this);
		main.add(scroller);
		main.add(send);
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(10, 20, 10, 20);
		north.add(addressLabel, constraints);
		constraints.gridx = 1;
		north.add(address, constraints);
		constraints.gridx = 0;
		constraints.gridy = 1;
		north.add(portLabel, constraints);
		constraints.gridx = 1;
		north.add(portfield, constraints);
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = 2;
		north.add(connect, constraints);
		
		c.add(north, BorderLayout.NORTH);
		c.add(main, BorderLayout.CENTER);
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	public static void main(String[] args)
	{
		new ClientUno();
	}
	
	public void showErrorMessage(String msg, String title)
	{
		JOptionPane.showMessageDialog(this, msg, title, JOptionPane.ERROR_MESSAGE);
	}
	
	public void appendText(String text)
	{
		String current = shell.getText();
		shell.setText(text + "\n" + current);
	}
	
	public void connect()
	{
		try {
			server = new Socket(address.getText(), Integer.parseInt(portfield.getText()));
			reader = new BufferedReader(new InputStreamReader(server.getInputStream()));
			writer = new PrintWriter(server.getOutputStream(), true);
			appendText("\nServer: " + server.getInetAddress().getHostAddress() + " on Port: " + server.getPort());
			shell.setCaretPosition(0);
			connect.setEnabled(false);
		} catch (NumberFormatException | IOException e) {
			showErrorMessage(e.getMessage(), "Verbindungsaufbau fehlgeschlagen");
		}
	}
	
	public void endConnection()
	{
		try {
			server.close();
			reader.close();
			writer.close();
			server = null;
		} catch (IOException e) {
			showErrorMessage(e.getMessage(), "close()");
		}
		connect.setEnabled(true);
	}
	
	public void sendCommand()
	{
		StringBuilder builder = new StringBuilder(shell.getText());
		int index = builder.indexOf("\n");
		writer.println(builder.subSequence(0, index));
		new Thread(new Runnable(){
			public void run(){
				try {
					appendText(reader.readLine());
					String text = shell.getText();
					shell.setText("\n" + text);
					shell.setCaretPosition(0);
					shell.requestFocus();
				} catch (IOException e) {
					showErrorMessage(e.getMessage(), "Server lost");
					endConnection();
				}
			}
		}).start();
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(connect == e.getSource())
		{
			connect();
		}
		else if(send == e.getSource())
		{
			if(server != null)
				sendCommand();
			else
				showErrorMessage("Erst Verbindung aufbauen!", "Fehler");
		}
	}
	
}
