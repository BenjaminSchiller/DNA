package dynamicGraphs.series;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.sun.media.sound.InvalidFormatException;

import dynamicGraphs.io.Keywords;
import dynamicGraphs.io.Reader;
import dynamicGraphs.io.Suffix;
import dynamicGraphs.io.Writer;
import dynamicGraphs.util.SuffixFilenameFilter;

public class Distribution {

	public Distribution(String name, double[] values) {
		this.name = name;
		this.values = values;
	}

	private String name;
	
	public String getName(){
		return this.name;
	}

	public String getFilename() {
		return this.name + Suffix.distribution;
	}

	private double[] values;
	
	public double[] getValues(){
		return this.values;
	}

	public void write(String dir) throws IOException {
		if (this.values == null) {
			throw new NullPointerException("no values for distribution \""
					+ this.name + "\" set to be written to " + dir);
		}
		Writer w = new Writer(dir + this.getFilename());
		for (int i = 0; i < this.values.length; i++) {
			w.writeln(i + Keywords.distributionDelimiter + this.values[i]);
		}
		w.close();
	}

	public static void write(Distribution[] distributions, String dir)
			throws IOException {
		for (Distribution d : distributions) {
			d.write(dir);
		}
	}

	public static Distribution read(String path, boolean readValues)
			throws IOException {
		String name = (new File(path)).getName().replace(Suffix.distribution,
				"");
		if (!readValues) {
			return new Distribution(name, null);
		}
		Reader r = new Reader(path);
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

	public static Distribution[] readDir(String dir, boolean readValues)
			throws IOException {
		File[] files = new File(dir).listFiles(new SuffixFilenameFilter(
				Suffix.distribution));
		Distribution[] distributions = new Distribution[files.length];
		for (int i = 0; i < files.length; i++) {
			distributions[i] = Distribution.read(files[i].getAbsolutePath(),
					readValues);
		}
		return distributions;
	}

}
