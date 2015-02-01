package sergey.chembalancer.math;

import org.apache.commons.math3.fraction.Fraction;
import org.apache.commons.math3.linear.FieldMatrix;

public class RREF
{
	// tolerance for singular matrix
	double tol;

	public void setTolerance(double tol) {
		this.tol = tol;
	}

	public void reduce(FieldMatrix<Fraction> A , int coefficientColumns) {
		if( A.getColumnDimension() < coefficientColumns)
			throw new IllegalArgumentException("The system must be at least as wide as A");

		// number of leading ones which have been found
		int leadIndex = 0;
		// compute the decomposition
		for( int i = 0; i < coefficientColumns; i++ ) {

			// select the row to pivot by finding the row with the largest column in 'i'
			int pivotRow = -1;
			double maxValue = tol;

			for( int row = leadIndex; row < A.getRowDimension(); row++ ) {
				Fraction v = A.getEntry(row, i).abs();

				if( v.doubleValue() > maxValue ) {
					maxValue = v.doubleValue();
					pivotRow = row;
				}
			}

			if( pivotRow == -1 )
				continue;

			// perform the row pivot
			// NOTE: performance could be improved by delaying the physical swap of rows until the end
			//       and using a technique which does the minimal number of swaps
			if( leadIndex != pivotRow)
				swapRows(A,leadIndex,pivotRow);

			// zero column 'i' in all but the pivot row
			for( int row = 0; row < A.getRowDimension(); row++ ) {
				if( row == leadIndex ) continue;

				int indexPivot = leadIndex*A.getColumnDimension()+i;
				int indexTarget = row*A.getColumnDimension()+i;

				Fraction alpha = getEntryAtIndex(A, indexTarget).divide(getEntryAtIndex(A, indexPivot++));
				setEntryAtIndex(A, indexTarget++, new Fraction(0));
				for( int col = i+1; col < A.getColumnDimension(); col++ ) {
					setEntryAtIndex(A, indexTarget, getEntryAtIndex(A, indexTarget).subtract(getEntryAtIndex(A, indexPivot++).multiply(alpha)));
					indexTarget++;
				}
			}

			// update the pivot row
			int indexPivot = leadIndex*A.getColumnDimension()+i;
			Fraction alpha = new Fraction(1).divide(getEntryAtIndex(A, indexPivot));
			setEntryAtIndex(A, indexPivot++, new Fraction(1));
			for( int col = i+1; col < A.getColumnDimension(); col++ ) {
				setEntryAtIndex(A, indexPivot, getEntryAtIndex(A, indexPivot).multiply(alpha));
				indexPivot++;
			}
			leadIndex++;
		}
	}
	
	protected static Fraction getEntryAtIndex(FieldMatrix<Fraction> matrix, int index) {
		return matrix.getEntry(index/matrix.getColumnDimension(), index%matrix.getColumnDimension());
	}
	protected static void setEntryAtIndex(FieldMatrix<Fraction> matrix, int index, Fraction f) {
		matrix.setEntry(index/matrix.getColumnDimension(), index%matrix.getColumnDimension(), f);
	}

	protected static void swapRows(FieldMatrix<Fraction> A , int rowA , int rowB ) {
		int indexA = rowA*A.getColumnDimension();
		int indexB = rowB*A.getColumnDimension();

		for( int i = 0; i < A.getColumnDimension(); i++ , indexA++,indexB++) {
			Fraction temp = getEntryAtIndex(A, indexA);
			setEntryAtIndex(A, indexA, getEntryAtIndex(A, indexB));
			setEntryAtIndex(A, indexB, temp);
		}
	}
}
