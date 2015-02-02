package sergey.chembalancer.chemistry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 
 * @author sergeys
 *
 */
public class Compound
{
	//An array of subcompounds
	private Compound[] compounds;
	
	//The amount of this compound
	private int number;
	
	//The name of this element (if it is an element)
	private String str;

	/**
	 * Create a array of compounds from a specific string
	 * 
	 * @param compounds a string to create the array from
	 * @return
	 */
	public static Compound[] getListOfCompounds(String compounds)
	{
		//Remove spaces
		String temp = compounds.replace(" ", "");
		
		//Split the string at the + signs
		String[] cpndStrs = temp.split("\\+");

		//Create the array of compounds
		return Arrays.stream(cpndStrs).parallel()
				.filter(s -> !s.equals(""))
				.map(s -> new Compound(s))
				.collect(Collectors.toList())
				.toArray(new Compound[] {});
	}

	//A single element. Formula: [UppercaseLetter][Lowercase letters(0-infinity amount)][Any number or no number]
	//Example: Fe2, O3, Potato, or Hi
	private final static String singleString = "\\p{javaUpperCase}\\p{javaLowerCase}*\\d*";
	//A group of single elements. Formula: [Optional parenthesis][Any amount of single strings][Optional parenthesis][Any number or no number]
	//Example: (NO3)2, (Fe2O3)2
	private final static String multiString = "\\(?(" +singleString + ")*\\)?\\d*";
	//A chain of elements or groups of any length. Formula: [infinite amount of either single strings or multistrings]
	//Example: Mg(NO3)2
	private final static String anyString = "(" + singleString + "|" + multiString + ")*";
	//A complex chain of the above. Formula: [infinite amount of: [Square bracket][Above chain][Close square bracket][Optional Number] ]
	//Example: [Cr(N2H4CO)6]4[Cr(CN)6]3
	private final static String anyBetterString = "(\\[" + anyString + "\\]\\d*)*";

	/**
	 * Create a compound from a string representation of the compound
	 * 
	 * @param compoundString the string representation of the compound
	 */
	public Compound(String compoundString)
	{
		if (compoundString.matches(singleString)) {
			initSingle(compoundString);
		} else if (compoundString.matches(multiString)) {
			initMulti(compoundString);
		} else if (compoundString.matches(anyString)) {
			initAny(compoundString);
		} else if (compoundString.matches(anyBetterString)) {
			initBetter(compoundString);
		}
		
		//Fix a bug that occured when the number was 0
		if (number == 0) {
			number = 1;
		}
	}

	/**
	 * Check if the compound string is valid
	 * 
	 * @param compoundString a string representation of the compound
	 * @return is it valid
	 */
	public static boolean isValidCompound(String compoundString) {
		if (compoundString.matches(singleString)||compoundString.matches(multiString)||compoundString.matches(anyString)||compoundString.matches(anyBetterString)) {
			return true;
		}
		return false;
	}

	/**
	 * Initialize a single element
	 * 
	 * @param compoundString the string representing this element
	 */
	private void initSingle(String compoundString) {
		//Find the location of the first digit in the sequence
		int index = getIndexOfFirstNumber(compoundString);

		//Name
		String name;
		//Amount
		String num;

		//If there is no number
		if (index == compoundString.length()) {
			//The whole thing is the name
			name = compoundString;
			//The number is 1
			num = "1";
		} else /*If there is a number*/{
			//The name is everything before the number
			name = compoundString.substring(0, index);
			//The number is everything after that
			num = compoundString.substring(index);
		}

		//Set the name and number
		str = name;
		number = Integer.parseInt(num);
	}
	/**
	 * Initialize a multi compound
	 * 
	 * @param compoundString the string representing this compound
	 */
	private void initMulti(String compoundString) {
		//Temporary working string
		String temp = compoundString;
		//The amount
		int num = 1;

		//If it starts with a parenthesis
		if (temp.startsWith("(")) {
			//Remove the parenthesis
			temp = temp.substring(1);

			//Find the last parenthesis
			int index = temp.lastIndexOf(")");

			//Get the number after the parenthesis
			if (index != -1 && temp.substring(index).matches("\\)\\d*")) {
				num = Integer.parseInt(temp.substring(index + 1));
			}

			//Remove the parenthesis
			if (index != -1) {
				temp = temp.substring(0, index);
			}
		}
		
		//Get each single string that is in the multi string
		String[] singles = getMatches(singleString, temp);
		//Set the number
		number = num;

		//Java 8 trickery for getting the compounds
		//Set the subcompounds array
		compounds = Arrays.stream(singles).parallel()
				.filter(s -> Compound.isValidCompound(s))
				.map(s -> new Compound(s))
				.collect(Collectors.toList())
				.toArray(new Compound[] {});
	}
	/**
	 * Initialize an any compound
	 * 
	 * @param compoundString the string representing the compound
	 */
	private void initAny(String compoundString) {
		//Temporary working string
		String temp = compoundString;

		//Get the subcompounds
		String[] makeup = getMatches("(" + multiString + "|" + singleString + ")", temp);
		
		//Set the number to 1
		number = 1;
		
		//Java 8 trickery for getting the compounds
		//Set the subcompounds array
		compounds = Arrays.stream(makeup).parallel()
				.filter(s -> Compound.isValidCompound(s))
				.map(s -> new Compound(s))
				.collect(Collectors.toList())
				.toArray(new Compound[] {});
	}
	/**
	 * Initalize a better compound
	 *  
	 * @param compoundString the string representing the compound
	 */
	private void initBetter(String compoundString) {
		//Temporary workign string
		String temp = compoundString;

		//Get subcompounds
		String[] makeup = getMatches("\\[" + anyString + "\\]\\d*", temp);

		//Subcompounds
		ArrayList<Compound> cpnds = new ArrayList<Compound>();
		for (String s : makeup) {
			//Loading trickery for the compound
			int num = 1;

			s = s.substring(1);

			int index = s.lastIndexOf("]");

			if (index != -1 && s.substring(index).matches("\\]\\d*")) {
				num = Integer.parseInt(s.substring(index + 1));
			}

			if (index != -1) {
				s = s.substring(0, index);
			}
			

			cpnds.add(new Compound(s).setNumber(num));
		}

		//Set the subcompounds array
		compounds = cpnds.toArray(new Compound[] {});
	}

	/**
	 * Get all matches in a string to a specific regular expression
	 * 
	 * @param regex the regular expression
	 * @param str the string to search for mathes in
	 * @return an array of matches
	 */
	private String[] getMatches(String regex, String str) {
		//List of matches
		List<String> allMatches = new ArrayList<String>();
		
		//Create a matcher
		Matcher m = Pattern.compile(regex).matcher(str);
		
		//While there are still more to find
		while (m.find()) {
			//Add them to the matches
			allMatches.add(m.group());
		}

		//Return the array of matches
		return allMatches.toArray(new String[] {});
	}

	/**
	 * Get the index of the first digit in a string
	 * 
	 * @param str the string
	 * @return the index of the first digit
	 */
	private static int getIndexOfFirstNumber(String str) {
		for (int i=0; i<str.length(); i++) {
			if (Character.isDigit(str.charAt(i))) {
				return i;
			}
		}
		return str.length();
	}

	/**
	 * Create a compound out of a compound and a number
	 * 
	 * @param cpnd a compound
	 * @param num the amount of said compound
	 */
	public Compound(Compound cpnd, int num)
	{
		compounds = new Compound[] {cpnd};
		number = num;
	}
	/**
	 * Create a compound out of a string and a number
	 * 
	 * @param cpnd a compound
	 * @param num a number
	 */
	public Compound(String cpnd, int num)
	{
		str = cpnd;
		number = num;
	}
	/**
	 * Create a compound out of an array of compounds
	 * 
	 * @param cpnd an array of compounds
	 */
	public Compound(Compound ... cpnd)
	{
		compounds = cpnd;
		number = 1;
	}

	/**
	 * Check if the compound represents a single element
	 * 
	 * @return does this compound represent a single element
	 */
	public boolean isSingleElement() {
		return compounds == null;
	}

	/**
	 * Get the amount of this compound
	 * 
	 * @return the amount of this compound
	 */
	public int getNumber()
	{
		return number;
	}

	/**
	 * Set the amount of this compound
	 * 
	 * @param num the amount of this compound
	 * @return this compound (for chaining)
	 */
	public Compound setNumber(int num) {
		number = num;
		return this;
	}

	/**
	 * Get the number of elements in this compound
	 * 
	 * @param element the element to check for
	 * @return the amount of the elements
	 */
	public int getNumOfElement(String element) {
		int count = 0;

		if (isSingleElement()) {
			if (str.equals(element)) {
				return number;
			}
		} else {
			for (Compound c : compounds) {
				count += (c.getNumOfElement(element) * number);
			}
		}

		return count;
	}

	/**
	 * Get the names of each unique element in this compound
	 * @return
	 */
	public String[] getUniqueElementNames() {
		ArrayList<String> names = new ArrayList<String>();

		if (isSingleElement()) {
			names.add(str);
		} else {
			Arrays.stream(compounds).forEach(c -> names.addAll(Arrays.asList(c.getUniqueElementNames())));
		}

		return names.toArray(new String[] {});
	}

	/**
	 * Get a string representation of this compound
	 */
	@Override
	public String toString()
	{
		String s = "";

		if (compounds != null) {
			for (Compound c : compounds) {
				s += c.toString();
			}
			if (number > 1) {
				s = "(" + s + ")" + number;
			}
		} else {
			s = str;
			if (number > 1) {
				s = s + number;
			}
		}

		return s;
	}
}
