package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.SwingConstants;

import main.Command;

/**
 * Die Klasse HistoryPanel stellt den scrollbaren Verlaufsbereich der ClientGui
 * und ServerGui dar. Sie enthält eine Liste von JPanels für die einzelnen
 * Rateversuche mit Ergebniscode. Weiterhin besitzt die Klasse eine feste Höhe, aber
 * eine dynamische Breite in Abhängigkeit von der Codelänge. Oberhalb des eigentlichen
 * Verlaufsbereichs befindet sich noch ein JLabel welches die Überschrift darstellt.
 * @author Chris
 * @category Grafikkomponente
 */
@SuppressWarnings("serial")
public class HistoryPanel extends JPanel implements AdjustmentListener
{
	private static final int GAP = 20, BARWIDTH = 30;
	private Vector<JPanel> panels;
	private JScrollBar scrollbar;
	private JPanel mainPanel, labelPanel;
	private JLabel caption;
	private int maxheight, maxwidth;
	private Window window;
	
	/**
	 * Konstruktor zur Initialiserung der einzelnen Teilkomponenten und
	 * definitives Festlegen der Höhe des Verlaufsbereichs.
	 * @param maxheight feste Maximalhoehe
	 * @param window übergeordnetes Window bzw. JFrame
	 * @param length anfänglich Codelänge
	 */
	public HistoryPanel(int maxheight, Window window, int length){
		this.maxheight = maxheight;
		setMaxWidth(length);
		this.window = window;
		setOpaque(false);
		panels = new Vector<>();
		init();
	}
	
	private void init()
	{
		setLayout(new BorderLayout());
		mainPanel = new JPanel();
		mainPanel.setLayout(null);
		mainPanel.setPreferredSize(new Dimension(maxwidth, maxheight));
		mainPanel.setSize(new Dimension(maxwidth, maxheight));
		mainPanel.setOpaque(false);
		labelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, GAP, GAP));
		labelPanel.setOpaque(false);
		caption = new JLabel("Spielverlauf");
		caption.setFont(Command.CAPTION_FONT);
		caption.setHorizontalAlignment(SwingConstants.CENTER);
		labelPanel.add(caption);
		scrollbar = new JScrollBar(JScrollBar.VERTICAL, 0, scrollbarSize()/10, 0, scrollbarSize()+scrollbarSize()/10);
		scrollbar.addAdjustmentListener(this);
		scrollbar.setBounds(maxwidth-GAP-BARWIDTH, GAP, BARWIDTH, maxheight-2*GAP);
		mainPanel.add(scrollbar);
		add(labelPanel, BorderLayout.NORTH);
		add(mainPanel, BorderLayout.CENTER);
	}
	
	private int scrollbarSize()
	{
		int size = panels.size()*Command.BUTTON_SIZE+panels.size()*GAP;
		size = (size < 0) ? 0 : size;
		return size;
	}

	/**
	 * Implementierung des AdjustmentListeners zum anpassen der JPanel Positionen in
	 * Abhängigkeit von der Scrollposition der Scrollbar.
	 * @param e Ereignisobjekt
	 * @see java.awt.event.AdjustmentListener#adjustmentValueChanged(java.awt.event.AdjustmentEvent)
	 */
	@Override
	public void adjustmentValueChanged(AdjustmentEvent e){
		for(int i=panels.size()-1, y=0; i>=0; i--, y++){
			JPanel p = panels.get(i);
			p.setLocation(p.getX(), y*Command.BUTTON_SIZE+(y+1)*GAP-e.getValue());
		}
	}
	
	/**
	 * Fügt dem Verlaufsbereich ein weiteres JPanel mit den spezifierten Werten visuell ganz oben ein.
	 * Dabei werden alle Dimensionen neu berechnet und die eventuell neue maximale Breite des Verlaufsbereichs
	 * bestimmt. Auch die Positionen der einzelnen JPanels sowie deren RoundButtons und der Scrollbar müssen
	 * neu berechnet werden. Dazu müssen viele Layoutberechnungen auf dem UI-Thread vorgenommen werden. Deshalb
	 * wird das entfernen aller Komponenten und das darauffolgende Neueinfügen in die EventQueue des UI-Threads
	 * eingereiht. Dies vermeidet ansonsten anzutreffende RuntimeExceptions, die durch gleichzeitiges Zugreifen
	 * auf GUI-Elemente zurückzuführen sind.
	 * @param colors der geratene Farbcode
	 * @param rescode Resultat des Servers
	 */
	public void addButtons(Color[] colors, String rescode){
		int resnum = colors.length/2;
		if(colors.length%2==1) resnum++;
		int panelwidth = 4*GAP+resnum*Command.RES_BUTTON_SIZE+colors.length*Command.BUTTON_SIZE+(colors.length-1)*GAP+BARWIDTH;
		this.maxwidth = panelwidth > maxwidth ? panelwidth : maxwidth;
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setOpaque(false);
		panel.setPreferredSize(new Dimension(maxwidth, Command.BUTTON_SIZE));
		panel.setSize(new Dimension(maxwidth, Command.BUTTON_SIZE));
		int x = GAP;
		RoundButton[] bs = new RoundButton[resnum*2];
		for(int i=0; i<colors.length; i+=2){
			bs[i] = new RoundButton(Color.BLACK, Command.RES_BUTTON_SIZE, Command.RES_BUTTON_SIZE, null, window, false);
			bs[i].hide(true);
			bs[i].setLocation(x, 0);
			bs[i+1] = new RoundButton(Color.BLACK, Command.RES_BUTTON_SIZE, Command.RES_BUTTON_SIZE, null, window, false);
			bs[i+1].hide(true);
			bs[i+1].setLocation(x, Command.RES_BUTTON_SIZE);
			panel.add(bs[i]);
			panel.add(bs[i+1]);
			x += Command.RES_BUTTON_SIZE;
		}
		if(rescode != null && rescode.length() > 0 && rescode.charAt(0) != Command.RESULT_ALL_WRONG)
			for(int i=0; i<rescode.length(); i++){
				bs[i].setColor(Command.getResColor(rescode.charAt(i)));
				bs[i].hide(false);
			}
		x += GAP;
		for(int i=0; i<colors.length; i++){
			RoundButton b = new RoundButton(colors[i], Command.BUTTON_SIZE, Command.BUTTON_SIZE, null, window, false);
			b.setLocation(x, 0);
			panel.add(b);
			x+=GAP+Command.BUTTON_SIZE;
		}
		
		EventQueue.invokeLater(new Runnable(){
			@Override
			public void run(){
				panels.add(panel);
				mainPanel.removeAll();
				mainPanel.setPreferredSize(new Dimension(maxwidth, maxheight));
				mainPanel.setSize(new Dimension(maxwidth, maxheight));
				for(int i=panels.size()-1, y=GAP; i>=0; i--, y+=Command.BUTTON_SIZE+GAP){
					JPanel p = panels.get(i);
					p.setPreferredSize(new Dimension(maxwidth, Command.BUTTON_SIZE));
					p.setSize(new Dimension(maxwidth, Command.BUTTON_SIZE));
					p.setLocation(0, y);
					mainPanel.add(p);
				}
				scrollbar.setValues(0, scrollbarSize()/10, 0, scrollbarSize()+scrollbarSize()/10);
				scrollbar.setBounds(maxwidth-GAP-BARWIDTH, GAP, BARWIDTH, maxheight-2*GAP);
				mainPanel.add(scrollbar);
				repaint();
				window.pack();
			}
		});
	}
	
	private void setMaxWidth(int length){
		int resnum = length/2;
		if(length%2==1) resnum++;
		maxwidth = 4*GAP+resnum*Command.RES_BUTTON_SIZE+length*Command.BUTTON_SIZE+(length-1)*GAP+BARWIDTH;
	}
	
	/**
	 * Entfernt und löscht alle JPanels welche Rateversuche darstellen.
	 * Die neue Breite des Verlaufsbereichs sollte hierbei vorerst festgelegt werden.
	 * Die Länge bezieht sich auf die Länge des Farbcodes und nicht auf die Länge in Pixel,
	 * welche nämlich durch die private Funktion setMaxWidth() erst berechnet wird.
	 * Grundsätzlich sollte die aktuell eingestellte Farbcodelänge übergeben werden.
	 * @param length Breite des Verlaufsbereichs nach dem Leeren - Angabe in Anzahl RoundButtons
	 */
	public void clearHistory(int length){
		setMaxWidth(length);
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				mainPanel.removeAll();
				panels.clear();
				mainPanel.setPreferredSize(new Dimension(maxwidth, maxheight));
				mainPanel.setSize(new Dimension(maxwidth, maxheight));
				scrollbar.setValues(0, scrollbarSize()/10, 0, scrollbarSize()+scrollbarSize()/10);
				scrollbar.setBounds(maxwidth-2*GAP-BARWIDTH, GAP, BARWIDTH, maxheight-2*GAP);
				mainPanel.add(scrollbar);
				repaint();
				window.pack();
			}
		});
	}
}
