package mastermind;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;

public class Client
{
	public static final String CLIENTNAME = "Client03";
	
	private String playername;
	private Vector<String> gameHistory;
	private boolean automatic;
	private char[] availableColors;
	private int codelength;
	private volatile boolean running;
	
	private Socket connection;
	private ClientGui gui;
	private PrintWriter writer;
	private BufferedReader reader;
	
	public Client()
	{
		gui = new ClientGui(this);
		playername = CLIENTNAME;
		automatic = false;
		running = false;
	}
	
	public void query(String cmd) throws NumberFormatException, IOException
	{
		StringBuilder builder = new StringBuilder(cmd);
		int sep = builder.indexOf(" ");
		String cmdF = builder.substring(0, sep);
		if(!running && cmdF.equalsIgnoreCase(Command.SETUP))
		{
			String args = builder.substring(sep);
			builder = new StringBuilder(args);
			sep = builder.indexOf(" ");
			int codelength = Integer.parseInt(builder.substring(0, sep));
			String colors = builder.substring(sep);
			startGame(codelength, colors);
		}
		else if(running && cmdF.equalsIgnoreCase(Command.GUESS))
		{
			changeToGuessMode();
		}
		else if(running && cmdF.equalsIgnoreCase(Command.RESULT))
		{
			String result = builder.substring(sep);
			presentResult(result);
		}
		else if(running && cmdF.equalsIgnoreCase(Command.GAMEOVER))
		{
			String result = builder.substring(sep);
			endGame(result);
		}
		else if(cmdF.equalsIgnoreCase(Command.QUIT))
		{
			endGame(null);
		}
		else
		{
			gui.showErrorMessage("Unknown Command", "Command received: " + cmd);
		}
	}
	
	public void connect(String ipaddress, int port) throws UnknownHostException, IOException
	{
		connection = new Socket(ipaddress, port);
		writer = new PrintWriter(connection.getOutputStream(), true);
		reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	}
	
	public void sendCommand(String cmd) throws IOException
	{
		writer.println(cmd);
		String serverCmd = reader.readLine();
		query(serverCmd);
		StringBuilder b = new StringBuilder(serverCmd);
		String code = b.substring(0, b.indexOf(" "));
		if(code.equalsIgnoreCase(Command.SETUP) || code.equalsIgnoreCase(Command.RESULT))
			query(reader.readLine());
	}
	
	public void newGame(String playername)
	{
		this.playername = playername;
		writer.write(String.format("%s %s\n", Command.NEWGAME, this.playername));
	}
	
	public void startGame(int codelength, String colors)
	{
		this.codelength = codelength;
		gameHistory = new Vector<>();
		availableColors = new char[colors.length()];
		for(int i=0; i<availableColors.length; i++) availableColors[i] = colors.charAt(i);
		running = true;
	}
	
	public void changeToGuessMode()
	{
		if(automatic)
		{
			// make calculated routine
		}
		else
		{
			// toggle gui elements to guess mode
		}
	}
	
	public void presentResult(String result)
	{
		gui.appendMessage("Ergebnis: " + result);
	}
	
	public void endGame(String result) throws IOException
	{
		if(result != null)
		{
			gui.setMessage("Glückwunsch! Sie haben gewonnen.");
		}
		else
		{
			gui.appendMessage("Verbindung zum Server wurde getrennt.");
			writer.close();
			reader.close();
			connection.close();
		}
	}
	
	public void makeGuess(String colorcode)
	{
		gameHistory.add(colorcode);
		writer.write(String.format("%s %s\n", Command.CHECK, colorcode));
	}
	
	public void quitServer() throws IOException
	{
		writer.write(String.format("%s\n", Command.QUIT));
		endGame(null);
	}
	
	public String getName()
	{
		return playername;
	}
	
	public Vector<String> getHistory()
	{
		return gameHistory;
	}
	
	public void setMode(boolean automatic)
	{
		this.automatic = automatic;
	}
	
	public int getCodelength()
	{
		return codelength;
	}
}
