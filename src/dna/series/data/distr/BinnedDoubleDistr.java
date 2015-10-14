package dna.series.data.distr;

public class BinnedDoubleDistr extends BinnedDistr<Double> {

	public BinnedDoubleDistr(String name) {
		super(name, 1.0);
	}

	public BinnedDoubleDistr(String name, Double binSize) {
		super(name, binSize);
	}

	public BinnedDoubleDistr(String name, Double binSize, long denominator,
			long[] values) {
		super(name, binSize, denominator, values);
	}

	public BinnedDoubleDistr(String name, String binSize, long denominator,
			long[] values) {
		super(name, Double.parseDouble(binSize), denominator, values);
	}

	@Override
	protected int getIndex(Double value) {
		return (int) Math.ceil(value / this.binSize);
	}

	@Override
	public Double[] getBin(int index) {
		if (index == 0) {
			return new Double[] { 0.0, 0.0 };
		} else {
			return new Double[] { (index - 1) * binSize, index * binSize };
		}
	}

	public DistrType getDistrType() {
		return DistrType.BINNED_DOUBLE;
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
