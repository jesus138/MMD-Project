package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import gui.ClientGui;

public class Client
{
	private String playername;
	private String guessCode;
	public int codelength;
	public boolean running;
	private ClientGui gui;
	private Socket connection;
	private PrintWriter writer;
	private BufferedReader reader;
	
	public Client()
	{
		playername = Command.DEFAULT_NAME;
		codelength = Command.DEFAULT_CODELENGTH;
		gui = new ClientGui(this);
		gui.setToConnectMode();
	}
	
	public void connect(String ipaddress, int port)
	{
		try{
			connection = new Socket(ipaddress, port);
			writer = new PrintWriter(connection.getOutputStream(), true);
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			gui.setToSetupMode();
			receive();
		}catch(Exception e){
			gui.showMessage("Fehler", "Verbindungsaufbau gescheitert");
		}
	}
	
	public void disconnect()
	{
		if(connection != null && !connection.isClosed()){
			try{
				writer.write(String.format("%s\n", Command.QUIT));
				writer.flush();
				writer.close();
				reader.close();
				connection.close();
			}catch(Exception ex){
				gui.showMessage("Fehler", "Verbingundsabbruch: " + ex.getMessage());
			}
		}
		running = false;
		gui.setToConnectMode();
	}
	
	public void receive()
	{
		new Thread(new Runnable(){
			public void run(){
				while(!connection.isClosed())
				{
					try{
						if(reader.ready())
							query(reader.readLine());
						else Thread.sleep(30);
					}catch(Exception ex){
						disconnect();
					}
				}
			}
		}).start();
	}
	
	public void query(String cmd) throws NumberFormatException, IOException
	{
		gui.appendMessage("Kommando empfangen: " + cmd);
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
		else if(running && cmdF.equalsIgnoreCase(Command.GUESS)){}
		else if(running && cmdF.equalsIgnoreCase(Command.RESULT)){
			String result = builder.substring(sep+1);
			gui.appendMessage("Resultat: " + result);
			gui.addGuess(Command.getColors(guessCode), result);
		}
		else if(running && cmdF.equalsIgnoreCase(Command.GAMEOVER)){
			String result = builder.substring(sep+1);
			endGame(result);
		}
		else if(cmdF.equalsIgnoreCase(Command.QUIT))
			endGame(null);
	}
	
	public void newGame(String playername)
	{
		this.playername = playername;
		gui.clearHistory();
		writer.write(String.format("%s %s\n", Command.NEWGAME, this.playername));
		writer.flush();
	}
	
	public void startGame(int codelength, String colors)
	{
		this.codelength = codelength;
		running = true;
		gui.setupGui(Command.getColors(colors), this.codelength);
		gui.setToGuessMode();
	}
	
	public void endGame(String result)
	{
		if(result != null){
			gui.showMessage("Spiel zu Ende", (result.equals(Command.GAMEOVER_WIN) ?
					"Glückwunsch! Sie haben gewonnen." : "Schade, leider verloren."));
			gui.again();
		}else disconnect();
	}
	
	public void makeGuess(String colorcode)
	{
		guessCode = colorcode;
		writer.write(String.format("%s %s\n", Command.CHECK, colorcode));
		writer.flush();
	}
	
	public void autoPlay(int rounds){}
}
