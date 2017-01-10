package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import main.Highscore;

@SuppressWarnings("serial")
public class HighscoreGui extends JFrame implements ActionListener
{
	private static final int FWIDTH = 400;
	private static final int FHEIGHT = 500;
	
	private static final String HTML_BEGIN = "<html>";
	private static final String HTML_END = "</html>";
	private static final String BODY_BEGIN = "<body>";
	private static final String BODY_END = "</body>";
	private static final String TABLE_BEGIN = "<table border=\"0\" cellpadding=\"15px\" style=\"font-family:sans-serif;border-collapse:collapse;\">";
	private static final String TABLE_END = "</table>";
	private static final String ROW_BEGIN_TOP = "<tr style=\"font-size:30px;\">";
	private static final String ROW_BEGIN_SUBTOP = "<tr style=\"font-size:20px;\">";
	private static final String ROW_BEGIN_MAIN = "<tr style=\"font-size:16px;\">";
	private static final String ROW_END = "</tr>";
	private static final String CELL_TOP = "<th colspan=\"3\"><u>Highscore</u></th>";
	private static final String CELL_SUBTOP_ID = "<th><u>ID</u></th>";
	private static final String CELL_SUBTOP_NAME = "<th><u>Name</u></th>";
	private static final String CELL_SUBTOP_SCORE = "<th><u>Score</u></th>";
	private static final String CELL_BEGIN_MAIN_ID = "<td align=\"center\">";
	private static final String CELL_BEGIN_MAIN_NAME = "<td align=\"center\">";
	private static final String CELL_BEGIN_MAIN_SCORE = "<td align=\"center\">";
	private static final String CELL_END_MAIN = "</td>";
	
	private HighContainer area;
	private JLabel content;
	private JScrollPane scroller;
	private JMenuBar menubar;
	private JMenu menu;
	private JMenuItem quitItem;
	private JMenuItem sortNormal;
	private JMenuItem sortAsc;
	private JMenuItem sortDes;
	private JMenuItem deleteItem;
	
	public HighscoreGui()
	{
		super("Highscore");
		area = new HighContainer();
		area.setLayout(new BorderLayout());	
		area.setOpaque(true);
		setContentPane(area);
		content = new JLabel();
		content.setHorizontalAlignment(SwingConstants.CENTER);
		content.setVerticalAlignment(SwingConstants.CENTER);
		scroller = new JScrollPane(content);
		area.add(scroller);
		area.setPreferredSize(new Dimension(FWIDTH, FHEIGHT));
		makeMenu();
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		showRows(Highscore.ORDER_NORMAL);
		setVisible(true);
		pack();
		setLocationRelativeTo(null);
	}
	
	public void makeMenu()
	{
		menubar = new JMenuBar();
		menu = new JMenu("Aktionen");
		quitItem = new JMenuItem("Schlie�en");
		sortNormal = new JMenuItem("Default");
		sortAsc = new JMenuItem("Sortieren ASC");
		sortDes = new JMenuItem("Sortieren DESC");
		deleteItem = new JMenuItem("Tabelle l�schen");
		sortNormal.addActionListener(this);
		quitItem.addActionListener(this);
		sortAsc.addActionListener(this);
		sortDes.addActionListener(this);
		deleteItem.addActionListener(this);
		menu.add(quitItem);
		menu.add(sortNormal);
		menu.add(sortAsc);
		menu.add(sortDes);
		menu.add(deleteItem);
		menubar.add(menu);
		setJMenuBar(menubar);
	}
	
	public void showRows(int order)
	{
		StringBuffer buffer = new StringBuffer(HTML_BEGIN + BODY_BEGIN + TABLE_BEGIN);
		buffer.append(ROW_BEGIN_TOP + CELL_TOP + ROW_END);
		buffer.append(ROW_BEGIN_SUBTOP + CELL_SUBTOP_ID + CELL_SUBTOP_NAME + CELL_SUBTOP_SCORE + ROW_END);
		Highscore.Row[] rows = Highscore.getAllEntries(order);
		for(Highscore.Row row : rows)
		{
			buffer.append(ROW_BEGIN_MAIN);
			buffer.append(CELL_BEGIN_MAIN_ID + row.id + CELL_END_MAIN);
			buffer.append(CELL_BEGIN_MAIN_NAME + row.name + CELL_END_MAIN);
			buffer.append(CELL_BEGIN_MAIN_SCORE + row.score + CELL_END_MAIN);
			buffer.append(ROW_END);
		}
		buffer.append(TABLE_END + BODY_END + HTML_END);
		content.setText(buffer.toString());
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == quitItem)
		{
			dispose();
		}
		else if(e.getSource() == sortNormal)
		{
			showRows(Highscore.ORDER_NORMAL);
		}
		else if(e.getSource() == sortAsc)
		{
			showRows(Highscore.ORDER_ASC);
		}
		else if(e.getSource() == sortDes)
		{
			showRows(Highscore.ORDER_DESC);
		}
		else if(e.getSource() == deleteItem)
		{
			int option = JOptionPane.showConfirmDialog(this, "Wollen Sie wirklich alle Eintr�ge\nin der Datenbank l�schen?",
					"Sind Sie sicher?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
			if(option == JOptionPane.YES_OPTION)
				Highscore.deleteEverything();
		}
	}
	
	private static class HighContainer extends JPanel
	{
		@Override
		protected void paintComponent(Graphics gr) {
			super.paintComponent(gr);
			double g = 210D;
			double gx = (g-45.0)/(double)getHeight();
			for(int y=getHeight()-1; y>=0; y--){
				gr.setColor(new Color(255, (int)g, 30));
				gr.fillRect(0, y, getWidth(), 1);
				g-=gx;
			}
		}
	}
}
