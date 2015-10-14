package dna.series.data.distr;

public class DoubleDistr extends Distr<Double> {

	public DoubleDistr(String name) {
		super(name);
	}

	public DoubleDistr(String name, long denominator, long[] values) {
		super(name, denominator, values);
	}

	@Override
	protected int getIndex(Double value) {
		return (int) Math.ceil(value);
	}

}
