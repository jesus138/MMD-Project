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

@SuppressWarnings("serial")
public class RoundButton extends JButton implements MouseListener, ActionListener
{
	private static final Color SILVER_BORDER = new Color(192, 192, 192, 255);
	private Color color, original;
	private Color[] palette;
	private Color drawColor, borderColor;
	private Window window;
	private boolean hidden;
	public int width, height;
	
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
	
	@Override
	public Border getBorder() {
		return null;
	}
	
	@Override
	public Dimension getPreferredSize() {
		return(new Dimension(width, height));
	}
	
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

	@Override
	public void mouseEntered(MouseEvent e) {
		drawColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()-155);
		borderColor = new Color(SILVER_BORDER.getRed(), SILVER_BORDER.getGreen(), SILVER_BORDER.getBlue(), SILVER_BORDER.getAlpha()-155);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		drawColor = color;
		borderColor = SILVER_BORDER;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		drawColor = new Color(color.getRed()+50 <= 255 ? color.getRed()+50 : 255,
				color.getGreen()+50 <= 255 ? color.getGreen()+50 : 255,
				color.getBlue()+50 <= 255 ? color.getBlue()+50 : 255, 255);
		borderColor = new Color(128, 128, 128, 255);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		drawColor = color;
		borderColor = SILVER_BORDER;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {}
	
	public Color getColor(){
		return original;
	}
	
	public void setColor(Color color){
		this.original = new Color(color.getRed(), color.getGreen(), color.getBlue(), 255);
		if(!hidden){
			this.color = new Color(original.getRed(), original.getGreen(), original.getBlue(), 255);
			drawColor = color;
			repaint();
		}
	}
	
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
