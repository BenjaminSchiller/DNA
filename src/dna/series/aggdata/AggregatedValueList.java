package dna.series.aggdata;

import java.io.IOException;

import dna.io.Reader;
import dna.io.Writer;
<<<<<<< HEAD
<<<<<<< HEAD
import dna.series.lists.List;
import dna.util.Config;
=======
import dna.io.etc.Keywords;
import dna.series.lists.List;
>>>>>>> Codeupdate 13-06-24
=======
import dna.io.etc.Keywords;
import dna.series.lists.List;
>>>>>>> Codeupdate 13-06-24

/**
 * An AggregatedValueList object contains a list of AggregatedValue objects.
 * 
 * @author Rwilmes
 * @date 24.06.2013
 */
public class AggregatedValueList extends List<AggregatedValue> {
<<<<<<< HEAD

<<<<<<< HEAD
<<<<<<< HEAD
=======
=======
>>>>>>> Codeupdate 13-06-28
	
>>>>>>> Codeupdate 13-06-24
=======
	
>>>>>>> Codeupdate 13-06-24
	// constructors
	public AggregatedValueList() {
		super();
	}

	public AggregatedValueList(int size) {
		super(size);
	}

	// IO methods
	public void write(String dir, String filename) throws IOException {
		Writer w = new Writer(dir, filename);
		for (String name : this.map.keySet()) {
			String temp = "";
<<<<<<< HEAD
<<<<<<< HEAD
			for (int i = 0; i < this.map.get(name).getValues().length; i++) {
				temp += Config.get("AGGREGATED_DATA_DELIMITER")
						+ this.map.get(name).getValues()[i];
=======
			for(int i = 0; i < this.map.get(name).getValues().length; i++) {
				temp += Keywords.aggregatedDataDelimiter + this.map.get(name).getValues()[i];
>>>>>>> Codeupdate 13-06-24
=======
			for(int i = 0; i < this.map.get(name).getValues().length; i++) {
				temp += Keywords.aggregatedDataDelimiter + this.map.get(name).getValues()[i];
>>>>>>> Codeupdate 13-06-24
			}
			w.writeln(name + temp);
		}
		w.close();
	}

<<<<<<< HEAD
<<<<<<< HEAD
	public static AggregatedValueList read(String dir, String filename,
			boolean readValues) throws IOException {
		if (!readValues) {
			return new AggregatedValueList();
		}
=======
	public static AggregatedValueList read(String dir, String filename)
			throws IOException {
>>>>>>> Codeupdate 13-06-24
=======
	public static AggregatedValueList read(String dir, String filename)
			throws IOException {
>>>>>>> Codeupdate 13-06-24
		AggregatedValueList list = new AggregatedValueList();
		Reader r = new Reader(dir, filename);
		String line = null;
		while ((line = r.readString()) != null) {
<<<<<<< HEAD
<<<<<<< HEAD
			String[] temp = line.split(Config.get("AGGREGATED_DATA_DELIMITER"));
			double[] values = new double[temp.length - 1];
			for (int i = 1; i < temp.length; i++) {
				values[i - 1] = Double.parseDouble(temp[i]);
			}
			list.add(new AggregatedValue(temp[0], values));
=======
=======
>>>>>>> Codeupdate 13-06-24
			String[] temp = line.split(Keywords.aggregatedDataDelimiter);
			double[] tempDouble = new double[temp.length];
			for(int i = 0; i < temp.length; i++) {
				tempDouble[i] = Double.parseDouble(temp[i]);
			}
			list.add(new AggregatedValue(filename + temp[0], tempDouble));
<<<<<<< HEAD
>>>>>>> Codeupdate 13-06-24
=======
>>>>>>> Codeupdate 13-06-24
		}
		r.close();
		return list;
	}
}
