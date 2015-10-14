package dna.series.data.distr2;

public class QualityDoubleDistr extends QualityDistr<Double> {

	public QualityDoubleDistr(String name) {
		super(name, 1.0);
	}

	public QualityDoubleDistr(String name, Double binSize) {
		super(name, binSize);
	}

	public QualityDoubleDistr(String name, Double binSize, double[] values) {
		super(name, binSize, values);
	}

	public QualityDoubleDistr(String name, String binSize) {
		super(name, Double.parseDouble(binSize));
	}

	public QualityDoubleDistr(String name, String binSize, double[] values) {
		super(name, Double.parseDouble(binSize), values);
	}

}
