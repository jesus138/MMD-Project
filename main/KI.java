package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Repraesentiert die Kuenstliche Intelligenz des Mastermind-Clients. Sorgt fuer
 * das automatisierte Raten, wenn sich der Client im Automatikmodus befindet und
 * wird entsprechend von diesem verwendet. Angelehnt ist der verwendete
 * Algorithmus an dem von Donald Knuth.
 * 
 * @author Shelly
 * @category Logikkomponente
 */

public class KI {
	char[] colorSelection, colorSelectionTemp, oldColorSelection;
	ArrayList<String> guessCodeList = new ArrayList<String>();
	ArrayList<String> resultList = new ArrayList<String>();
	ArrayList<String> wrongGuessCodeList = new ArrayList<String>();
	String guessCode, serverResult, internalResult = "01";
	StringBuilder firstGuessCodeBuilder = new StringBuilder();
	int createFirstGuessIndex = 0;

	/**
	 * Initialisiert die KI fuer eine Runde. Danach wird laut Donald Knuth's
	 * Strategie das erste Codewort generiert. Das beste Codewort, um das Spiel
	 * anzufangen, waere laut Knuths Strategie Rot,Rot,Gruen,Gruen. Also mit zwei
	 * verschiedenen Farben. Das Codewort wird je nach Farbpalette und Codelaenge
	 * angepasst.
	 * 
	 * @param colors
	 *            Farbpalette
	 * @param codelength
	 *            Laenge des zu erratenen Codewortes
	 */
	public KI(int codelenght, String colorRange) {
		oldColorSelection = new char[colorRange.length()];
		for (int i = 0; i < oldColorSelection.length; i++) {
			oldColorSelection[i] = colorRange.charAt(i);
		}
		colorSelection = Arrays.copyOf(oldColorSelection, oldColorSelection.length);
		while (createFirstGuessIndex < 2) {
			for (int j = 0; j < codelenght / 2; j++) {
				firstGuessCodeBuilder.append(colorSelection[createFirstGuessIndex]);
			}
			createFirstGuessIndex++;
		}
		if (codelenght % 2 != 0)
			firstGuessCodeBuilder.append((colorSelection[createFirstGuessIndex]));
		createFirstGuessIndex = 0;
		guessCode = firstGuessCodeBuilder.toString();
	}

	/**
	 * Diese Get-Methode gibt das neu generierte Codewort fuer den naechsten
	 * Rateversuch zurueck.
	 * 
	 * @return gibt das neue Codewort zurueck.
	 */
	public String getGuess() {
		return guessCode;
	}

	/**
	 * Prueft erst, ob das Spiel schon gewonnen ist, wenn nicht dann wird ein
	 * neues und zum Spiel am besten passendes Codewort generiert. Falls das
	 * Resulat davor 0 war, loescht er, durch die deleteColorts() Methode, die
	 * Farben die man nicht mehr braucht und laesst einen neuen und am besten zum
	 * Spiel passenden Codewort generieren.
	 * 
	 * @param result
	 *            Neues Resultat vom letzten abgegebenen Rateveruch.
	 */
	public void nextRes(String result) {
		boolean win = false;
		try {
			for (int j = 0; j < guessCode.length(); j++) {

				if (result.charAt(j) != 'B') {
					win = false;
					break;
				} else
					win = true;
			}
		} catch (Exception e) {
			win = false;
		}
		if (win == false) {
			if (result.length() == guessCode.length()) {
				for (int i = 0; i < colorSelection.length; i++) {
					int check = 0;
					for (int y = 0; y < guessCode.length(); y++) {
						if (colorSelection[i] == guessCode.charAt(y)) {
							check++;
						}
					}
					if (check == 0) {
						colorSelection[i] = '0';
					}
				}
			}
			if (result.equals("0")) {
				deleteColors();
				checkResult(result);
				algorithm(result);
			} else {
				checkResult(result);
				algorithm(result);
			}
		} else {
			guessCodeList.clear();
			resultList.clear();
			wrongGuessCodeList.clear();
			createFirstGuessIndex = 0;
			guessCode = firstGuessCodeBuilder.toString();
			colorSelection = Arrays.copyOf(oldColorSelection, oldColorSelection.length);
		}
	}

	/**
	 * Die Methode checkResult() prueft, ob durch das letzte generierte Codewort
	 * sich das Resultat verschlechtert hat. Wenn nicht speichert er das
	 * Codewort und das Resultat in ein ArrayList.
	 * 
	 * @param result
	 *            Neues Resultat vom letzten abgegebenen Rateveruch.
	 */
	private void checkResult(String result) {
		double newResult = 0;
		double oldResult = 0;
		try {
			for (int n = 0; n < result.length(); n++) {
				if (result.charAt(n) == 'W')
					newResult += 1.7;
				if (result.charAt(n) == 'B')
					newResult += 3.0;
			}
			for (int i = 0; i < resultList.get(resultList.size() - 1).length(); i++) {
				if (resultList.get(resultList.size() - 1).charAt(i) == 'W')
					oldResult += 1.7;
				if (resultList.get(resultList.size() - 1).charAt(i) == 'B')
					oldResult += 3.0;
			}
		} catch (Exception e) {
		}
		if (createFirstGuessIndex == 0 && result.equals("0")) {
			resultList.add("0");
			guessCodeList.add(guessCode);
			createFirstGuessIndex++;
		} else {
			if (newResult < oldResult) {
				wrongGuessCodeList.add(guessCode);
				serverResult = resultList.get(resultList.size() - 1);
				guessCode = guessCodeList.get(guessCodeList.size() - 1);
			} else {
				resultList.add(serverResult = result);
				guessCodeList.add(guessCode);
			}
		}
	}

	/**
	 * Die algorithm() Methode laesst ein neues Codewort generieren. Alle
	 * Codewoerter und Resultate die bereits an den Server geschickt und vom
	 * Server zurueck kamen wurden in zwei ArryListen gespeichert. Ein Arraylist
	 * fuer die Codewoerter und eines fuer die Resultate. Es wird ein neues
	 * zufaelliges Codewort generiert mit den Farben die er noch zur Auswahl hat.
	 * Danach wird das Codewort mit allen Resultaten die vom Server gekommen
	 * sind verglichen. Wenn das Resultat gleich ist wird das Codewort genommen.
	 * 
	 * @param result
	 *            Neues Resultat vom letzten abgegebenen Rateveruch.
	 */
	private void algorithm(String result) {
		int index = 0;
		boolean test = false;
		for (int n = 0; n < guessCodeList.size(); n++) {
			while (true) {
				if (index != 0) {
					internalResult = checkCombination(guessCode, guessCodeList.get(n));
					test = internalResult.equals(resultList.get(n));
					if (test == true) {
						break;
					} else
						n = 0;
				}
				index++;
				guessCode = getNewGuessCode(guessCode);
				internalResult = checkCombination(guessCode, guessCodeList.get(n));

				if (internalResult.equals(resultList.get(n)))
					break;
				n = 0;
				index = 0;
			}
		}
		boolean check = false;
		for (int i = 0; i < guessCodeList.size(); i++) {
			check = guessCode.equals(guessCodeList.get(i));
			if (check == true)
				algorithm(result);
		}
		for (int y = 0; y < wrongGuessCodeList.size(); y++) {
			check = guessCode.equals(wrongGuessCodeList.get(y));
			if (check == true)
				algorithm(result);
		}
		internalResult = "01";
	}

	/**
	 * Generiert intern ein neues Codewort. Dieser Algorithmus waehlt ein
	 * zufaelliges Codewort mit den Farben die er noch zur Auswahl hat.
	 * 
	 * @param oldGuessCode
	 *            Das letze generierte Codewort.
	 * 
	 * @return gibt das neu generierte Codewort zurueck.
	 */
	private String getNewGuessCode(String oldGuessCode) {
		char[] c = oldGuessCode.toCharArray();
		int randomColor;
		for (int i = 0; i < oldGuessCode.length(); i++) {
			boolean check = false;
			do {
				randomColor = new Random().nextInt(colorSelection.length + 1);
				if (randomColor != 0) {
					if (colorSelection[randomColor - 1] != '0')
						check = true;
				}
			} while (check == false);
			c[i] = colorSelection[randomColor - 1];
		}
		return oldGuessCode = new String(c);
	}

	/**
	 * Erstellt ein Resultat zwischen dem neuen und einem alten bereits an den
	 * Server geschickten Codewort.
	 * 
	 * @param newGuessCode
	 *            Das neue generierte Codewort.
	 * @param checkCode
	 *            Ein bereits an den Server schon geschicktes Codewort.
	 * @return gibt das Resultat aus beiden Codewoertern aus.
	 */
	private String checkCombination(String newGuessCode, String checkCode) {
		String feedback = "0";
		StringBuilder sb = new StringBuilder();
		int right = 0;
		boolean[] codeChecked = new boolean[checkCode.length()];
		boolean[] guessChecked = new boolean[newGuessCode.length()];
		for (int i = 0; i < newGuessCode.length() && i < checkCode.length(); i++) {
			if (newGuessCode.charAt(i) == checkCode.charAt(i)) {
				right++;
				guessChecked[i] = true;
				codeChecked[i] = true;
				sb.append("B");
			}
		}
		for (int i = 0; i < newGuessCode.length(); i++) {
			if (!guessChecked[i])
				for (int j = 0; j < checkCode.length(); j++) {
					if (!codeChecked[j] && newGuessCode.charAt(i) == checkCode.charAt(j)) {
						right++;
						codeChecked[j] = true;
						sb.append("W");
						break;
					}
				}
		}
		if (right == 0)
			sb.append("0");
		feedback = sb.toString();
		return feedback;
	}

	/**
	 * Loescht alle Farben die nach einem Result von 0 nicht mehr benoetigt
	 * werden. Das bedeutet alle Farben die im letzten Codewort enthalten waren.
	 */
	private void deleteColors() {
		colorSelectionTemp = new char[colorSelection.length];
		for (int i = 0; i < colorSelection.length; i++) {
			for (int y = 0; y <= guessCode.length() - 1; y++) {
				while (colorSelection[i] == guessCode.charAt(y)) {
					colorSelectionTemp[i] = '0';
					break;
				}
				if (colorSelectionTemp[i] != '0')
					colorSelectionTemp[i] = colorSelection[i];
			}
		}
		colorSelection = colorSelectionTemp;
	}
}