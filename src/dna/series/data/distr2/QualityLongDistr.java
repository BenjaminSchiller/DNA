package dna.series.data.distr2;

public class QualityLongDistr extends QualityDistr<Long> {

	public QualityLongDistr(String name) {
		super(name, 1l);
	}

	public QualityLongDistr(String name, Long binSize) {
		super(name, binSize);
	}

	public QualityLongDistr(String name, Long binSize, double[] values) {
		super(name, binSize, values);
	}

	public QualityLongDistr(String name, String binSize) {
		super(name, Long.parseLong(binSize));
	}

	public QualityLongDistr(String name, String binSize, double[] values) {
		super(name, Long.parseLong(binSize), values);
	}

	@Override
	public DistrType getDistrType() {
		return DistrType.QUALITY_LONG;
	}
}
