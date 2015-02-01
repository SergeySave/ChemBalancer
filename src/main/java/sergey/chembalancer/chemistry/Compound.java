package sergey.chembalancer.chemistry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Compound
{

	private Compound[] compounds;
	private int number;
	private String str;

	public static Compound[] getListOfCompounds(String compounds)
	{
		String temp = compounds.replace(" ", "");
		String[] cpndStrs = temp.split("\\+");
		Compound[] cpnds = new Compound[cpndStrs.length];

		for (int i=0; i<cpndStrs.length; i++) {
			if (cpndStrs.equals("")) {
				continue;
			}
			cpnds[i] = new Compound(cpndStrs[i]);
		}

		return cpnds;
	}

	private final static String singleString = "\\p{javaUpperCase}\\p{javaLowerCase}*\\d*";
	private final static String multiString = "\\(?(" +singleString + ")*\\)?\\d*";
	private final static String anyString = "(" + singleString + "|" + multiString + ")*";
	private final static String anyBetterString = "(\\[" + anyString + "\\]\\d*)*";

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
		
		if (number == 0) {
			number = 1;
		}
	}

	public static boolean isValidCompound(String compoundString) {
		if (compoundString.matches(singleString)||compoundString.matches(multiString)||compoundString.matches(anyString)||compoundString.matches(anyBetterString)) {
			return true;
		}
		return false;
	}

	private void initSingle(String compoundString) {
		int index = getIndexOfFirstNumber(compoundString);

		String name;
		String num;

		if (index == compoundString.length()) {
			name = compoundString;
			num = "1";
		} else {
			name = compoundString.substring(0, index);
			num = compoundString.substring(index);
		}


		str = name;
		number = Integer.parseInt(num);
	}
	private void initMulti(String compoundString) {
		String temp = compoundString;
		int num = 1;

		if (temp.startsWith("(")) {
			temp = temp.substring(1);

			int index = temp.lastIndexOf(")");

			if (index != -1 && temp.substring(index).matches("\\)\\d*")) {
				num = Integer.parseInt(temp.substring(index + 1));
			}

			if (index != -1) {
				temp = temp.substring(0, index);
			}
		}
		String[] singles = getMatches(singleString, temp);
		number = num;

		ArrayList<Compound> cpnds = new ArrayList<Compound>();
		for (String s : singles) {
			if (!isValidCompound(s)) {
				continue;
			}
			cpnds.add(new Compound(s));
		}

		compounds = cpnds.toArray(new Compound[] {});
	}
	private void initAny(String compoundString) {
		String temp = compoundString;
		int num = 1;

		String[] makeup = getMatches("(" + multiString + "|" + singleString + ")", temp);
		number = num;

		ArrayList<Compound> cpnds = new ArrayList<Compound>();
		for (String s : makeup) {
			if (!isValidCompound(s)) {
				continue;
			}
			cpnds.add(new Compound(s));
		}

		compounds = cpnds.toArray(new Compound[] {});
	}
	private void initBetter(String compoundString) {
		String temp = compoundString;

		String[] makeup = getMatches("\\[" + anyString + "\\]\\d*", temp);

		ArrayList<Compound> cpnds = new ArrayList<Compound>();
		for (String s : makeup) {
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

		compounds = cpnds.toArray(new Compound[] {});
	}

	private String[] getMatches(String regex, String str) {
		List<String> allMatches = new ArrayList<String>();
		Matcher m = Pattern.compile(regex).matcher(str);
		while (m.find()) {
			allMatches.add(m.group());
		}

		return allMatches.toArray(new String[] {});
	}

	private static int getIndexOfFirstNumber(String str) {
		for (int i=0; i<str.length(); i++) {
			if (Character.isDigit(str.charAt(i))) {
				return i;
			}
		}
		return str.length();
	}

	public Compound(Compound cpnd, int num)
	{
		compounds = new Compound[] {cpnd};
		number = num;
	}
	public Compound(String cpnd, int num)
	{
		str = cpnd;
		number = num;
	}
	public Compound(Compound ... cpnd)
	{
		compounds = cpnd;
		number = 1;
	}

	public boolean isSingleElement() {
		return compounds == null;
	}

	public int getNumber()
	{
		return number;
	}

	public Compound setNumber(int num) {
		number = num;
		return this;
	}

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

	public String[] getUniqueElementNames() {
		ArrayList<String> names = new ArrayList<String>();

		if (isSingleElement()) {
			names.add(str);
		} else {
			for (Compound c : compounds) {
				names.addAll(Arrays.asList(c.getUniqueElementNames()));
			}
		}

		return names.toArray(new String[] {});
	}

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
