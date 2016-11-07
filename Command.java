package mastermind;

import java.awt.Color;

public class Command
{
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
	
	public static final Color[] COLORS = {
			new Color(255,0,0),					// Red FF0000
			new Color(0,255,0),					// Green 00FF00
			new Color(0,0,255),					// Blue 0000FF
			new Color(220,220,220),				// darker White DCDCDC
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
	
	public static char getCodeChar(Color color)
	{
		int i = 0;
		char c = COLORSET[i++];
		while(i < COLORSET.length)
		{
			if(color.equals(COLORS[i]))
				c = COLORSET[i];
			i++;
		}
		return c;
	}
}
