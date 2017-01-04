package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

@SuppressWarnings("serial")
public class Mastermind extends JFrame implements ActionListener
{
	private JLabel caption;
	private JButton serverBtn, clientBtn;
	private Container c;
	private JMenuBar menubar;
	private JMenu menu;
	private JMenuItem exitItem;
	
	public Mastermind()
	{
		super("Mastermind");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		c = getContentPane();
		c.setPreferredSize(new Dimension(Command.MWIDTH, Command.MHEIGHT));
		c.setSize(Command.MWIDTH, Command.MHEIGHT);
		init();
		pack();
		setLocationRelativeTo(null);
		setResizable(false);
		setVisible(true);
	}
	
	private void init()
	{
		c.setLayout(new BorderLayout());
		caption = new JLabel("Mastermind");
		caption.setFont(new Font("SansSerif", Font.BOLD, 50));
		caption.setHorizontalAlignment(SwingConstants.CENTER);
		serverBtn = new JButton("Server");
		serverBtn.addActionListener(this);
		serverBtn.setSize(200, 200);
		serverBtn.setPreferredSize(new Dimension(200, 200));
		serverBtn.setFont(Command.CAPTION_FONT);
		serverBtn.setBackground(Color.gray);
		clientBtn = new JButton("Client");
		clientBtn.addActionListener(this);
		clientBtn.setSize(200, 200);
		clientBtn.setPreferredSize(new Dimension(200, 200));
		clientBtn.setFont(Command.CAPTION_FONT);
		clientBtn.setBackground(Color.orange);
		JPanel mainPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 200, 80));
		mainPanel.add(serverBtn);
		mainPanel.add(clientBtn);
		JPanel capPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 50));
		capPanel.add(caption);
		c.add(capPanel, BorderLayout.NORTH);
		c.add(mainPanel, BorderLayout.CENTER);
		menubar = new JMenuBar();
		menu = new JMenu("Optionen");
		exitItem = new JMenuItem("Beenden");
		exitItem.addActionListener(this);
		menu.add(exitItem);
		menubar.add(menu);
		setJMenuBar(menubar);
	}
	
	public static void main(String[] args)
	{
		new Mastermind();
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == serverBtn){
			new Server();
			dispose();
		}else if(e.getSource() == clientBtn){
			new Client();
			dispose();
		}else if(e.getSource() == exitItem){
			System.exit(0);
		}
	}
}
