package main;

import java.awt.Color;
import java.awt.Font;

/**
 * Konfigurationsklasse in der Konstanten für folgende
 * Aspekte definiert sind:<br/>
 * <ul>
 * <li>Größenangaben zu Grafikfenstern, Verlaufsbereiche und Buttons</li>
 * <li>Standardeinstellungswerte wie im Netzwerkprotokoll definiert</li>
 * <li>Kommandostrings für Client-Server Kommunikation</li>
 * <li>Ergebnisstrings</li>
 * <li>Farbpalette als Zeichen und Farben</li>
 * <li>Standardschriftart</li>
 * </ul>
 * Außerdem sind noch einige Funktionen zum Umwandeln von Farben, Strings und Zeichen
 * in das gewünschte Zielformat vorhanden. Diese Funktionen werden in den Hauptklassen
 * verwendet um textuelle Farbcodes in visuelle umzuwandeln und umgekehrt.
 * @author Chris
 * @category Logikkomponente
 */
public class Command
{
	public static final int MWIDTH = 800, MHEIGHT = 600, COURSE_HEIGHT = 420, BUTTON_SIZE = 60, RES_BUTTON_SIZE = 30;
	public static final int DEFAULT_TRIES = 7, DEFAULT_COLOR_NUM = 6, DEFAULT_CODELENGTH = 4, DEFAULT_PORT = 50003;
	public static final String DEFAULT_NAME = "Client03", DEFAULT_HOST = "localhost";
	
	public static final String NEWGAME = "NEWGAME";
	public static final String SETUP = "SETUP";
	public static final String GUESS = "GUESS";
	public static final String CHECK = "CHECK";
	public static final String GAMEOVER = "GAMEOVER";
	public static final String RESULT = "RESULT";
	public static final String QUIT = "QUIT";
	
	public static final String GAMEOVER_WIN = "WIN";
	public static final String GAMEOVER_LOSE = "LOSE";
	public static final char RESULT_ALL_WRONG = '0';
	public static final char RESULT_WRONG_PLACE = 'W';
	public static final char RESULT_RIGHT_PLACE = 'B';
	
	/**
	 * Versteckte oder unbenutzte Elemente werden Pink dargestellt
	 */
	public static final Color HIDDEN_COLOR = new Color(244, 0, 138, 255);
	public static final Font CAPTION_FONT = new Font("SansSerif", Font.BOLD, 30);
	/**
	 * Farbpalette mit tatsächlichen Farben im RGB-Farbmodell
	 */
	public static final Color[] COLORS = {
			new Color(255,0,0),					// Red FF0000
			new Color(0,255,0),					// Green 00FF00
			new Color(0,0,255),					// Blue 0000FF
			new Color(240,248,255),				// Alice Blue F0F8FF
			new Color(25,25,25),				// almost Black 191919
			new Color(255,106,0),				// Orange FF6A00
			new Color(0,127,14),				// dark Green 007F0E
			new Color(0,148,255),				// bright Blue 0094FF
			new Color(127,0,55),				// dark Pink 7F0037
			new Color(201,219,41),				// stronger Yellow C9DB29
			new Color(48,48,48),				// dark Gray 303030
			new Color(255,178,127),				// Skin FFB27F
			new Color(68,255,178),				// weak Green 44FFAB
			new Color(137,67,41),				// Brown 894329
			new Color(33,0,127)					// ocean Blue 21007F
	};
	/**
	 * Standardfarbpalette 123456
	 */
	public static final Color[] DEFAULT_SET = {COLORS[0], COLORS[1], COLORS[2], COLORS[3], COLORS[4], COLORS[5]};
	/**
	 * Farbpalette als char-array: 123456789abcdef
	 */
	public static final char[] COLORSET = {
			'1',
			'2',
			'3',
			'4',
			'5',
			'6',
			'7',
			'8',
			'9',
			'a',
			'b',
			'c',
			'd',
			'e',
			'f'
	};
	
	/**
	 * Liefert zu einem beliebigen Zeichen der Zeichenfarbpalette eine
	 * dazugehörige Farbe im RGB-Farbmodell.
	 * @param c Zeichen aus COLORSET
	 * @return Farbe aus COLORS
	 */
	public static Color representColorchar(char c)
	{
		Color color = COLORS[0];
		switch(c)
		{
			case '2':
				color = COLORS[1];
				break;
			case '3':
				color = COLORS[2];
				break;
			case '4':
				color = COLORS[3];
				break;
			case '5':
				color = COLORS[4];
				break;
			case '6':
				color = COLORS[5];
				break;
			case '7':
				color = COLORS[6];
				break;
			case '8':
				color = COLORS[7];
				break;
			case '9':
				color = COLORS[8];
				break;
			case 'a':
				color = COLORS[9];
				break;
			case 'b':
				color = COLORS[10];
				break;
			case 'c':
				color = COLORS[11];
				break;
			case 'd':
				color = COLORS[12];
				break;
			case 'e':
				color = COLORS[13];
				break;
			case 'f':
				color = COLORS[14];
				break;
		}
		return color;
	}
	
	/**
	 * Liefert zu einer Farbe das dazu definierte Zeichen wie
	 * im Netzwerkprotokoll definiert und in COLORSET spezifiziert.
	 * @param color Farbe aus COLORS
	 * @return Zeichendarstellung der Farbe
	 */
	public static char getCodeChar(Color color)
	{
		int i = 0;
		char c = COLORSET[i++];
		while(i < COLORSET.length)
		{
			if(color.equals(COLORS[i])){
				c = COLORSET[i];
				break;
			}
			i++;
		}
		return c;
	}
	
	/**
	 * Liefert eine für jedes Zeichen eines Ergebnisses
	 * die dazu spezifizierte Farbe.
	 * @param c Ergebniszeichen vom Server
	 * @return Farbe für Ergebniszeichen
	 */
	public static Color getResColor(char c)
	{
		Color color = new Color(100, 255, 220);
		if(c == RESULT_WRONG_PLACE) color = new Color(255,255,255);
		else if(c == RESULT_RIGHT_PLACE) color = new Color(0,0,0);
		return color;
	}
	
	/**
	 * Wandelt einen Rateversuch in eine Folge von Farben
	 * um. Wird hauptsächlich von HistoryPanel verwendet.
	 * @param code Zeichenkette als Teilmenge von COLORSET
	 * @return Farben als Array
	 */
	public static Color[] getColors(String code){
		Color[] colors = new Color[code.length()];
		for(int i=0; i<colors.length; i++)
			colors[i] = representColorchar(code.charAt(i));
		return colors;
	}
	
	/**
	 * Liefert für eine Folge von Farben, die eine Teilmenge von
	 * COLORS sein muss, eine Zeichenkette, die Teilmenge von COLORSET
	 * ist. Wird im Zusammenhang mit dem CodePanel verwendet.
	 * @param colors Farbinformationen
	 * @return Repräsentation der Farbinformation als Zeichenkette
	 */
	public static String getStringCode(Color[] colors)
	{
		StringBuilder sb = new StringBuilder();
		for(Color c : colors)
			sb.append(getCodeChar(c));
		return sb.toString();
	}
}

