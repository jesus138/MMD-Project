package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Window;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import main.Command;

/**
 * Repr�sentiert den Farbauswahlbereich des Clients und den Bereich des Servers,
 * welcher den zu ratenden Code anzeigt. Beinhaltet eine Ueberschrift, sowie eine
 * Reihe von RoundButtons. Obwohl diese Komponente von sowohl ClientGui und ServerGui
 * verwendet wird, bietet sie f�r beide unterschiedliche Funktionalit�ten an.
 * @author Chris
 * @category Grafikkomponente
 */
@SuppressWarnings("serial")
public class CodePanel extends JPanel
{
	private JLabel label;
	private JPanel capPanel, buttonPanel;
	private RoundButton[] buttons;
	private Window window;
	private boolean disabled;
	
	/**
	 * Konstruktor des CodePanels, der alle ben�tigten Teilkomponenten initialisiert
	 * und dem Bereich eine anf�ngliche Dimension in Abh�ngigkeit zur Codel�nge gibt.
	 * @param title Ueberschrift des CodePanels
	 * @param window Containerbereich der Komponente; eigentlich immer ein JFrame
	 * @param palette erlaubte Farben f�r Auswahldialog
	 * @param codelength Codel�nge bestimmt die Anzahl der dargestellten RoundButtons
	 */
	public CodePanel(String title, Window window, Color[] palette, int codelength)
	{
		this.window = window;
		setOpaque(false);
		setLayout(new BorderLayout());
		label = new JLabel(title);
		label.setFont(Command.CAPTION_FONT);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		capPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
		capPanel.setOpaque(false);
		capPanel.add(label);
		buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
		buttonPanel.setOpaque(false);
		add(capPanel, BorderLayout.NORTH);
		add(buttonPanel, BorderLayout.CENTER);
		changePanel(palette, codelength);
	}
	
	/**
	 * Erlaubt dem Server, das zu erratende Codewort visuell zu verschleiern.
	 * N�tzlich ist dies wenn das Client- und Server-Programm am selben Bildschirm
	 * angezeigt werden.
	 * @param hidden true -> Farbcode wird versteckt / false -> Farbcode wird wieder angezeigt
	 */
	public void hideCode(boolean hidden){
		for(RoundButton b : buttons){
			b.hide(hidden);
			b.removeActionListener(b);
			if(!hidden && !disabled) b.addActionListener(b);
		}
	}
	
	/**
	 * Z�hlt alle RoundButtons durch und wandelt dessen Farbe in den dazugeh�rigen
	 * Character um. Baut diese zu einem String zusammen.<br/>
	 * Verwendung findet diese Funktion vor allem vom Client-Programm, aber auch
	 * vom Server bei manueller Farbauswahl.
	 * @return Zeichenkettenrepr�sentation des gew�hlten Farbcodes
	 */
	public String getCode()
	{
		StringBuilder sb = new StringBuilder();
		for(RoundButton b : buttons)
			sb.append(Command.getCodeChar(b.getColor()));
		return sb.toString();
	}
	
	/**
	 * Aktualisiert die Anzahl und Auswahldialoge der RoundButtons und sorgt
	 * f�r eine Neuberechnung des Gesamtlayouts.<br/>
	 * Die �nderung des Layouts wird in die EventQueue des UI-Threads eingereiht, sodass
	 * sichergestellt ist, dass keine gleichzeitigen Zugriffe auf Oberfl�chenelemente erfolgen.
	 * @param palette neue Farbpalette f�r Auswahldialoge
	 * @param codelength neue Anzahl der RoundButtons
	 */
	public void changePanel(Color[] palette, int codelength)
	{
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				buttonPanel.removeAll();
				buttons = new RoundButton[codelength];
				for(int i=0; i<buttons.length; i++)
				{
					buttons[i] = new RoundButton(palette[0], Command.BUTTON_SIZE, Command.BUTTON_SIZE, palette, window, !disabled);
					buttonPanel.add(buttons[i]);
				}
				repaint();
				window.pack();
			}
		});
	}
	
	/**
	 * Erlaubt das Ein- und Ausschalten der Farbauswahldialoge. Der Client wird
	 * diese Funktion jedoch nicht verwenden, da grunds�tzlich die Farben ausgew�hlt
	 * werden k�nnen bzw. im Automatikmodus ignoriert werden k�nnen. Hingegen
	 * verwendet der Server diese Funktion im manuellen Modus, sodass sich gezielt
	 * ein zu ratendes Codewort einstellen l�sst bzw. diese Funktion wieder ausstellen
	 * l�sst.
	 * @param disabled true -> kein Auswahldialog / false -> mit Auswahldialog
	 */
	public void setDisabled(boolean disabled)
	{
		this.disabled = disabled;
		for(RoundButton b : buttons){
			b.removeActionListener(b);
			if(disabled) b.removeActionListener(b);
			else b.addActionListener(b);
		}
	}
	
	/**
	 * F�rbt die RoundButtons bez�glich des aktuellen Codeworts ein.
	 * Wird nur vom Server bei automatischer Codegeneration verwendet.
	 * @param code anzuzeigendes Codewort
	 */
	public void setCode(String code)
	{
		if(code != null && code.length() == buttons.length)
			for(int i=0; i<code.length(); i++)
				buttons[i].setColor(Command.representColorchar(code.charAt(i)));
	}
}
