package dna.series.data;

import java.io.IOException;
import java.util.ArrayList;

import com.sun.media.sound.InvalidFormatException;

import dna.io.Reader;
import dna.io.Writer;
import dna.io.etc.Keywords;
import dna.util.Log;

public class Distribution extends Data {
//public class Distribution implements ListItem {
	public Distribution(String name, double[] values) {
		this.name = name;
		this.values = values;
	}

	public Distribution(String name, double value) {
		Log.warn("Distribution initialized with a single value");
		double[] temp = { value };
		this.values = temp;
	}
	
	public String toString() {
		return "distribution(" + this.name + ")";
	}

	private String name;

	public String getName() {
		return this.name;
	}

	private double[] values;

	public double[] getValues() {
		return this.values;
	}

	public String getType() {
		return "Distribution";
	}
	
	public void write(String dir, String filename) throws IOException {
		if (this.values == null) {
			throw new NullPointerException("no values for distribution \""
					+ this.name + "\" set to be written to " + dir);
		}
		Writer w = new Writer(dir, filename);
		for (int i = 0; i < this.values.length; i++) {
			w.writeln(i + Keywords.distributionDelimiter + this.values[i]);
		}
		w.close();
	}

	public static Distribution read(String dir, String filename, String name,
			boolean readValues) throws IOException {
		if (!readValues) {
			return new Distribution(name, null);
		}
		Reader r = new Reader(dir, filename);
		ArrayList<Double> list = new ArrayList<Double>();
		String line = null;
		int index = 0;
		while ((line = r.readString()) != null) {
			String[] temp = line.split(Keywords.distributionDelimiter);
			if (Integer.parseInt(temp[0]) != index) {
				throw new InvalidFormatException("expected index " + index
						+ " but found " + temp[0] + " @ \"" + line + "\"");
			}
			list.add(Double.parseDouble(temp[1]));
			index++;
		}
		double[] values = new double[list.size()];
		for (int i = 0; i < list.size(); i++) {
			values[i] = list.get(i);
		}
		r.close();
		return new Distribution(name, values);
	}

}
