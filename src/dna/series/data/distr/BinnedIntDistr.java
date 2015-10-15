package dna.series.data.distr;

public class BinnedIntDistr extends BinnedDistr<Integer> {

	public BinnedIntDistr(String name) {
		super(name, 1);
	}

	public BinnedIntDistr(String name, Integer binSize) {
		super(name, binSize);
	}

	public BinnedIntDistr(String name, Integer binSize, long[] values,
			long denominator) {
		super(name, binSize, values, denominator);
	}

	public BinnedIntDistr(String name, String binSize) {
		super(name, Integer.parseInt(binSize));
	}

	public BinnedIntDistr(String name, String binSize, long[] values,
			long denominator) {
		super(name, Integer.parseInt(binSize), values, denominator);
	}

	@Override
	protected int getIndex(Integer value) {
		int r = value % this.binSize;
		return ((value - r) / this.binSize + (r == 0 ? 0 : 1));
	}

	@Override
	public Integer[] getBin(int index) {
		if (index == 0) {
			return new Integer[] { 0, 0 };
		} else {
			return new Integer[] { (index - 1) * binSize, index * binSize };
		}
	}

	@Override
	public DistrType getDistrType() {
		return DistrType.BINNED_INT;
	}

	/**
	 * note that this average is computed with each call of this function. also
	 * note that this is NOT the correct and actual average of the values used
	 * to fill this distribution because the max value is assumed for each value
	 * counted in a bin. hence, this value is an upper bound on the actual
	 * value.
	 * 
	 * @return average value of the property reflected by this distribution
	 */
	@Override
	public double computeAverage() {
		return super.computeAverage() * this.binSize;
	}

}
