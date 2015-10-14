package dna.series.data.distr;

public class LongDistr extends Distr<Long> {

	public LongDistr(String name) {
		super(name);
	}

	public LongDistr(String name, long denominator, long[] values) {
		super(name, denominator, values);
	}

	@Override
	protected int getIndex(Long value) {
		return (int) (value + 0);
	}

	public DistrType getDistrType() {
		return DistrType.LONG;
	}

}
