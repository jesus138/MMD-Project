package mastermind;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class ClientGui extends JFrame implements ActionListener
{
	private Client client;
	private JTextArea textfield;
	private JButton connect;
	private JButton send;
	private JPanel area;
	private JTextField shell;
	
	public ClientGui(Client client)
	{
		super("Mastermind Client");
		this.client = client;
		area = new JPanel(new FlowLayout());
		add(area, BorderLayout.CENTER);
		
		textfield = new JTextArea(10, 20);
		textfield.setWrapStyleWord(true);
		JScrollPane scrollbar = new JScrollPane(textfield, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		area.add(scrollbar);
		shell = new JTextField(15);
		area.add(shell);
		
		send = new JButton("Send");
		send.addActionListener(this);
		area.add(send);
		connect = new JButton("Connect");
		connect.addActionListener(this);
		area.add(connect);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setVisible(true);
		setSize(240, 300);
		setLocationRelativeTo(null);
	}
	
	public void setMessage(String msg)
	{
		textfield.setText(msg);
	}
	
	public void appendMessage(String msg)
	{
		textfield.append("\n" + msg);
	}
	
	public void showErrorMessage(String title, String msg)
	{
		JOptionPane.showMessageDialog(this, msg, title, JOptionPane.ERROR_MESSAGE);
	}
	
	@Override
	public void actionPerformed(ActionEvent event)
	{
		if(event.getSource() == send)
		{
			try {
				client.sendCommand(shell.getText());
			} catch (IOException e) {
				showErrorMessage("IOException", e.getMessage());
			}
		}
		else if(event.getSource() == connect)
		{
			try {
				client.connect("localhost", Server.SERVERPORT);
			} catch (UnknownHostException e) {
				showErrorMessage("UnknownHostException", e.getMessage());
			} catch (IOException e) {
				showErrorMessage("IOException", e.getMessage());
			}
		}
	}
}
