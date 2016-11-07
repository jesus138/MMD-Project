package mastermind;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.Vector;

public class Server
{
	public static final int SERVERPORT = 50003;
	private int port = SERVERPORT;
	
	private String playername;
	private boolean automatic;
	private long score;
	private int availableTries;
	private Vector<String> gameHistory;
	
	private String colorcode;
	private char[] availableColors;
	private int codelength;
	private int tries;
	
	private volatile boolean running;
	private ServerGui gui;
	private ServerSocket serverSocket;
	private Socket clientSocket;
	private PrintWriter writer;
	private BufferedReader reader;
	
	public Server()
	{
		try {
			serverSocket = new ServerSocket(SERVERPORT);
		} catch (IOException e) {}
		codelength = 4;
		tries = 4;
		score = 10000 * tries;
		availableColors = new char[6];
		for(int i=0; i<availableColors.length; i++) availableColors[i] = Command.COLORSET[i];
		automatic = true;
		running = false;
		gui = new ServerGui(this);
	}
	
	public static void main(String[] args)
	{
		Server server = new Server();
		while(true)
		{
			try{
				server.startConnection();
				while(server.running)
				{
					server.gui.appendText("Server erwartet Kommando.");
					server.query(server.readCmd());
				}
			}catch(Exception e){
				server.gui.showErrorMessage("Exception", e.getMessage());
			}
		}
	}
	
	public synchronized void startConnection() throws IOException
	{
		clientSocket = serverSocket.accept();
		gui.setText("Connection to a client established: " + clientSocket.getInetAddress().getHostName());
		reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		writer = new PrintWriter(clientSocket.getOutputStream(), true);
	}
	
	public String readCmd() throws IOException
	{
		String msg = reader.readLine();
		gui.appendText("Client sent: " + msg);
		return msg;
	}
	
	public void query(String cmd) throws IOException, InterruptedException
	{
		gui.appendText("Client Kommando: " + cmd);
		StringBuilder builder = new StringBuilder(cmd);
		int sep = builder.indexOf(" ");
		String cmdF = builder.substring(0, sep);
		if(!running && cmdF.equalsIgnoreCase(Command.NEWGAME))
		{
			String playerarg = builder.substring(sep);
			newGame(playerarg);
			setup();
		}
		else if(running && cmdF.equalsIgnoreCase(Command.CHECK))
		{
			String code = builder.substring(sep);
			checkCode(code);
		}
		else if(cmdF.equalsIgnoreCase(Command.QUIT))
		{
			quitGame();
		}
		else
		{
			gui.showErrorMessage("Unknown Command", "Server received command: " + cmd);
		}
	}
	
	public synchronized void endConnection() throws IOException
	{
		reader.close();
		writer.close();
		clientSocket.close();
		running = false;
		gui.appendText("Verbindung zum Client geschlossen.");
	}
	
	public void newGame(String playername)
	{
		this.playername = playername;
		availableTries = tries;
		gameHistory = new Vector<>();
		running = true;
	}
	
	public void checkCode(String colorcode)
	{
		gameHistory.add(colorcode);
		gui.addNewColorCode(colorcode);
		if(colorcode.equalsIgnoreCase(this.colorcode))
		{
			writer.write(String.format("%s %s\n", Command.RESULT, "BBBB"));
			writer.write(String.format("%s %s\n", Command.GAMEOVER, Command.GAMEOVER_WIN));
		}
		else
		{
			StringBuilder builder = new StringBuilder();
			char[] realCode = this.colorcode.toCharArray();
			char[] givenCode = colorcode.toCharArray();
			boolean zeroFits = true;
			for(int i=0; i<givenCode.length; i++)
			{
				for(int j=0; j<realCode.length; j++)
				{
					if(givenCode[i] == realCode[i])
					{
						zeroFits = false;
						if(i == j) builder.append(Command.RESULT_RIGHT_PLACE);
						else builder.append(Command.RESULT_WRONG_PLACE);
						break;
					}
				}
			}
			if(zeroFits) builder.append(Command.RESULT_ALL_WRONG);
			String feedback = builder.toString();
			writer.write(String.format("%s %s\n", Command.RESULT, feedback));
			
			availableTries--;
			score -= 1000;
			if(availableTries <= 0)
			{
				writer.write(String.format("%s %s\n", Command.GAMEOVER, Command.GAMEOVER_LOSE));
				running = false;
			}
			else
			{
				writer.write(String.format("%s\n", Command.GUESS));
			}
		}
	}
	
	public void setup()
	{
		if(automatic)
		{
			char[] codeSet = new char[codelength];
			Random rand = new Random(System.currentTimeMillis());
			for(int i=0; i<codeSet.length; i++)
				codeSet[i] = Command.COLORSET[rand.nextInt(availableColors.length)];
			colorcode = new String(codeSet);
			writer.write(String.format("%s %d %s\n", Command.SETUP, codelength, new String(availableColors)));
		}
		else
		{
			writer.write(String.format("%s %d %s\n", Command.SETUP, codelength, new String(availableColors)));
		}
		writer.write(String.format("%s\n", Command.GUESS));
	}
	
	public synchronized void quitGame()
	{
		running = false;
		gui.setText("Server wurde beendet.");
	}
	
	public void serverQuit()
	{
		writer.write(String.format("%s\n", Command.QUIT));
		quitGame();
	}
	
	public void setColorCode(String code)
	{
		this.colorcode = code;
		codelength = code.length();
	}
	
	public void setMode(boolean automatic)
	{
		this.automatic = automatic;
	}
	
	public void setTries(int tries)
	{
		this.tries = tries;
		score = this.tries * 10000;
	}
	
	public synchronized void setPort(int port)
	{
		if(port != SERVERPORT)
		{
			this.port = port;
			try {
				serverSocket = new ServerSocket(this.port);
				endConnection();
			} catch (IOException e) {
				gui.showErrorMessage("Netzwerkfehler", "Fehler beim Einstellen des Serverports.");
			}
		}
	}
	
	public void setAvailableColors(String colors)
	{
		availableColors = new char[colors.length()];
		for(int i=0; i<availableColors.length; i++)
			availableColors[i] = colors.charAt(i);
	}
	
	public Vector<String> getHistory()
	{
		return gameHistory;
	}
	
	public void writeToHighscore()
	{
		Highscore.insertIntoHighscore(playername, score);
	}
}
