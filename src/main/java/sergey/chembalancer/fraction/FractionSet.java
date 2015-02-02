package sergey.chembalancer.fraction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.math3.fraction.Fraction;

/**
 * 
 * @author sergeys
 *
 */
public class FractionSet
{
	//Internal list of fractions
	private ArrayList<Fraction> fractions;

	/**
	 * Default constructor
	 */
	public FractionSet()
	{
		this(new Fraction[0]);
	}

	/**
	 * Make a fraction set using an array of fractions
	 * @param fs an array of fractions
	 */
	public FractionSet(Fraction[] fs)
	{
		this(Arrays.asList(fs));
	}

	/**
	 * Make a fraction set using an list of fractions
	 * @param fs a list of fractions
	 */
	public FractionSet(List<Fraction> fs)
	{
		fractions = new ArrayList<Fraction>(fs);
	}

	/**
	 * Get the fractions held by this set
	 * @return an arraylist of fraction objects
	 */
	public ArrayList<Fraction> getFractions()
	{
		return fractions;
	}

	
	/**
	 * Used to remove the denominators from all of the fractions
	 */
	public void removeDenoms()
	{		
		//Get the lcd
		int lcd = fractions.stream()
				.map(f -> f.getDenominator())
				.reduce(1, (i1, i2) -> getLeastCommonMultiple(i1, i2));
		
		//Multiply all of the fractions by the collective lcd
		for (int i = 0; i<fractions.size(); i++)
			fractions.set(i, fractions.get(i).multiply(new Fraction(lcd)));
	}

	private int getLeastCommonMultiple(int a, int b)
	{
		// The least common denominator is equal to the absolute values of a and
		// b multiplied
		// towards each other divided by the two numbers' greatest common
		// denominator
		return Math.abs(a) * Math.abs(b) / getGreatestCommonDenominator(a, b);
	}

	private int getGreatestCommonDenominator(int a, int b)
	{
		while (b != 0)
		{ // Continue looping until b is 0
			int t = b; // Temporary storage of b
			b = a % b; // b is now the remainder of the division of a and b
			a = t; // a is now what b used to be
		}
		return a; // a is the final answer
	}

}
