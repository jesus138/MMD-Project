package mastermind;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class ServerGui extends JFrame
{
	private Container area;
	private JTextArea textfield;
	private Server server;
	
	public ServerGui(Server server)
	{
		super("Mastermind Server");
		this.server = server;
		area = getContentPane();
		area.setLayout(new FlowLayout());
		
		textfield = new JTextArea(10, 30);
		textfield.setLineWrap(true);
		JScrollPane scrollbar = new JScrollPane(textfield, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		area.add(scrollbar);

		setSize(400, 300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		setResizable(false);
		setLocationRelativeTo(null);
	}
	
	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension(400, 300);
	}
	
	public void appendText(String addition)
	{
		textfield.append("\n" + addition);
	}
	
	public void setText(String str)
	{
		textfield.setText(str);
	}
	
	public void showErrorMessage(String title, String content)
	{
		JOptionPane.showMessageDialog(this, content, title, JOptionPane.ERROR_MESSAGE);
	}
}
