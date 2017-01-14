package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import gui.ClientGui;

/**
 * Das Client-Programm des Mastermind Spiels.
 * Stellt einen clientseitigen Socket zur Verfügung und startet
 * nach dessen erfolgreichen Initialisierung den Netzwerkthread.
 * Enthält alle notwendigen clientseitigen Einstellungen wie den
 * Spielernamen, die Codelänge oder den gerade geratenen Farbcode.
 * Außerdem wird die KI Klasse referenziert und im Automatikmodus
 * verwendet.<br/>
 * Die Klasse Client ist mit einer entsprechenden ClientGui Klasse assoziiert.
 * @author Chris
 * @category Netzwerkkommunikation
 */
public class Client
{
	private String playername;
	private String guessCode;
	private int codelength, autorounds;
	private boolean running, automatic;
	private KI ki;
	private ClientGui gui;
	private Socket connection;
	private PrintWriter writer;
	private BufferedReader reader;
	
	/**
	 * Setzt die Standardeinstellungen des Clients und initialisiert
	 * die ClientGui. Diese GUI wird in den Verbindungszustand gesetzt.
	 */
	public Client()
	{
		playername = Command.DEFAULT_NAME;
		codelength = Command.DEFAULT_CODELENGTH;
		gui = new ClientGui(this);
		gui.setToConnectMode();
	}
	
	/**
	 * Versucht eine Verbindung zum Server aufzubauen. Falls
	 * erfolgreich werden die Datenkanäle initialisiert, die GUI
	 * wird in den Einstellungsmodus versetzt und es wird
	 * die Methode receive() aufgerufen, welche Kommandos des Servers
	 * einliest. Sollte keine Verbindung herzustellen sein, wird
	 * eine Fehlermeldung ausgegeben.
	 * @param ipaddress IP-Addresse oder Domainname
	 * @param port Port auf dem der Server seine Dienste anbietet
	 */
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
	
	/**
	 * Trennt eine eventuell bestehende Verbindung zum Server.
	 * Sollte eine Verbindung derzeitig noch bestehen werden alle
	 * Datenkanäle geschlossen und das QUIT-Kommando an den Server
	 * gesendet. In jedem Falle wird die ClientGui wieder in den
	 * Verbindungsmodus gesetzt. 
	 */
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
		automatic = false;
		gui.setToConnectMode();
	}
	
	/**
	 * Startet den Netzwerkthread des Client-Programms.
	 * Der Netzwerkthread schaut alle 5 ms nach ob der Server
	 * ein Kommando gesendet hat. Wenn ja wird dieses an die
	 * query() Funktion zur Verarbeitung übermittelt. Hierbei
	 * werden auch alle möglicherweise auftretenden Exceptions
	 * abgefangen. In diesem Falle wird die Verbindung mittels
	 * disconnect() geschlossen und die Threadschleife endet.
	 */
	public void receive()
	{
		new Thread(new Runnable(){
			public void run(){
				while(!connection.isClosed())
				{
					try{
						if(reader.ready())
							query(reader.readLine());
						else Thread.sleep(5);
					}catch(Exception ex){
						disconnect();
					}
				}
			}
		}).start();
	}
	
	/**
	 * Verarbeitet alle Kommandos die vom Server empfangen werden.
	 * Ausgewertet werden die Kommandos nach Vorgabe des
	 * Mastermind-Netzwerkprotokolls und mit Funktionen der Klasse
	 * StringBuilder.<br/>
	 * <ul>
	 * <li>SETUP führt zum Aufruf von startGame()</li>
	 * <li>GUESS bezieht im Automatikmodus die KI ein</li>
	 * <li>RESULT ruft addGuess() von der GUI auf bzw. liefert der KI
	 * den Resultatstring</li>
	 * <li>GAMEOVER und QUIT rufen endGame() mit jeweils unterschiedlichen
	 * Parametern auf</li>
	 * </ul>
	 * Nicht spezifierte Kommandos werden einfach ignoriert.
	 * @param cmd Kommandostring des Servers
	 * @throws NumberFormatException
	 * @throws IOException
	 */
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
		else if(running && cmdF.equalsIgnoreCase(Command.GUESS)){
			if(automatic)
				makeGuess(ki.getGuess());
		}
		else if(running && cmdF.equalsIgnoreCase(Command.RESULT)){
			String result = builder.substring(sep+1);
			gui.appendMessage("Resultat: " + result);
			gui.addGuess(Command.getColors(guessCode), result);
			if(automatic)
				ki.nextRes(result);
		}
		else if(running && cmdF.equalsIgnoreCase(Command.GAMEOVER)){
			if(automatic) autorounds--;
			String result = builder.substring(sep+1);
			endGame(result);
		}
		else if(cmdF.equalsIgnoreCase(Command.QUIT))
			endGame(null);
	}
	
	/**
	 * Leert den Spielverlauf der GUI und sendet
	 * das Kommando NEWGAME an den Server.
	 * @param playername Spielername der an den Server gesendet wird
	 */
	public void newGame(String playername)
	{
		this.playername = playername;
		gui.clearHistory();
		writer.write(String.format("%s %s\n", Command.NEWGAME, this.playername));
		writer.flush();
	}
	
	/**
	 * Geht in den Spielzustandsmodus über.
	 * Falls Client im Automatikmodus ist wird die
	 * KI initialisiert.
	 * @param codelength Länge des zu erratenen Codewortes
	 * @param colors Farbpalette als Zeichenkettenrepräsentation
	 */
	public void startGame(int codelength, String colors)
	{
		this.codelength = codelength;
		running = true;
		if(!automatic)
		{
			gui.setupGui(Command.getColors(colors), this.codelength);
			gui.setToGuessMode();
		}
		else
			ki = new KI(colors, codelength);
	}
	
	/**
	 * Beendet die aktuelle Runde.
	 * Spieler wird über GUI Funktion again() gefragt ob er weiter spielen will.
	 * Im Automatikmodus wird weitergemacht, insofern noch offene Runden ausstehen.
	 * @param result
	 * falls null dann Verbindungsabbruch
	 * sonst Gameover Nachricht
	 */
	public void endGame(String result)
	{
		if(result != null){
			gui.showMessage("Spiel zu Ende", (result.equals(Command.GAMEOVER_WIN) ?
					"Glückwunsch! Sie haben gewonnen." : "Schade, leider verloren."));
			if(!automatic)
				gui.again();
			else
				if(autorounds > 0)
					newGame(playername);
				else
					disconnect();
		}else disconnect();
	}
	
	/**
	 * Setzt den aktuell geratenen Farbcode und sendet
	 * ihn dem Server mit dem Kommando CHECK.
	 * Wird von der ClientGui im manuellen Modus verwendet,
	 * ansonsten vom Client selbst.
	 * @param colorcode
	 */
	public void makeGuess(String colorcode)
	{
		guessCode = colorcode;
		writer.write(String.format("%s %s\n", Command.CHECK, colorcode));
		writer.flush();
	}
	
	/**
	 * Versetzt die GUI und den Client in den Automatikmodus.
	 * Anschließend wird newGame() aufgerufen um den Server
	 * mitzuteilen, dass eine neue Spielrunde gestartet werden soll.
	 * @param rounds Anzahl der zu spielenden Runden
	 * @param playername Spielername für den Server
	 */
	public void autoPlay(int rounds, String playername)
	{
		autorounds = rounds;
		automatic = true;
		gui.setToAutoMode();
		newGame(playername);
	}
}
