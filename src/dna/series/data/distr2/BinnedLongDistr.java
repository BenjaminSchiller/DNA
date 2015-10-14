package dna.series.data.distr2;

public class BinnedLongDistr extends BinnedDistr<Long> {

	public BinnedLongDistr(String name) {
		super(name, 1l);
	}

	public BinnedLongDistr(String name, Long binSize) {
		super(name, binSize);
	}

	public BinnedLongDistr(String name, Long binSize, long[] values, long denominator) {
		super(name, binSize, values, denominator);
	}

	public BinnedLongDistr(String name, String binSize) {
		super(name, Long.parseLong(binSize));
	}

	public BinnedLongDistr(String name, String binSize, long[] values,
			long denominator) {
		super(name, Long.parseLong(binSize), values, denominator);
	}

	@Override
	protected int getIndex(Long value) {
		long r = value % this.binSize;
		return (int) ((value - r) / this.binSize + (r == 0 ? 0 : 1));
	}

	@Override
	public Long[] getBin(int index) {
		if (index == 0) {
			return new Long[] { 0l, 0l };
		} else {
			return new Long[] { (index - 1) * binSize, index * binSize };
		}
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
