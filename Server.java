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
	
	private String playername;
	private boolean automatic;
	private long score;
	private int availableTries;
	
	private String colorcode;
	private char[] availableColors;
	private int codelength;
	private int tries;
	
	private boolean running;
	private boolean connected;
	private ServerGui gui;
	private ServerSocket serverSocket;
	private Socket clientSocket;
	private PrintWriter writer;
	private BufferedReader reader;
	
	public Server()
	{
		codelength = 4;
		tries = 7;
		score = 10000 * tries;
		availableColors = new char[6];
		for(int i=0; i<availableColors.length; i++) availableColors[i] = Command.COLORSET[i];
		automatic = true;
		running = false;
		connected = false;
		gui = new ServerGui(this);
		gui.appendText("Mit aktualisieren starten");
	}
	
	public static void main(String[] args)
	{
		new Server();
	}
	
	public void startConnection()
	{
		new Thread(new Runnable(){
			public void run(){
				try{
					serverSocket = new ServerSocket(port);
					gui.appendText("Warten auf Client auf Port " + serverSocket.getLocalPort());
					clientSocket = serverSocket.accept();
					connected = true;
					gui.setPlayMode();
					gui.appendText("Verbindung zum Client aufgebaut: " + clientSocket.getInetAddress().getHostName()
							+ " auf Port: " + clientSocket.getPort());
					gui.indicateHostAddress(serverSocket.getInetAddress().getHostAddress() + " auf Port " + serverSocket.getLocalPort());
					reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
					writer = new PrintWriter(clientSocket.getOutputStream(), true);
					processCommands();
				}catch(IOException ex){
					gui.appendText("Verbindung aktualisieren: " + ex.getMessage());
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
		gui.appendText("Serverprozess beendet.");
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
			endConnection();
		}
		else
		{
			gui.showErrorMessage("Unbekanntes Kommando", "Folgendes Kommando wurde erhalten: " + cmd);
		}
	}
	
	public void endConnection()
	{
		try{
			if(clientSocket != null && !clientSocket.isClosed())
			{
				writer.write(String.format("%s\n", Command.QUIT));
				writer.flush();
				clientSocket.shutdownInput();
				clientSocket.shutdownOutput();
				reader.close();
				writer.close();
				clientSocket.close();
				gui.appendText("Verbindung zum Client geschlossen.");
			}
			if(serverSocket != null)
				serverSocket.close();
		}catch(IOException ex){
			gui.showErrorMessage("endConnection() fehlgeschlagen", ex.getMessage());
		}
		running = false;
		connected = false;
		gui.setSetupMode();
		startConnection();
	}
	
	public void checkCode(String codeguess)
	{
		String feedback = "0";
		availableTries--;
		score -= 1000;
		
		StringBuilder sb = new StringBuilder();
		int right = 0, completelyRight = 0;
		boolean[] codeChecked = new boolean[colorcode.length()];
		boolean[] guessChecked = new boolean[codeguess.length()];
		for(int i=0; i<codeguess.length() && i<colorcode.length(); i++){
			if(codeguess.charAt(i) == colorcode.charAt(i)){
				right++;
				completelyRight++;
				guessChecked[i] = true;
				codeChecked[i] = true;
				sb.append(Command.RESULT_RIGHT_PLACE);
			}
		}
		for(int i=0; i<codeguess.length(); i++){
			if(!guessChecked[i])
				for(int j=0; j<colorcode.length(); j++){
					if(!codeChecked[j] && codeguess.charAt(i) == colorcode.charAt(j)){
						right++;
						codeChecked[j] = true;
						sb.append(Command.RESULT_WRONG_PLACE);
						break;
					}
				}
		}
		if(right == 0) sb.append(Command.RESULT_ALL_WRONG);
		boolean won = completelyRight == colorcode.length();
		boolean lost = availableTries <= 0 && !won;
		feedback = sb.toString();
		
		writer.write(String.format("%s %s\n", Command.RESULT, feedback));
		if(won)
			writer.write(String.format("%s %s\n", Command.GAMEOVER, Command.GAMEOVER_WIN));
		else if(lost)
			writer.write(String.format("%s %s\n", Command.GAMEOVER, Command.GAMEOVER_LOSE));
		else
			writer.write(String.format("%s\n", Command.GUESS));
		
		writer.flush();
		gui.addNewColorCode(codeguess, feedback);
	}
	
	public void setup(String playername)
	{
		this.playername = playername;
		availableTries = tries;
		running = true;
		
		if(automatic)
		{
			char[] codeSet = new char[codelength];
			char[] pool = new char[availableColors.length];
			System.arraycopy(availableColors, 0, pool, 0, availableColors.length);
			Random rand = new Random(System.currentTimeMillis());
			for(int i=0; i<codeSet.length; i++)
				codeSet[i] = pool[rand.nextInt(availableColors.length)];
			colorcode = new String(codeSet);
			gui.setSelectionColorCode(colorcode);
		}
		else colorcode = gui.getSelectedCode();
		
		gui.appendText("Client muss raten: " + colorcode);
		writer.write(String.format("%s %d %s\n", Command.SETUP, codelength, new String(availableColors)));
		writer.write(String.format("%s\n", Command.GUESS));
		writer.flush();
	}
	
	public void changePort(int port)
	{
		if(port >= 50000 && port <= 50100)
		{
			gui.appendText("Port wird geändert auf: " + port);
			this.port = port;
		}
	}
	
	public void setConfiguration(boolean automatic, int codelength, String colors, int tries, int port)
	{
		this.automatic = automatic;
		this.codelength = codelength;
		availableColors = new char[colors.length()];
		for(int i=0; i<availableColors.length; i++)
			availableColors[i] = colors.charAt(i);
		this.tries = tries;
		score = this.tries * 10000;
		changePort(port);
		if(!connected)
			endConnection();
		else
			gui.appendText("Änderungen werden beim nächsten Verbindungsaufbau in Kraft treten.");
	}
	
	public boolean isRunning()
	{
		return running;
	}
	
	public void instantQuit()
	{
		endConnection();
	}
	
	public void writeToHighscore()
	{
		Highscore.insertIntoHighscore(playername, score);
	}
}
