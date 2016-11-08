package mastermind;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class Server
{
	public static final int SERVERPORT = 50003;
	private int port = SERVERPORT;
	private boolean waiting = false;
	
	private String playername;
	private boolean automatic;
	private long score;
	private int availableTries;
	
	private String colorcode;
	private char[] availableColors;
	private int codelength;
	private int tries;
	
	private boolean running;
	private ServerGui gui;
	private ServerSocket serverSocket;
	private Socket clientSocket;
	private PrintWriter writer;
	private BufferedReader reader;
	
	public Server()
	{
		codelength = 4;
		tries = 4;
		score = 10000 * tries;
		availableColors = new char[6];
		for(int i=0; i<availableColors.length; i++) availableColors[i] = Command.COLORSET[i];
		automatic = true;
		running = false;
		gui = new ServerGui(this);
		gui.setText("Mit aktualisieren starten");
	}
	
	public static void main(String[] args)
	{
		new Server();
	}
	
	public void startConnection(int port)
	{
		new Thread(new Runnable(){
			public void run(){
				try{
					serverSocket = new ServerSocket(port);
					gui.appendText("Warten auf Client auf Port " + serverSocket.getLocalPort());
					waiting = true;
					clientSocket = serverSocket.accept();
					waiting = false;
					gui.setText("Verbindung zum Client aufgebaut: " + clientSocket.getInetAddress().getHostName()
							+ " auf Port: " + clientSocket.getPort());
					gui.indicateHostAddress(serverSocket.getInetAddress().getHostAddress() + " auf Port " + serverSocket.getLocalPort());
					reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
					writer = new PrintWriter(clientSocket.getOutputStream(), true);
					processCommands();
				}catch(IOException ex){
					gui.showErrorMessage("startConnection() fehlgeschlagen", ex.getMessage());
				}
			}
		}).start();
	}
	
	public void processCommands()
	{
		while(!clientSocket.isClosed())
		{
			try{
				if(reader.ready())
					query(reader.readLine());
				else Thread.sleep(100);
			}catch(IOException ex){
				gui.showErrorMessage("processCommands()", ex.getMessage());
				try { clientSocket.close(); } catch (IOException e) {}
			}catch(InterruptedException e){}
		}
		gui.setText("Serverprozess beendet.\nBitte aktualisieren.");
	}
	
	public void query(String cmd) throws IOException
	{
		gui.appendText("Kommando empfangen: " + cmd);
		StringBuilder builder = new StringBuilder(cmd);
		String cmdF = builder.toString();
		int sep = builder.indexOf(" ");
		if(sep != -1)
			cmdF = builder.substring(0, sep);
		if(!running && cmdF.equalsIgnoreCase(Command.NEWGAME))
		{
			String playerarg = builder.substring(sep+1);
			setup(playerarg);
		}
		else if(running && cmdF.equalsIgnoreCase(Command.CHECK))
		{
			String code = builder.substring(sep+1);
			checkCode(code);
		}
		else if(cmdF.equalsIgnoreCase(Command.QUIT))
		{
			quitGame();
		}
		else
		{
			gui.showErrorMessage("Unbekanntes Kommando", "Folgendes Kommando wurde erhalten: " + cmd);
		}
	}
	
	public void endConnection()
	{
		try{
			if(clientSocket != null)
			{
				clientSocket.shutdownInput();
				clientSocket.shutdownOutput();
				reader.close();
				writer.close();
				clientSocket.close();
				gui.appendText("Verbindung zum Client geschlossen.");
			}
			running = false;
			startConnection(port);
		}catch(IOException ex){
			gui.showErrorMessage("endConnection() fehlgeschlagen", ex.getMessage());
		}
	}
	
	public void checkCode(String colorcode)
	{
		gui.addNewColorCode(colorcode);
		if(colorcode.equalsIgnoreCase(this.colorcode))
		{
			writer.write(String.format("%s %s\n", Command.RESULT, "BBBB"));	// muss überarbeitet werden
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
		writer.flush();
	}
	
	public void setup(String playername)
	{
		this.playername = playername;
		availableTries = tries;
		running = true;
		
		if(automatic)
		{
			char[] codeSet = new char[codelength];
			Random rand = new Random(System.currentTimeMillis());
			for(int i=0; i<codeSet.length; i++)
				codeSet[i] = Command.COLORSET[rand.nextInt(availableColors.length)];
			colorcode = new String(codeSet);
			gui.appendText("Client muss raten: " + colorcode);
		}
		writer.write(String.format("%s %d %s\n", Command.SETUP, codelength, new String(availableColors)));
		writer.write(String.format("%s\n", Command.GUESS));
		writer.flush();
	}
	
	public void quitGame()
	{
		gui.appendText("Spiel wird beendet...");
		// writer.write(String.format("%s\n", Command.QUIT));
		// writer.flush();
		endConnection();
	}
	
	public void setColorCode(String code)
	{
		this.colorcode = code;
		codelength = code.length();
	}
	
	public void changePort(int port)
	{
		if(port >= 50000 && port <= 50100)
		{
			gui.appendText("Port wird geändert auf: " + port);
			this.port = port;
		}
		endConnection();
	}
	
	public void setConfiguration(boolean automatic, int codelength, String colors, int tries, int port)
	{
		if(!running && !waiting){
			this.automatic = automatic;
			this.codelength = codelength;
			availableColors = new char[colors.length()];
			for(int i=0; i<availableColors.length; i++)
				availableColors[i] = colors.charAt(i);
			this.tries = tries;
			score = this.tries * 10000;
			changePort(port);
		}else
			gui.showErrorMessage("setConfiguration()", "Konfiguration kann nicht während eines laufenden Spiels geändert werden.\n"
					+ "Zudem kann es sein, dass der Server bereits wartet.");
	}
	
	public void writeToHighscore()
	{
		Highscore.insertIntoHighscore(playername, score);
	}
}
