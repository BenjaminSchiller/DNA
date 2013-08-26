package dna.series;

import java.io.IOException;

import dna.io.Writer;
import dna.util.Config;

public class Values {
	public Values(double[][] values, String name) {
		this.values = values;
		this.name = name;
	}

	private double[][] values;

	public double[][] getValues() {
		return this.values;
	}

	private String name;

	public String getName() {
		return this.name;
	}

	public String getFilename() {
		return this.name + Config.get("SUFFIX_VALUE");
	}

	public void write(String dir) throws IOException {
		this.write(dir, this.getFilename());
	}

	public void write(String dir, String filename) throws IOException {
		Writer writer = new Writer(dir, filename);
		for (int i = 0; i < this.values.length; i++) {
			StringBuffer buff = new StringBuffer();
			for (int j = 0; j < this.values[i].length; j++) {
				if (j > 0) {
					buff.append(Config.get("DATA_DELIMITER"));
				}
				buff.append(this.values[i][j]);
				if (this.values[i][j] == Double.NaN) {
					continue;
				}
			}
			writer.writeln(buff.toString());
		}
		writer.close();
	}
}
