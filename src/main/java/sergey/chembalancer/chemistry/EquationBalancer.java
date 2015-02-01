package sergey.chembalancer.chemistry;

import java.util.ArrayList;
import org.apache.commons.math3.fraction.Fraction;
import org.apache.commons.math3.fraction.FractionField;
import org.apache.commons.math3.linear.FieldMatrix;
import org.apache.commons.math3.linear.MatrixUtils;
import sergey.chembalancer.fraction.FractionSet;
import sergey.chembalancer.math.RREF;


public class EquationBalancer
{
	private Compound[] left, right;
	private long[] coefficients;

	public EquationBalancer(String equation)
	{
		equation = equation.replace("->", "=");
		String[] sides = equation.split("=");
		
		this.left = Compound.getListOfCompounds(sides[0]);
		this.right = Compound.getListOfCompounds(sides[1]);
		coefficients = new long[left.length + right.length];

		balance();
	}

	public EquationBalancer(Compound[] left, Compound[] right)
	{
		this.left = left;
		this.right = right;
		coefficients = new long[left.length + right.length];

		balance();
	}

	private void balance()
	{
		ArrayList<String> str = new ArrayList<String>();

		for (Compound c : left) {
			for (String s : c.getUniqueElementNames()) {
				if (!str.contains(s)) {
					str.add(s);
				}
			}
		}
		for (Compound c : right) {
			for (String s : c.getUniqueElementNames()) {
				if (!str.contains(s)) {
					str.add(s);
				}
			}
		}
		
		int[][] matr = new int[str.size()][left.length + right.length];
		
		for (int y=0; y<str.size(); y++) {
			for (int x=0; x<left.length; x++) {
				matr[y][x] = left[x].getNumOfElement(str.get(y));
			}
			for (int x=0; x<right.length; x++) {
				matr[y][x + left.length] = right[x].getNumOfElement(str.get(y));
			}				
		}
		
		FieldMatrix<Fraction> matrix = MatrixUtils.createFieldMatrix(FractionField.getInstance(), str.size(), left.length + right.length);
		
		for (int y=0; y<matr.length; y++) {
			for (int x=0; x<matr[y].length; x++) {
				matrix.setEntry(y, x, new Fraction(matr[y][x]));
			}				
		}
		
		RREF rref = new RREF();
		
		rref.reduce(matrix, matrix.getColumnDimension()-1);
		
		FieldMatrix<Fraction> mat2 = MatrixUtils.createFieldMatrix(FractionField.getInstance(), Math.max(matrix.getColumnDimension(), matrix.getRowDimension()), Math.max(matrix.getColumnDimension(), matrix.getRowDimension()));

		for (int y=0; y<matrix.getColumnDimension(); y++) {
			for (int x=0; x<matrix.getRowDimension(); x++) {
				mat2.setEntry(x, y, matrix.getEntry(x, y));
			}
		}

		mat2.setEntry(mat2.getRowDimension()-1, mat2.getColumnDimension()-1, new Fraction(1));

		matrix = mat2;

		Fraction[] fract = new Fraction[matrix.getRowDimension()];
		for (int row=0; row<matrix.getRowDimension(); row++) {
			fract[row] = matrix.getEntry(row, matrix.getColumnDimension()-1).abs();
		}
		
		FractionSet fset = new FractionSet(fract);
		
		fset.removeDenoms();
		
		for (int i=0; i<fset.getFractions().size(); i++) {
			coefficients[i] = fset.getFractions().get(i).getNumerator();
		}
	}

	public String getResult(boolean doHtml)
	{
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
