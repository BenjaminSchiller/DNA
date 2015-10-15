package dna.series.data.distr;


public class QualityIntDistr extends QualityDistr<Integer> {

	public QualityIntDistr(String name) {
		super(name, 1);
	}

	public QualityIntDistr(String name, Integer binSize) {
		super(name, binSize);
	}

	public QualityIntDistr(String name, Integer binSize, double[] values) {
		super(name, binSize, values);
	}

	public QualityIntDistr(String name, String binSize) {
		super(name, Integer.parseInt(binSize));
	}

	public QualityIntDistr(String name, String binSize, double[] values) {
		super(name, Integer.parseInt(binSize), values);
	}

	@Override
	public DistrType getDistrType() {
		return DistrType.QUALITY_INT;
	}
}
