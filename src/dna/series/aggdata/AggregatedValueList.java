package dna.series.aggdata;

import java.io.IOException;

import dna.io.Reader;
import dna.io.Writer;
import dna.io.etc.Keywords;
import dna.series.lists.List;

/**
 * An AggregatedValueList object contains a list of AggregatedValue objects.
 * 
 * @author Rwilmes
 * @date 24.06.2013
 */
public class AggregatedValueList extends List<AggregatedValue> {
<<<<<<< HEAD
	
=======

>>>>>>> remotes/beniMaster/master
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
			for(int i = 0; i < this.map.get(name).getValues().length; i++) {
				temp += Keywords.aggregatedDataDelimiter + this.map.get(name).getValues()[i];
=======
			for (int i = 0; i < this.map.get(name).getValues().length; i++) {
				temp += Keywords.aggregatedDataDelimiter
						+ this.map.get(name).getValues()[i];
>>>>>>> remotes/beniMaster/master
			}
			w.writeln(name + temp);
		}
		w.close();
	}

	public static AggregatedValueList read(String dir, String filename)
			throws IOException {
		AggregatedValueList list = new AggregatedValueList();
		Reader r = new Reader(dir, filename);
		String line = null;
		while ((line = r.readString()) != null) {
			String[] temp = line.split(Keywords.aggregatedDataDelimiter);
			double[] tempDouble = new double[temp.length];
<<<<<<< HEAD
			for(int i = 0; i < temp.length; i++) {
=======
			for (int i = 0; i < temp.length; i++) {
>>>>>>> remotes/beniMaster/master
				tempDouble[i] = Double.parseDouble(temp[i]);
			}
			list.add(new AggregatedValue(filename + temp[0], tempDouble));
		}
		r.close();
		return list;
	}
}
