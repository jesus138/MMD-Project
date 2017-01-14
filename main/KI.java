package main;

import java.util.Random;

/**
 * Repr�sentiert die K�nstliche Intelligenz des Mastermind-Clients.
 * Sorgt f�r das automatisierte Raten, wenn sich der Client im
 * Automatikmodus befindet und wird entsprechend von diesem verwendet.
 * Angelehnt ist der verwendete Algorithmus an dem von Donald Knuth.
 * @author Shelly
 * @category Logikkomponente
 */
public class KI
{
	private int codelength;
	private String palette;
	
	/**
	 * Initialisiert die KI f�r eine Runde.
	 * @param colors Farbpalette
	 * @param codelength L�nge des zu erratenen Codewortes
	 */
	public KI(String colors, int codelength)
	{
		this.codelength = codelength;
		palette = colors;
	}
	
	/**
	 * --Beschreibung des Algorithmus von Shelly--
	 * @return Codewort f�r den n�chsten Rateversuch
	 */
	public String getGuess()
	{
		StringBuilder builder = new StringBuilder();
		Random rand = new Random(System.currentTimeMillis());
		for(int i=0; i<codelength; i++)
			builder.append(palette.charAt(rand.nextInt(palette.length())));
		return builder.toString();
	}
	
	/**
	 * Aktualisiert die KI um eine R�ckmeldung zum zuletzt geratenen Codewort.
	 * @param result Auswerteergebnis des Servers
	 */
	public void nextRes(String result){}
}
