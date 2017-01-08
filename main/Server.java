package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

import gui.ServerGui;

public class Server
{
	private int port;
	
	private String playername;
	private int score, halfright, fullright;
	private boolean automatic, unlimited;
	private int availableTries;
	private ServerGui gui;
	
	public String colorcode;
	public char[] availableColors;
	public int codelength;
	private int tries;
	
	private volatile boolean running;
	private volatile boolean connected;
	private boolean clientKnown;
	private ServerSocket serverSocket;
	private Socket clientSocket;
	private PrintWriter writer;
	private BufferedReader reader;
	
	public Server()
	{
		port = Command.DEFAULT_PORT;
		codelength = Command.DEFAULT_CODELENGTH;
		tries = Command.DEFAULT_TRIES;
		availableColors = new char[Command.DEFAULT_COLOR_NUM];
		for(int i=0; i<availableColors.length; i++) availableColors[i] = Command.COLORSET[i];
		automatic = true;
		gui = new ServerGui(this);
	}
	
	public void connect()
	{
		new Thread(new Runnable(){
			public void run(){
				try{
					serverSocket = new ServerSocket(port);
					gui.appendMessage("Server " + serverSocket.getInetAddress().getHostAddress()
							+ " wartet auf Port " + serverSocket.getLocalPort());
					clientSocket = serverSocket.accept();
					gui.appendMessage("Client " + clientSocket.getInetAddress().getHostAddress()
							+ " hat sich verbunden");
					connected = true;
					gui.setToPlayMode();
					reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
					writer = new PrintWriter(clientSocket.getOutputStream(), true);
					while(!clientSocket.isClosed())
					{
						try{
							if(reader.ready())
								query(reader.readLine());
							else Thread.sleep(30);
						}catch(IOException ex){
							try { clientSocket.close(); } catch (IOException e) {}
						}catch(InterruptedException e){}
					}
				}catch(IOException ex){}
			}
		}).start();
	}
	
	public void query(String cmd) throws IOException
	{
		gui.appendMessage("Kommando empfangen: " + cmd);
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
		else if(cmdF.equalsIgnoreCase(Command.QUIT)){
			disconnect();
		}
	}
	
	public void disconnect()
	{
		try{
			if(clientSocket != null && !clientSocket.isClosed())
			{
				writer.write(String.format("%s\n", Command.QUIT));
				writer.flush();
				reader.close();
				writer.close();
				clientSocket.close();
			}
			if(serverSocket != null)
				serverSocket.close();
		}catch(IOException ex){}
		running = false;
		connected = false;
		clientKnown = false;
		gui.setToSetupMode();
		connect();
	}
	
	public void checkCode(String codeguess)
	{
		String feedback = "0";
		if(!unlimited)
			availableTries--;
		
		StringBuilder sb = new StringBuilder();
		int right = 0, completelyRight = 0;
		boolean[] codeChecked = new boolean[colorcode.length()];
		boolean[] guessChecked = new boolean[codeguess.length()];
		for(int i=0; i<codeguess.length() && i<colorcode.length(); i++){
			if(codeguess.charAt(i) == colorcode.charAt(i)){
				fullright++;
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
						halfright++;
						right++;
						codeChecked[j] = true;
						sb.append(Command.RESULT_WRONG_PLACE);
						break;
					}
				}
		}
		if(right == 0) sb.append(Command.RESULT_ALL_WRONG);
		boolean won = completelyRight == colorcode.length();
		boolean lost = !unlimited && availableTries == 0 && !won;
		feedback = sb.toString();
		
		gui.addGuess(Command.getColors(codeguess), feedback);
		writer.write(String.format("%s %s\n", Command.RESULT, feedback));
		if(won){
			writer.write(String.format("%s %s\n", Command.GAMEOVER, Command.GAMEOVER_WIN));
			running = false;
			writeToHighscore(true);
		} else if(lost){
			writer.write(String.format("%s %s\n", Command.GAMEOVER, Command.GAMEOVER_LOSE));
			running = false;
			writeToHighscore(false);
		} else
			writer.write(String.format("%s\n", Command.GUESS));
		
		writer.flush();
	}
	
	public void setup(String playername)
	{
		this.playername = playername;
		availableTries = tries;
		halfright = 0;
		fullright = 0;
		score = 0;
		running = true;
		
		if(automatic || clientKnown){
			char[] codeSet = new char[codelength];
			char[] pool = new char[availableColors.length];
			System.arraycopy(availableColors, 0, pool, 0, availableColors.length);
			Random rand = new Random(System.currentTimeMillis());
			for(int i=0; i<codeSet.length; i++)
				codeSet[i] = pool[rand.nextInt(availableColors.length)];
			colorcode = new String(codeSet);
			gui.setCode(colorcode);
		}else colorcode = gui.getChosenCode();
		gui.clearHistory();
		
		clientKnown = true;
		writer.write(String.format("%s %d %s\n", Command.SETUP, codelength, new String(availableColors)));
		writer.write(String.format("%s\n", Command.GUESS));
		writer.flush();
	}
	
	public void configure(boolean automatic, int codelength, String colors, int tries, int port)
	{
		this.automatic = automatic;
		this.codelength = codelength;
		availableColors = new char[colors.length()];
		for(int i=0; i<availableColors.length; i++)
			availableColors[i] = colors.charAt(i);
		unlimited = tries == 0;
		this.tries = tries;
		if(port >= 50000 && port <= 50100)
			this.port = port;
		if(!connected)
			disconnect();
	}
	
	public void writeToHighscore(boolean won){
		int bonus = won ? 10000 : 100;
		score = (50*halfright + 200*fullright + availableTries*300)/(tries+1) + bonus;
		gui.appendMessage(playername + " scored " + score);
		//Highscore.insertIntoHighscore(playername, score);
	}
}
