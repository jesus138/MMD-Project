package mastermind;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client
{
	public static final String CLIENTNAME = "Client03";
	
	private String playername;
	private int codelength;
	private boolean running;
	
	private Socket connection;
	private ClientGui gui;
	private PrintWriter writer;
	private BufferedReader reader;
	
	public Client()
	{
		playername = CLIENTNAME;
		running = false;
		codelength = 4;
		gui = new ClientGui(this);
	}
	
	public void connect(String ipaddress, int port)
	{
		try{
			connection = new Socket(ipaddress, port);
			writer = new PrintWriter(connection.getOutputStream(), true);
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			gui.setToSetupMode();
		}catch(UnknownHostException ex){
			gui.showErrorMessage("UnknownHostException connect()", ex.getMessage());
		}catch(IOException ex){
			gui.showErrorMessage("IOException connect()", ex.getMessage());
		}
	}
	
	public void receiveMessages()
	{
		new Thread(new Runnable(){
			public void run(){
				while(!connection.isClosed())
				{
					try{
						if(reader.ready())
							query(reader.readLine());
						else Thread.sleep(100);
					}catch(IOException ex){
						gui.showErrorMessage("Fehler beim Einlesen", ex.getMessage());
						try { connection.close(); } catch(IOException e){}
					}catch(InterruptedException e){}
				}
				gui.setMessage("Verbindung zum Server verloren.");
			}
		}).start();
	}
	
	public void query(String cmd) throws NumberFormatException, IOException
	{
		gui.appendMessage("Server sendete: " + cmd);
		StringBuilder builder = new StringBuilder(cmd);
		int sep = builder.indexOf(" ");
		String cmdF = builder.toString();
		if(sep != -1)
			cmdF = builder.substring(0, sep);
		if(!running && cmdF.equalsIgnoreCase(Command.SETUP))
		{
			String args = builder.substring(sep+1);
			builder = new StringBuilder(args);
			sep = builder.indexOf(" ");
			int codelength = Integer.parseInt(builder.substring(0, sep));
			String colors = builder.substring(sep+1);
			startGame(codelength, colors);
		}
		else if(running && cmdF.equalsIgnoreCase(Command.GUESS))
		{
			gui.setGuessMode();
		}
		else if(running && cmdF.equalsIgnoreCase(Command.RESULT))
		{
			String result = builder.substring(sep+1);
			gui.addColorLine(result);
			presentResult(result);
		}
		else if(running && cmdF.equalsIgnoreCase(Command.GAMEOVER))
		{
			String result = builder.substring(sep+1);
			endGame(result);
		}
		else if(cmdF.equalsIgnoreCase(Command.QUIT))
		{
			endGame(null);
		}
		else
		{
			gui.showErrorMessage("Unbekanntes Kommando", "Folendes Kommando wurde empfangen: " + cmd);
		}
	}
	
	public void newGame(String playername)
	{
		gui.appendMessage("Initialisiere neues Spiel");
		this.playername = playername;
		writer.write(String.format("%s %s\n", Command.NEWGAME, this.playername));
		writer.flush();
		receiveMessages();
	}
	
	public void startGame(int codelength, String colors)
	{
		this.codelength = codelength;
		gui.setColorPanel(colors);
		running = true;
	}
	
	public void presentResult(String result)
	{
		gui.appendMessage("Ergebnis: " + result);
	}
	
	public void endGame(String result)
	{
		if(result != null)
		{
			gui.setMessage("Glückwunsch! Sie haben gewonnen.");
		}
		try{
			writer.write(String.format("%s\n", Command.QUIT));
			writer.flush();
			writer.close();
			reader.close();
			connection.close();
			gui.appendMessage("Verbindung zum Server wurde getrennt.");
		}catch(IOException ex){
			gui.showErrorMessage("endGame() Fehler", ex.getMessage());
		}
		gui.setToConnectMode();
	}
	
	public void makeGuess(String colorcode)
	{
		writer.write(String.format("%s %s\n", Command.CHECK, colorcode));
		writer.flush();
	}
	
	public void autoPlay()
	{
		// TODO
	}
	
	public int getCodelength()
	{
		return codelength;
	}
	
	public static void main(String[] args)
	{
		new Client();
	}
}
