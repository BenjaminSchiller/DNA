package dna.series.aggdata;

import java.io.IOException;

import dna.io.Reader;
import dna.io.Writer;
import dna.series.lists.List;
import dna.util.Config;

/**
 * An AggregatedRunTimeList object contains aggregated values of a RunTimeList.
 * 
 * @author Rwilmes
 * @date 04.07.2013
 */
public class AggregatedRunTimeList extends List<AggregatedValue> {

	// member variables
	private String name;

	// constructors
	public AggregatedRunTimeList() {
		super();
	}

	public AggregatedRunTimeList(int size) {
		super(size);
	}

	public AggregatedRunTimeList(String name) {
		super();
		this.name = name;
	}

	public AggregatedRunTimeList(String name, int size) {
		super(size);
		this.name = name;
	}

	// methods
	public String getName() {
		return this.name;
	}

	// IO methods
	public void write(String dir, String filename) throws IOException {
		Writer w = Writer.getWriter(dir, filename);

		for (AggregatedValue aggData : this.getList()) {
			String temp = "" + aggData.getName()
					+ Config.get("AGGREGATED_DATA_DELIMITER");
			for (int i = 0; i < aggData.getValues().length; i++) {
				if (i == aggData.getValues().length - 1)
					temp += aggData.getValues()[i];
				else
					temp += aggData.getValues()[i]
							+ Config.get("AGGREGATED_DATA_DELIMITER");
			}
			w.writeln(temp);
		}
		w.close();
	}

	public static AggregatedRunTimeList read(String dir, String filename,
			boolean readValues) throws IOException {
		if (!readValues) {
			return new AggregatedRunTimeList(filename);
		}
		AggregatedRunTimeList list = new AggregatedRunTimeList();
		Reader r = Reader.getReader(dir, filename);
		String line = null;
		while ((line = r.readString()) != null) {
			String[] temp = line.split(Config.get("AGGREGATED_DATA_DELIMITER"));
			double[] values = new double[temp.length - 1];

			for (int i = 1; i < temp.length; i++) {
				values[i - 1] = Double.parseDouble(temp[i]);
			}
			list.add(new AggregatedValue(temp[0], values));
		}
		r.close();
		return list;
	}

}
