package dna.series.aggdata;

import java.io.IOException;
import java.util.ArrayList;

import com.sun.media.sound.InvalidFormatException;

import dna.io.Reader;
import dna.io.Writer;
import dna.io.etc.Keywords;

/**
 * AggregatedNodeValueList is a class containing the values of an aggregated
 * NodeValueList. It contains an array of AggregatedValue objects.
 * AggregatedValue object array structure: { x (diff number), avg, min, max,
 * median, variance, variance-low, variance-up, confidence-low, confidence-up }
 * 
 * @author Rwilmes
 * @date 10.06.2013
 */
public class AggregatedNodeValueList extends AggregatedData {

	// member variables
	private AggregatedValue[] values;

	// constructors
	public AggregatedNodeValueList(String name) {
		super(name);
	}

	public AggregatedNodeValueList(String name, AggregatedValue[] values) {
		super(name);
		this.values = values;
	}

	// get methods
	public AggregatedValue[] getValues() {
		return this.values;
	}

	public String getName() {
		return super.getName();
	}

	// IO methods
	/**
	 * @param dir
	 *            String which contains the path to the directory the
	 *            AggregatedNodeValueList will be read from.
	 * 
	 * @param filename
	 *            String representing the filename the Distribution will be read
	 *            from.
	 * 
	 * @param readValues
	 *            Boolean. True: values from the file will be read. False: empty
	 *            AggregatedNodeValueList will be created.
	 */
	public static AggregatedNodeValueList read(String dir, String filename,
			String name, boolean readValues) throws IOException {
		if (!readValues) {
			return new AggregatedNodeValueList(name);
		}
		Reader r = new Reader(dir, filename);
		ArrayList<AggregatedValue> list = new ArrayList<AggregatedValue>();
		String line = null;
		int index = 0;
		while ((line = r.readString()) != null) {
			String[] temp = line.split(Keywords.aggregatedDataDelimiter);
			if (Integer.parseInt(temp[0]) != index) {
				throw new InvalidFormatException("expected index " + index
						+ " but found " + temp[0] + " @ \"" + line + "\"");
			}
			double[] tempDouble = new double[temp.length];
			for (int i = 0; i < tempDouble.length; i++) {
				tempDouble[i] = Double.parseDouble(temp[i]);
			}

			AggregatedValue tempV = new AggregatedValue(name + temp[0],
					tempDouble);
			list.add(tempV);
			index++;
		}
		AggregatedValue[] values = new AggregatedValue[list.size()];
		for (int i = 0; i < list.size(); i++) {
			values[i] = list.get(i);
		}
		r.close();
		return new AggregatedNodeValueList(name, values);
	}

	public void write(String dir, String filename) throws IOException {
		Writer w = new Writer(dir, filename);
		AggregatedValue[] tempData = this.getValues();

		for (AggregatedValue aggData : tempData) {
			String temp = "" + (int) aggData.getValues()[0]
					+ Keywords.aggregatedDataDelimiter;
			for (int i = 1; i < aggData.getValues().length; i++) {
				if (i == aggData.getValues().length - 1)
					temp += aggData.getValues()[i];
				else
					temp += aggData.getValues()[i]
							+ Keywords.aggregatedDataDelimiter;
			}
			w.writeln(temp);
		}
		w.close();
	}

	public static void write(String dir, String filename, double[][] values)
			throws IOException {
		Writer w = new Writer(dir, filename);

		for (int i = 0; i < values.length; i++) {
			String temp = "";
			for (int j = 0; j < values[i].length; j++) {
				if (j == 0)
					temp += (int) values[i][j]
							+ Keywords.aggregatedDataDelimiter;
				else {
					if (j == values[i].length - 1)
						temp += values[i][j];
					else
						temp += values[i][j] + Keywords.aggregatedDataDelimiter;
				}
			}
			w.writeln(temp);
		}
		w.close();
	}

}
