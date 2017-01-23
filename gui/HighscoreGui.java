package gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import main.Highscore;

/**
 * Dient dem anzeigen der Highscore ausgehend vom Server-Programm.
 * Dieses Fenster gliedert sich in den UI-Thread der ServerGui ein und
 * laesst diesen bei Beendigung unveraendert. Die Highscore wird als HTML-Tabelle
 * dargestellt und laesst sich nach Wunsch sortieren.
 * @author Chris
 * @category Grafikkomponente
 */
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
	
	private Container area;
	private JLabel content;
	private JScrollPane scroller;
	private JMenuBar menubar;
	private JMenu menu;
	private JMenuItem quitItem;
	private JMenuItem sortNormal;
	private JMenuItem sortAsc;
	private JMenuItem sortDes;
	private JMenuItem deleteItem;
	
	/**
	 * Konstruktor uebernimmt das erzeugen der Oberflaeche, indem er vor allem
	 * die Funktionen makeMenu() und showRows() aufruft. Ausserdem wird eine
	 * feste Groesse festgelegt sowie ein entsprechender Listener registriert,
	 * um ueber das Menue die Highscoretabelle sortieren zu koennen.
	 */
	public HighscoreGui()
	{
		super("Highscore");
		area = getContentPane();
		area.setLayout(new BorderLayout());	
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
	
	private void makeMenu()
	{
		menubar = new JMenuBar();
		menu = new JMenu("Aktionen");
		quitItem = new JMenuItem("Schliessen");
		sortNormal = new JMenuItem("Default");
		sortAsc = new JMenuItem("Sortieren ASC");
		sortDes = new JMenuItem("Sortieren DESC");
		deleteItem = new JMenuItem("Tabelle loeschen");
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
	
	/**
	 * Erneuert das Layout nach einem festgelegten Algorithmus. Die zu
	 * verwendenden Sortieralgorithmen sind in der Highscore Klasse definiert.<br/>
	 * Prinzipiell interagiert diese Funktion mit der Apache Derby Datenbank und schreibt
	 * die Abfragenergebnisse in die HTML-Tabelle.
	 * @param order Sortieralgorithmus
	 */
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
			int option = JOptionPane.showConfirmDialog(this, "Wollen Sie wirklich alle Eintraege\nin der Datenbank loeschen?",
					"Sind Sie sicher?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
			if(option == JOptionPane.YES_OPTION)
				Highscore.deleteEverything();
		}
	}
}
