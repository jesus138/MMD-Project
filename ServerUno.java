package networkingtrials;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
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
public class ServerUno extends JFrame implements ActionListener
{
	private Container c;
	private JPanel setupPanel;
	private JPanel mainPanel;
	private JTextArea logcat;
	private JScrollPane scroller;
	private JLabel portlabel;
	private JTextField portfield;
	private JButton button;
	
	private ServerSocket server;
	private Socket client;
	private PrintWriter writer;
	private BufferedReader reader;
	
	public ServerUno()
	{
		super("Server Uno");
		c = getContentPane();
		c.setLayout(new BorderLayout());
		setupPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
		mainPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
		logcat = new JTextArea(10, 20);
		logcat.setLineWrap(true);
		scroller = new JScrollPane(logcat, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		button = new JButton("Submit Changes");
		button.addActionListener(this);
		portlabel = new JLabel("Port:");
		portfield = new JTextField("50003", 10);
		setupPanel.add(portlabel);
		setupPanel.add(portfield);
		mainPanel.add(scroller);
		mainPanel.add(button);
		c.add(setupPanel, BorderLayout.NORTH);
		c.add(mainPanel, BorderLayout.CENTER);
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
		
		initServer(50003);
	}
	
	public static void main(String[] args)
	{
		new ServerUno();
	}
	
	public void showErrorMessage(String msg, String title)
	{
		JOptionPane.showMessageDialog(this, msg, title, JOptionPane.ERROR_MESSAGE);
	}
	
	public void appendText(String text)
	{
		String current = logcat.getText();
		logcat.setText(text + "\n" + current);
	}
	
	public void initServer(int port)
	{
		try {
			server = new ServerSocket(port);
			appendText(server.getInetAddress().getHostAddress() + " - Port: " + server.getLocalPort());
			startConnection();
		} catch (IOException e) {
			showErrorMessage(e.getMessage(), "IOException");
		}
	}
	
	public void startConnection()
	{
		new Thread(new Runnable(){
			public void run(){
			try {
				client = server.accept();
				writer = new PrintWriter(client.getOutputStream(), true);
				reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
				appendText("Client Port: " + client.getPort());
				processCommands();
			} catch (IOException e) {
				showErrorMessage(e.getMessage(), "IOException");
			}
		}}).start();;
	}
	
	public void processCommands()
	{
		while(server != null && !server.isClosed())
		{
			System.out.println("Waiting to read ...");
			try {
				String cmd = reader.readLine();
				appendText("Client sent: " + cmd);
				try{
					Thread.sleep((int)(Math.random()*4500+500));
					writer.println("Server responds.");
				}catch(InterruptedException e){
					showErrorMessage(e.getMessage(), "Thread Fehler");
				}
			} catch (IOException e) {
				showErrorMessage(e.getMessage(), "during readLine()");
				endConnection();
				break;
			}
		}
	}
	
	public void endConnection()
	{
		try {
			reader.close();
			writer.close();
			client.close();
			startConnection();
		} catch (IOException e) {
			showErrorMessage(e.getMessage(), "close()");
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		// JOptionPane.showMessageDialog(this, "I am available!", "Message", JOptionPane.INFORMATION_MESSAGE);
		try{
			server.close();
			if(client != null)
				client.close();
			initServer(Integer.parseInt(portfield.getText()));
		}catch(Exception ex){
			showErrorMessage(ex.getMessage(), "Umkonfiguration");
		}
	}
}
