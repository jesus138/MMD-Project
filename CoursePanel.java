package mastermind;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.SwingConstants;

public class CoursePanel implements AdjustmentListener
{
	private int COURSEHEIGHT;
	
	private int maxButtons = 4;
	private int maxWidth = 0;
	private JPanel mainPanel;
	private JPanel labelPanel;
	private JPanel scrollPanel;
	private JPanel coursePanel;
	private Vector<JPanel> panels;
	private JLabel label;
	private JScrollBar scrollbar;
	private JFrame frame;
	
	// used by client because of different height
	public CoursePanel(JFrame frame, int height)
	{
		COURSEHEIGHT = height;
		this.frame = frame;
		mainPanel = new JPanel(new BorderLayout());
		labelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
		scrollPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 20));
		coursePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
		panels = new Vector<>();
		label = new JLabel("Spielverlauf");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setFont(new Font("SansSerif", Font.BOLD, 30));
		scrollbar = new JScrollBar(JScrollBar.VERTICAL, 0, 20, 0, scrollbarSize());
		scrollbar.setPreferredSize(new Dimension(30, COURSEHEIGHT-40));
		scrollbar.addAdjustmentListener(this);
		labelPanel.add(label);
		scrollPanel.add(scrollbar);
		coursePanel.setPreferredSize(new Dimension(maxButtons*50+(maxButtons+1)*20, COURSEHEIGHT));
		mainPanel.add(labelPanel, BorderLayout.NORTH);
		mainPanel.add(scrollPanel, BorderLayout.EAST);
		mainPanel.add(coursePanel, BorderLayout.CENTER);
	}
	
	public CoursePanel(JFrame frame)
	{
		this(frame, 6*50+7*20); // height for Server
	}
	
	public void addButtons(Color[] colors, String rescode)
	{
		JPanel complete = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
		
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
		JButton[] buttons = new JButton[colors.length];
		for(int i=0; i<buttons.length; i++)
		{
			JButton b = buttons[i] = new JButton();
			b.setBackground(colors[i]);
			b.setPreferredSize(new Dimension(50, 50));
			panel.add(b);
		}
		
		JPanel resPanel = new JPanel();
		resPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		if(rescode.charAt(0) == Command.RESULT_ALL_WRONG){
			JButton b = new JButton();
			b.setPreferredSize(new Dimension(25, 50));
			b.setBackground(Command.getResColor(Command.RESULT_ALL_WRONG));
			c.insets = new Insets(0, 10, 0, 0);
			resPanel.add(b, c);
		}else{
			boolean up = true;
			for(int i=0; i<rescode.length(); i++){
				JButton b = new JButton();
				b.setPreferredSize(new Dimension(20, 20));
				b.setBackground(Command.getResColor(rescode.charAt(i)));
				c.gridx = i/2;
				c.gridy = (up) ? 0 : 1;
				c.insets = new Insets(0, 10, (up) ? 10 : 0, 0);
				resPanel.add(b, c);
				up = !up;
			}
		}
		
		complete.add(resPanel);
		complete.add(panel);
		maxWidth = (int) ((maxWidth < complete.getPreferredSize().getWidth()) ? complete.getPreferredSize().getWidth() : maxWidth);
		panels.add(complete);
		coursePanel.setPreferredSize(new Dimension(maxWidth, COURSEHEIGHT));
		coursePanel.removeAll();
		scrollbar.setMaximum(scrollbarSize());
		for(int i=panels.size()-1 ; i>=0; i--){
			JPanel p = panels.get(i);
			p.setPreferredSize(new Dimension(maxWidth, 50));
			coursePanel.add(p);
		}
		frame.pack();
		// frame.setLocationRelativeTo(null);
	}
	
	private int scrollbarSize()
	{
		int size = panels.size()*50+(panels.size()+1)*20-COURSEHEIGHT;
		size = (size < 0) ? 0 : size;
		return size+20;
	}

	@Override
	public void adjustmentValueChanged(AdjustmentEvent e)
	{
		int count = 0;
		for(int i=panels.size()-1; i>=0; i--)
		{
			JPanel p = panels.get(i);
			p.setLocation(p.getX(), count*50+(count++ +1)*20-e.getValue());
		}
	}
	
	public JPanel getPanel()
	{
		return mainPanel;
	}
}
