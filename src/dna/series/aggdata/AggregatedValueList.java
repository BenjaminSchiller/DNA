package dna.series.aggdata;

import java.io.FileNotFoundException;
import java.io.IOException;

import dna.io.Reader;
import dna.io.Writer;
import dna.series.lists.List;
import dna.util.Config;

/**
 * An AggregatedValueList object contains a list of AggregatedValue objects.
 * 
 * @author Rwilmes
 * @date 24.06.2013
 */
public class AggregatedValueList extends List<AggregatedValue> {

	// constructors
	public AggregatedValueList() {
		super();
	}

	public AggregatedValueList(int size) {
		super(size);
	}

	// IO methods
	public void write(String dir, String filename) throws IOException {
		Writer w = Writer.getWriter(dir, filename);

		for (String name : this.map.keySet()) {
			String temp = "";
			for (int i = 0; i < this.map.get(name).getValues().length; i++) {
				temp += Config.get("AGGREGATED_DATA_DELIMITER")
						+ this.map.get(name).getValues()[i];
			}
			w.writeln(name + temp);
		}
		w.close();
	}

	public static AggregatedValueList read(String dir, String filename,
			boolean readValues) throws IOException {
		if (!readValues) {
			return new AggregatedValueList();
		}
		AggregatedValueList list = new AggregatedValueList();

		// try to read values, if no file exists = no values, return empty list
		try {
			Reader r = Reader.getReader(dir, filename);
			String line = null;
			while ((line = r.readString()) != null) {
				String[] temp = line.split(Config
						.get("AGGREGATED_DATA_DELIMITER"));
				double[] values = new double[temp.length - 1];
				for (int i = 1; i < temp.length; i++) {
					values[i - 1] = Double.parseDouble(temp[i]);
				}
				list.add(new AggregatedValue(temp[0], values));
			}
			r.close();
		} catch (FileNotFoundException e) {

		}
		return list;
	}
}
