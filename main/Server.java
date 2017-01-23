package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

import gui.ServerGui;

/**
 * Server-Programm des Mastermind Spiels. Startet den
 * Netzwerkthread auf einen einstellbaren Port und wartet auf eine
 * Clientverbindung. Dazu werden ein ServerSocket und ein (Client-)
 * Socket verwendet. Die Klasse speichert ausserdem alle notwendigen
 * Informationen welche die Highscore und den aktuellen Spielzustand bzw.
 * Verbindungsszustand betreffen. Sie ist eng verzahnt mit der ServerGui
 * Klasse, mit welche den UI-Thread des Servers liefert. Dieser ist dem
 * Netzwerkthread uebergeordnet.
 * <b>Wichtig:</b> Beim Start des Servers startet dieser noch keinen
 * Netzwerkthread. Dies wird erst nach dem ersten Aktualisieren der
 * SeverGui gemacht.
 * @author Chris
 * @category Netzwerkkomponente
 */
public class Server
{
	private int port;
	
	private String playername;
	private int score, halfright, fullright;
	private boolean automatic, unlimited;
	private int availableTries;
	private ServerGui gui;
	
	private String colorcode;
	private char[] availableColors;
	private int codelength;
	private int tries;
	
	private volatile boolean running;
	private volatile boolean connected;
	private boolean clientKnown;
	private ServerSocket serverSocket;
	private Socket clientSocket;
	private PrintWriter writer;
	private BufferedReader reader;
	
	/**
	 * Konstruktor erstellt die ServerGui fuer den Server.
	 * Zudem werden Netzwerkport, Codelaenge, Anzahl Versuche,
	 * verfuegbare Farben und Modus auf die Standardwerte gesetzt.
	 */
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
	
	/**
	 * Startet den Netzwerkthread welcher zunaechst auf einen Client wartet.
	 * Sollte sich ein Client verbunden haben geht der Server in den Spielezustand.
	 * In diesem koennen vorerst keine Einstellungen mehr geaendert werden.
	 * Dabei schaut der Server alle 5 ms in den Lesekanal zum Client um
	 * Kommandos entgegenzunehmen. Diese werden an die Funktion query() delegiert.
	 * Sollten waehrend einer noch stehenden Verbindung abprupte Probleme auftreten,
	 * wird die Verbindung mit disconnect() geschlossen.
	 */
	public void connect()
	{
		new Thread(new Runnable(){
			public void run(){
				try{
					serverSocket = new ServerSocket(port);
					gui.appendMessage("Server " + serverSocket.getInetAddress().getHostAddress()
							+ " wartet auf Port " + serverSocket.getLocalPort());
					gui.showHostAddress(serverSocket.getLocalSocketAddress().toString());
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
							else Thread.sleep(5);
						}catch(Exception ex){
							disconnect();
						}
					}
				}catch(IOException ex){}
			}
		}).start();
	}
	
	/**
	 * Verarbeitet alle vom Client empfangenen Kommandos und Nachrichten.
	 * Diese werden nach dem Netzwerkprotokoll der Mastermind-Anwendung
	 * mit einem StringBuilder zerlegt und entsprechend ausgewertet.<br/>
	 * <ul>
	 * <li>NEWGAME ruft setup() auf</li>
	 * <li>CHECK ruft checkCode() auf</li>
	 * <li>QUIT ruft disconnect() auf</li>
	 * </ul>
	 * Nicht spezifizierte Kommandos werden ignoriert.
	 * @param cmd Kommando des Clients
	 * @throws IOException
	 */
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
	
	/**
	 * Schliesst die stehende Verbindung und beendet den aktuell aktiven
	 * Netzwerkthread. Die Datenkanaele werden nur geschlossen falls diese
	 * nicht schon zuvor geschlossen wurden. Bevor sie geschlossen werden,
	 * wird bei stehender Verbindung noch ein QUIT Kommando an den Client
	 * gesendet, denn es handelt sich um ein vorzeitiges Beenden der des
	 * Spiels. In jedem Falle wird die ServerGui wieder in den Setupmodus
	 * gesetzt, um neue Einstellungen aktualisieren zu koennen bevor sich der
	 * naechste Client verbindet.
	 * <b>Beachten:</b> connect() wird am Ende dieser Funktion wieder aufgerufen.
	 * Dies bedeutet, ein neuer Netzwerkthread ersetzt den alten, sodass sich
	 * gleich wieder ein neuer Client verbinden koennte.
	 */
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
	
	/**
	 * Generiert den Resultatcode fuer einen Rateversuch.
	 * Dabei werden zuerst komplett richtige Farben geprueft und diese
	 * sowohl im richtigen Codewort als auch im Rateversuch als abgearbeitet
	 * markiert. Danach werden die noch nicht abgearbeiteten Farben geprueft.
	 * Sollte nichts richtig gewesen sein, dann bleibt das Resultat bei 0.
	 * Ansonsten ist es eine entsprechende Kombination aus den Buchstaben B und W.
	 * Dies ist auch in der Command Klasse spezifiziert. ueber die noch verbliebene
	 * Anzahl der Versuche, bzw. wenn nicht unendlich eingestellt ist, wird ermittelt
	 * ob gewonnen, verloren wurde oder es normal weitergeht. Entsprechende
	 * Kommandokombinationen werden an den Client gesendet. Sollte das Spiel zu Ende
	 * sein wird writeToHighscore() aufgerufen.
	 * @param codeguess Rateversuch des Clients
	 */
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
	
	/**
	 * Ein Client hat sich verbunden, wodurch alle Einstellungen die zuvor
	 * in der ServerGui gemacht wurden uebernommen werden. Alle
	 * spielzustandsrelevanten Daten werden initialisert und das zu
	 * erratene Codewort wird zufaellig generiert. Dies erfolgt jedoch nur
	 * im Automatikmodus oder bei wiederholten Spielen. Im manuellen Modus
	 * wird beim ersten Spiel des gerade verbundenen Clients das Codewort
	 * aus dem vom Benutzer gewaehlten Codewort im CodePanel der ServerGui
	 * extrahiert. Bei wiederholten Spielen desselben Clients wird aus
	 * Verklemmungsvorbeugungsgruenden der Automatikmodus verwendet.<br/>
	 * Weiterhin wird der Verlaufsbereich geleert und dem Client werden
	 * die Kommandos SETUP und GUEss geschickt.
	 * @param playername Spielername fuer die Highscore
	 */
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
		}else {
			colorcode = gui.getChosenCode();
			gui.disableButtons();
		}
		gui.clearHistory();
		
		clientKnown = true;
		writer.write(String.format("%s %d %s\n", Command.SETUP, codelength, new String(availableColors)));
		writer.write(String.format("%s\n", Command.GUESS));
		writer.flush();
	}
	
	/**
	 * Aktualisiert die Einstellungen des Servers. Wenn sich ein Client
	 * mit dem Server verbindet, werden die hier zuletzt aktualisierten
	 * Werte fuer das kommende Spiel uebernommen.
	 * @param automatic Spielmodus
	 * @param codelength Laenge des zu erratenen Codeworts
	 * @param colors Farbpalette
	 * @param tries Anzahl Versuche
	 * @param port Netzwerkport
	 */
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
	
	/**
	 * Schreibt am Ende jeder Spielrunde die erreichte Punktzahl
	 * des Spielers in die Highscoretabelle der Apache Derby Datenbank.
	 * Fuer die Berechnung der Punktzahl werden die halb und komplett richtigen
	 * Farbtreffer und die noch uebrig gebliebenen Versuche positiv einbezogen.
	 * Negativ auf die Punktzahl wirkt sich die Anzahl der eingestellten Versuche
	 * aus. Sollten unendlich Versuche eingestellt wurden sein, so kann kein
	 * Highscorewert erzielt werden.
	 * @param won true -> Bonus
	 */
	public void writeToHighscore(boolean won){
		int bonus = won ? 10000 : 0;
		double df = (double) (fullright*100) / ((tries-availableTries)*codelength/2.0D);
		double dh = (double) (halfright*100) / ((tries-availableTries)*codelength/2.0D);
		double rt = ((double)availableTries*10000)/tries;
		score = (int)(350*df + 150*dh + 400*rt) + bonus;
		gui.appendMessage(playername + " scored " + score);
		Highscore.insertIntoHighscore(playername, score);
	}
}
