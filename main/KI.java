package main;

import java.util.Random;

public class KI
{
	private int codelength;
	private String palette;
	
	public KI(String colors, int codelength)
	{
		this.codelength = codelength;
		palette = colors;
	}
	
	public String getGuess()
	{
		StringBuilder builder = new StringBuilder();
		Random rand = new Random(System.currentTimeMillis());
		for(int i=0; i<codelength; i++)
			builder.append(palette.charAt(rand.nextInt(palette.length())));
		return builder.toString();
	}
	
	public void nextRes(String result){}
}
