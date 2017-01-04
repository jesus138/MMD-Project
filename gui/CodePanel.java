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

@SuppressWarnings("serial")
public class CodePanel extends JPanel
{
	private JLabel label;
	private JPanel capPanel, buttonPanel;
	private RoundButton[] buttons;
	private Window window;
	private boolean disabled;
	
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
	
	public void hideCode(boolean hidden){
		for(RoundButton b : buttons){
			b.hide(hidden);
			b.removeActionListener(b);
			if(!hidden && !disabled) b.addActionListener(b);
		}
	}
	
	public String getCode()
	{
		StringBuilder sb = new StringBuilder();
		for(RoundButton b : buttons)
			sb.append(Command.getCodeChar(b.getColor()));
		return sb.toString();
	}
	
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
	
	public void setDisabled(boolean disabled)
	{
		this.disabled = disabled;
		for(RoundButton b : buttons){
			b.removeActionListener(b);
			if(disabled) b.removeActionListener(b);
			else b.addActionListener(b);
		}
	}
	
	public void setCode(String code)
	{
		if(code != null && code.length() == buttons.length)
			for(int i=0; i<code.length(); i++)
				buttons[i].setColor(Command.representColorchar(code.charAt(i)));
	}
}
