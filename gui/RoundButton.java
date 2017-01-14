package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.border.Border;

import main.Command;

/**
 * RoundButton erweitert das Swing Standard Widget JButton. Dazu
 * muss ebenfalls das Interface MouseListener implementiert werden,
 * um in Abhängigkeit von der Mausinteraktion mit der Komponente
 * visuelles Feedback zu geben. Außerdem beinhaltet RoundButton
 * bereits einen Farbauswahldialog, welcher über das ActionListener
 * Interface angezeigt werden kann. Dies kann jedoch ausgestellt werden.
 * @author Chris
 * @category Grafikkomponente
 */
@SuppressWarnings("serial")
public class RoundButton extends JButton implements MouseListener, ActionListener
{
	private static final Color SILVER_BORDER = new Color(192, 192, 192, 255);
	private Color color, original;
	private Color[] palette;
	private Color drawColor, borderColor;
	private Window window;
	private boolean hidden;
	/**
	 * Schnellzugriff auf die Breite des Buttons
	 */
	final public int width;
	/**
	 * Schnellzugriff auf die Höhe des Buttons
	 */
	final public int height;
	
	/**
	 * Konstruktor zur Initialisierung der JButton Unterklasse RoundButton.
	 * Setzt die Hauptfarbe auf die spezifierte Farbe und die Grenzfarbe auf Silber.
	 * Die Höhe und Breite werden einmalig festgelegt und für den eventuellen
	 * Auswahldialog wird bereits eine Farbpalette angelegt.
	 * @param color Hauptfarbe des RoundButtons
	 * @param w feste Breite
	 * @param h feste Höhe
	 * @param palette Farbpalette für Farbauswahldialog
	 * @param window Window bzw. JFrame in dem sich der Button befindet
	 * @param dialog true -> Auswahldialog bei Klick / false -> kein Dialog
	 */
	public RoundButton(Color color, int w, int h, Color[] palette, Window window, boolean dialog)
	{
		this.original = new Color(color.getRed(), color.getGreen(), color.getBlue(), 255);
		this.color = new Color(original.getRed(), original.getGreen(), original.getBlue(), 255);
		this.palette = palette;
		this.window = window;
		hidden = false;
		drawColor = color;
		borderColor = SILVER_BORDER;
		width = w;
		height = h;
		setPreferredSize(new Dimension(w, h));
		setSize(w, h);
		setContentAreaFilled(false);
		setOpaque(false);
		setFocusPainted(false);
		addMouseListener(this);
		if(dialog) addActionListener(this);
	}
	
	/**
	 * Muss überschrieben werden, um keinen rechteckigen Rahmen um die Komponente
	 * gezeichnet zu bekommen.
	 * @return null
	 * @see javax.swing.JComponent#getBorder()
	 */
	@Override
	public Border getBorder() {
		return null;
	}
	
	/**
	 * Liefert die zuvor festgelegt Höhe und Breite in
	 * dem Object Dimension zusammengefasst.
	 * @return Höhe und Breite
	 * @see javax.swing.JComponent#getPreferredSize()
	 */
	@Override
	public Dimension getPreferredSize() {
		return(new Dimension(width, height));
	}
	
	/**
	 * Zeichnet eine innere ausgefüllte Ellipse, welche den Hauptbereich
	 * des RoundButtons darstellt und eine äußere nicht ausgefüllte
	 * Ellipse, welche den runden silbergrauen Grenzbereich darstellt.
	 * Zum Zeichnen wird eine Instanz der Klasse Graphics2D verwendet und
	 * Antialising eingestellt. Die Farben ergeben sich dynamisch aus der
	 * Originalfarbe, gerade eingestellten Farbe und der Zeichenfarbe.
	 * @param g Grafikobjekt
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(new Color(0, 0, 0, 0));
		g2.fillRect(0, 0, getWidth(), getHeight());
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(drawColor);
		g2.fillOval(1, 1, getPreferredSize().width-2, getPreferredSize().height-2);
		g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 2f));
		g2.setColor(borderColor);
		g2.drawOval(1, 1, getPreferredSize().width-2, getPreferredSize().height-2);
	}

	/**
	 * Falls der ActionListener eingestellt wurde zeigt der JButton auf Klick einen
	 * Farbauswahldialog an, in dem sich die Farben der Farbpalette einstellen lassen.
	 * Somit kann die Farbe des RoundButtons direkt per Klick verändert werden.
	 * @param e Ereignisobjekt
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		JDialog dialog = new JDialog(window);
		dialog.setTitle("Farbauswahl");
		Container c = dialog.getContentPane();
		c.setLayout(new GridLayout(3, 5));
		for(Color color : Command.COLORS)
		{
			JButton button = new JButton();
			button.setPreferredSize(new Dimension(80, 80));
			button.setBackground(color);
			boolean inPalette = false;
			for(Color col : palette){
				if(col.equals(color)){
					inPalette = true;
					break;
				}
			}
			if(inPalette)
				button.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent event){
						RoundButton.this.setColor(button.getBackground());
						dialog.dispose();
					}
				});
			else{
				button.setEnabled(false);
				Color bg = button.getBackground();
				button.setBackground(new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), 0));
			}
			c.add(button);
		}
		dialog.pack();
		dialog.setLocationRelativeTo(window);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setResizable(false);
		dialog.setVisible(true);
	}

	/**
	 * Beim drüberfahren mit dem Mauszeiger muss der Alphawert der Komponente verringert werden.
	 * Der Konstruktor ignoriert standardmäßig den mitgelieferten Alphawert, sodass dieser
	 * immer den Wert 255 besitzt.
	 * @param e Ereignisobjekt
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
		drawColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()-155);
		borderColor = new Color(SILVER_BORDER.getRed(), SILVER_BORDER.getGreen(), SILVER_BORDER.getBlue(), SILVER_BORDER.getAlpha()-155);
	}

	/**
	 * Beim verlassen des Mauszeigers muss die eingestellte Farbe wieder gezeichnet werden.
	 * @param e Ereignisobjekt
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent e) {
		drawColor = color;
		borderColor = SILVER_BORDER;
	}

	/**
	 * Verdunkelt die Farbe wenn möglich beim gedrückthalten des Mauszeigers.
	 * Sollte Verdunkeln nicht funktionieren wird aufgehellt.<br/>
	 * Zusammen mit mouseReleased() wird somit bei Mausklick eine Farbanimation erzeugt.
	 * @param e Ereignisobjekt
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		drawColor = new Color(color.getRed()+50 <= 255 ? color.getRed()+50 : 255,
				color.getGreen()+50 <= 255 ? color.getGreen()+50 : 255,
				color.getBlue()+50 <= 255 ? color.getBlue()+50 : 255, 255);
		borderColor = new Color(128, 128, 128, 255);
	}

	/**
	 * Beim loslassen des Mauszeigers muss die eingestellte Farbe wieder gezeichnet werden.
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		drawColor = color;
		borderColor = SILVER_BORDER;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {}
	
	/**
	 * Wird zum bestimmen des Farbcodes verwendet.
	 * @return original eingestellte Farbe
	 */
	public Color getColor(){
		return original;
	}
	
	/**
	 * Stellt die Farbe des RoundButtons auf einen neuen Wert. Falls
	 * der RoundButton nicht versteckt ist wird auch die Zeichenfarbe
	 * auf diese neue Farbe gesetzt und die Komponente neugezeichnet.
	 * @param color neu eingestellte Farbe
	 */
	public void setColor(Color color){
		this.original = new Color(color.getRed(), color.getGreen(), color.getBlue(), 255);
		if(!hidden){
			this.color = new Color(original.getRed(), original.getGreen(), original.getBlue(), 255);
			drawColor = color;
			repaint();
		}
	}
	
	/**
	 * Verschleiert die tatsächlich eingestellte Farbe durch die "Verstecktfarbe", welche
	 * in der Basisklasse Command definiert ist. Wird für die ServerGui benötigt.
	 * @param hidden true -> Zeichenfarbe auf "Verstecktfarbe" / false -> Zeichenfarbe auf eingestellte Farbe
	 */
	public void hide(boolean hidden){
		this.hidden = hidden;
		if(hidden)
			color = Command.HIDDEN_COLOR;
			
		else
			color = original;
		drawColor = color;
		repaint();
	}
}
