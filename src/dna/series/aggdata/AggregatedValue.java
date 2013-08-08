package dna.series.aggdata;

import java.io.IOException;

import dna.io.Reader;
import dna.io.Writer;
import dna.io.etc.Keywords;

/**
 * An AggregatedValue object contains aggregated values.
 * 
 * @author Rwilmes
 * @date 10.06.2013
 */
public class AggregatedValue extends AggregatedData {

	// member variables
	private double[] values;

	// AggregatedValue array structure: { avg, min, max, median, variance,
	// variance-low, variance-up, confidence-low, confidence-up }

	public double getAvg() {
		return this.values[0];
	}

	public double getMin() {
		return this.values[1];
	}

	public double getMax() {
		return this.values[2];
	}

	public double getMedian() {
		return this.values[3];
	}

	public double getVariance() {
		return this.values[4];
	}

	public double getVarianceLow() {
		return this.values[5];
	}

	public double getVarianceUp() {
		return this.values[6];
	}

	public double getConfidenceLow() {
		return this.values[7];
	}

	public double getConfidenceUp() {
		return this.values[8];
	}

	// constructors
	public AggregatedValue(String name) {
		super(name);
	}

	public AggregatedValue(String name, double[] values) {
		super(name);
		this.values = values;
	}

	// get methods
	public double[] getValues() {
		return this.values;
	}

	// IO methods
	/**
	 * @param dir
	 *            String which contains the path to the directory the
	 *            AggregatedValue will be read from.
	 * 
	 * @param filename
	 *            String representing the filename the Distribution will be read
	 *            from.
	 * 
	 * @param readValues
	 *            Boolean. True: values from the file will be read. False: empty
	 *            AggregatedValue will be created.
	 */
	public static AggregatedValue read(String dir, String filename,
			String name, boolean readValues) throws IOException {
		if (!readValues) {
			return new AggregatedValue(name, null);
		}
		Reader r = new Reader(dir, filename);

		String line = null;

		line = r.readString();
		String[] temp = line.split(Keywords.aggregatedDataDelimiter);

		double[] tempDouble = new double[temp.length];
		for (int i = 0; i < tempDouble.length; i++) {
			tempDouble[i] = Double.parseDouble(temp[i]);
		}

		r.close();
		return new AggregatedValue(name, tempDouble);
	}

	public static void write(double[] x, AggregatedValue[] values, String dir,
			String filename) throws IOException {
		Writer w = new Writer(dir, filename);
		for (int i = 0; i < x.length; i++) {
			StringBuffer buff = new StringBuffer(x[i] + "");
			for (int j = 0; j < values[i].getValues().length; j++) {
				buff.append(Keywords.dataDelimiter);
				buff.append(values[i].getValues()[j]);
			}
			w.writeln(buff.toString());
		}
		w.close();
	}

}
