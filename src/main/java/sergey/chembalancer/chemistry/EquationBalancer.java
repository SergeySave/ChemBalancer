package sergey.chembalancer.chemistry;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.math3.fraction.Fraction;
import org.apache.commons.math3.fraction.FractionField;
import org.apache.commons.math3.linear.FieldMatrix;
import org.apache.commons.math3.linear.MatrixUtils;

import sergey.chembalancer.fraction.FractionSet;
import sergey.chembalancer.math.RREF;

/**
 * 
 * @author sergeys
 *
 */
public class EquationBalancer
{
	//Arrays containing the left and right sides of the equation
	private Compound[] left, right;
	//An array containing all of the coefficients
	private long[] coefficients;
	private boolean isInvalid = false;
	
	/**
	 * Create an equation balancer from a string
	 * 
	 * @param equation the string representation of the equation
	 */
	public EquationBalancer(String equation)
	{
		//Replace any arrows with equals signs
		String nequation = equation.replace("->", "=");
		//Split the string on the equals sign to get the left and right parts
		String[] sides = nequation.split("=");
		
		//Create the left and right parts
		this.left = Compound.getListOfCompounds(sides[0]);
		this.right = Compound.getListOfCompounds(sides[1]);
		//Create the array of coefficients
		coefficients = new long[left.length + right.length];
		
		//Balance the equation
		balance();
	}
	
	/**
	 * Create an equation balancer from the two sides of the equation
	 * 
	 * @param left an array of compound objects representing the left side
	 * @param right an array of compound objects representing the right side
	 */
	public EquationBalancer(Compound[] left, Compound[] right)
	{
		//Set the two sides
		this.left = left;
		this.right = right;
		//Set the length
		coefficients = new long[left.length + right.length];
		
		//Balance the sides
		balance();
	}
	
	private void balance()
	{
		//A list of every element in the equation
		ArrayList<String> str = new ArrayList<String>();
		
		{
			//Use Java 8 streams to add each unique element in the equation to the list
			Arrays.stream(left).forEach(c -> Arrays.stream(c.getUniqueElementNames()).filter(s -> !str.contains(s)).forEach(s -> str.add(s)));
			
			//If there is something on the right that wasn't on the left we have a problem
			if (!Arrays.stream(right).noneMatch(c -> Arrays.stream(c.getUniqueElementNames()).noneMatch(s -> str.contains(s)))) 
				isInvalid = true;
		}
		
		//Create the matrix of Fractions that we will be using
		FieldMatrix<Fraction> matrix = MatrixUtils.createFieldMatrix(FractionField.getInstance(), str.size(), left.length + right.length);
		
		//Fill the matrix
		{			
			//Loop through the different elements we have
			for (int y=0; y<str.size(); y++) {
				//Loop through the different compounds on the left side
				for (int x=0; x<left.length; x++) {
					//Put the amount of elements in that compound of the type we are checking for in the matrix
					matrix.setEntry(y, x, new Fraction(left[x].getNumOfElement(str.get(y))));
				}
				//Loop through the different compounds on the right side
				for (int x=0; x<right.length; x++) {
					//Put the amount of elements in that compound of the type we are checking for in the matrix
					matrix.setEntry(y, x + left.length, new Fraction(right[x].getNumOfElement(str.get(y))));
				}				
			}
		}
		
		//Reduce the matrix
		{
			//Create a object to reduce the matrix
			RREF rref = new RREF();
			
			//Reduce the matrix
			rref.reduce(matrix, matrix.getColumnDimension()-1);
		}
		
		//Resize the matrix
		{
			//Create a second matrix for resizing of the first one
			FieldMatrix<Fraction> mat2 = MatrixUtils.createFieldMatrix(FractionField.getInstance(), Math.max(matrix.getColumnDimension(), matrix.getRowDimension()), Math.max(matrix.getColumnDimension(), matrix.getRowDimension()));
			
			//Copy over the old matrix into the new one
			for (int y=0; y<matrix.getColumnDimension(); y++) {
				for (int x=0; x<matrix.getRowDimension(); x++) {
					mat2.setEntry(x, y, matrix.getEntry(x, y));
				}
			}
			
			//Set bottom left entry to 1
			mat2.setEntry(mat2.getRowDimension()-1, mat2.getColumnDimension()-1, new Fraction(1));
			
			//Set the matrix variable to the new one
			matrix = mat2;
		}
		
		//Get the right most column from the matrix (it contains the answers)
		Fraction[] fract = new Fraction[matrix.getRowDimension()];
		for (int row=0; row<matrix.getRowDimension(); row++) {
			fract[row] = matrix.getEntry(row, matrix.getColumnDimension()-1).abs();
		}
		
		//Create a fraction set to work with the fractions
		FractionSet fset = new FractionSet(fract);
		
		//Multiply all of the fractions by their collective lcd
		fset.removeDenoms();
		
		//Set the coefficients
		for (int i=0; i<fset.getFractions().size(); i++) 
			coefficients[i] = fset.getFractions().get(i).getNumerator();
	}
	
	/**
	 * Create an output string for the equation
	 * 
	 * @param doHtml should the equation add HTML tags (for gui stuff)
	 * @return a string representation of the balanced equation
	 */
	public String getResult(boolean doHtml)
	{
		if (isInvalid) {
			String str = (doHtml ? "<html><center><span style=\"color:red\">" : "");
			
			str += "Invalid Input";
			
			if (doHtml) {
				str += "</span></center></html>";
			}
			
			return str;
		} else {
			String str = (doHtml ? "<html><center>" : "");
			int currLength = 0;
			
			for (int i=0; i<left.length; i++) {
				str += (doHtml ? "<span style=\"color:red\">" : "") + coefficients[i] + (doHtml ? "</span>" : "") + left[i].toString() + " ";
				currLength += (coefficients[i] + "").length() + left[i].toString().length();
				if (i != left.length-1) {
					str += "+ ";
				}
				if (doHtml && currLength > 50) {
					str += "<br/>";
					currLength -= 50;
				}
			}
			
			str += "-> ";
			currLength++;
			
			for (int i=0; i<right.length; i++) {
				str += (doHtml ? "<span style=\"color:red\">" : "") + coefficients[i + left.length] + (doHtml ? "</span>" : "") + right[i].toString() + " ";
				currLength += (coefficients[i + left.length] + "").length() + right[i].toString().length();
				if (i != right.length-1) {
					str += "+ ";
				}
				if (doHtml && currLength > 50) {
					str += "<br/>";
					currLength -= 50;
				}
			}				
			
			if (doHtml) {
				str += "</center></html>";
			}
			
			return str;
		}
	}
}
