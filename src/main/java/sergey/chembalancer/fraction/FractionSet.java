package sergey.chembalancer.fraction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.math3.fraction.Fraction;

public class FractionSet
{
	private ArrayList<Fraction> fractions;

	public FractionSet()
	{
		this(new Fraction[0]);
	}

	public FractionSet(Fraction[] fs)
	{
		this(Arrays.asList(fs));
	}

	public FractionSet(List<Fraction> fs)
	{
		fractions = new ArrayList<Fraction>(fs);
	}

	public ArrayList<Fraction> getFractions()
	{
		return fractions;
	}

	public void removeDenoms()
	{

		for (Fraction f : fractions) {
			int denom = f.getDenominator();
			for (int i = 0; i<fractions.size(); i++) {
				fractions.set(i, fractions.get(i).multiply(new Fraction(denom, 1)));
			}
		}
		
		int gcd = fractions.get(0).getNumerator();
		
		for (int i = 1; i<fractions.size(); i++) {
			gcd = getGreatestCommonDenominator(gcd, fractions.get(i).getNumerator());
		}

		for (int i = 0; i<fractions.size(); i++) {
			fractions.set(i, fractions.get(i).divide(new Fraction(gcd, 1)));
		}
	}
/*

	private long getLeastCommonMultiple(long a, long b)
	{
		// The least common denominator is equal to the absolute values of a and
		// b multiplied
		// towards each other divided by the two numbers' greatest common
		// denominator
		return Math.abs(a) * Math.abs(b) / getGreatestCommonDenominator(a, b);
	}*/

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
